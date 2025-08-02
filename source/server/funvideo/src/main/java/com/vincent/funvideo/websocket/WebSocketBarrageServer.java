package com.vincent.funvideo.websocket;

import com.alibaba.fastjson.JSON;
import com.vincent.funvideo.service.BarrageService;
import com.vincent.funvideo.db.pojo.Barrage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import com.vincent.funvideo.util.SpringContextUtils;
import com.vincent.funvideo.config.shiro.JwtUtil;


/**
 * websocket连接地址
 * 本机：
 * ws://localhost:998//funvideo-api/video/barrage
 *
 *
 */
@Component
@ServerEndpoint("/video/barrage/{id}/{token}")//标记此类为服务端
public class WebSocketBarrageServer {


    private static AtomicInteger onlineCount = new AtomicInteger(0);
    /**
     * 全部在线会话  PS: 基于场景考虑 这里使用线程安全的Map存储会话对象。
     */
    private static Map<String, Map<String, Session>> videoSessions = new ConcurrentHashMap<>();

    // 获取BarrageService的静态方法
    private BarrageService getBarrageService() {
        return SpringContextUtils.getBean(BarrageService.class);
    }

    /**
     * 当客户端打开连接：1.添加会话对象 2.更新在线人数
     */ 
    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id, @PathParam("token") String token) {
        // 解析uid并存入session属性
        JwtUtil jwtUtil = SpringContextUtils.getBean(JwtUtil.class);
        int uid = jwtUtil.getUserId(token);
        session.getUserProperties().put("uid", uid);

        videoSessions.putIfAbsent(id, new ConcurrentHashMap<>());
        videoSessions.get(id).put(session.getId(), session);
        // 查询历史弹幕（如最近100条，按时间升序）
        List<Barrage> history = getBarrageService().getHistory(Integer.parseInt(id), 100);
        for (Barrage barrage : history) {
            long date = barrage.getSendTime() != null ? barrage.getSendTime().getTime() : System.currentTimeMillis();
            Integer barrageUid = barrage.getUid();
            boolean self = barrageUid != null && barrageUid.equals(uid);
            Message msgObj = new Message(Message.SPEAK, barrage.getUsername(), barrage.getMsg(), videoSessions.get(id).size(), date);
            msgObj.setUid(barrageUid);
            msgObj.setSelf(self);
            String msg = JSON.toJSONString(msgObj);
            try {
                session.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("onOpen____"+id+"___"+token);
       
        onlineCount.incrementAndGet();
        String s = session.getNegotiatedSubprotocol();
        System.out.println(s);
    }

    /**
     * 当客户端发送消息：1.获取它的用户名和消息 2.发送消息给所有人
     * <p>
     * PS: 这里约定传递的消息为JSON字符串 方便传递更多参数！
     */
    @OnMessage
    public void onMessage(Session session, String jsonStr, @PathParam("id") String id) {
        Message message = JSON.parseObject(jsonStr, Message.class);
        Integer senderUid = (Integer) session.getUserProperties().get("uid");
        message.setUid(senderUid);
        // 1. 保存弹幕到数据库
        getBarrageService().saveBarrage(Integer.parseInt(id), message.getUsername(), message.getMsg(), senderUid);
        // 2. 广播给同视频用户，date字段为当前时间，uid和self字段
        broadcastMessageToVideo(id, message);
    }

    /**
     * 当关闭连接：1.移除会话对象 2.更新在线人数
     */
    @OnClose
    public void onClose(Session session, @PathParam("id") String id) {
        Map<String, Session> sessions = videoSessions.get(id);
        if (sessions != null) {
            sessions.remove(session.getId());
            if (sessions.isEmpty()) {
                videoSessions.remove(id);
            }
        }
    }

    /**
     * 当通信发生异常：打印错误日志
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    /**
     * 公共方法：发送信息给所有人
     */
    private static void sendMessageToVideo(String videoId, String msg) {
        Map<String, Session> sessions = videoSessions.get(videoId);
        if (sessions != null) {
            sessions.forEach((id, session) -> {
                try {
                    session.getBasicRemote().sendText(msg);
                    System.out.println("发送："+msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    // 新增：广播Message对象，带self字段
    private static void broadcastMessageToVideo(String videoId, Message message) {
        Map<String, Session> sessions = videoSessions.get(videoId);
        if (sessions != null) {
            long now = System.currentTimeMillis();
            for (Session s : sessions.values()) {
                Integer sessionUid = (Integer) s.getUserProperties().get("uid");
                message.setSelf(sessionUid != null && sessionUid.equals(message.getUid()));
                message.setDate(now);
                String msg = JSON.toJSONString(message);
                try {
                    s.getBasicRemote().sendText(msg);
                    System.out.println("发送："+msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}