/**
 * WebSocket 聊天消息类
 */
package com.vincent.funvideo.websocket;

import com.alibaba.fastjson.JSON;
import org.springframework.context.annotation.Bean;

/**
 * WebSocket 聊天消息类
 */
public class Message {

    public static final String ENTER = "ENTER";
    public static final String SPEAK = "SPEAK";
    public static final String QUIT = "QUIT";

    
    private String type;//消息类型

    private String username; //发送人

    private String msg; //发送消息

    private int onlineCount; //在线用户数

    private long date; // 消息发送时间戳，单位毫秒

    private Integer uid; // 发送者用户ID
    private boolean self; // 是否为自己发送

    public static String jsonStr(String type, String username, String msg, int onlineTotal, long date) {
        return JSON.toJSONString(new Message(type, username, msg, onlineTotal, date));
    }

    public static String jsonStr(String type, String username, String msg, int onlineTotal) {
        return JSON.toJSONString(new Message(type, username, msg, onlineTotal));
    }

    public Message(String type, String username, String msg, int onlineCount, long date) {
        this.type = type;
        this.username = username;
        this.msg = msg;
        this.onlineCount = onlineCount;
        this.date = date;
    }

    public Message(String type, String username, String msg, int onlineCount) {
        this(type, username, msg, onlineCount, System.currentTimeMillis());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getOnlineCount() {
        return onlineCount;
    }

    public void setOnlineCount(int onlineCount) {
        this.onlineCount = onlineCount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public boolean isSelf() {
        return self;
    }

    public void setSelf(boolean self) {
        this.self = self;
    }
}
