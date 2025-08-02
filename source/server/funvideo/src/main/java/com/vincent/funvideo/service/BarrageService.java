package com.vincent.funvideo.service;

import com.vincent.funvideo.db.dao.BarrageDao;
import com.vincent.funvideo.db.pojo.Barrage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BarrageService {
    @Autowired
    private BarrageDao barrageDao;

    public void saveBarrage(int videoId, String username, String msg, Integer uid) {
        Barrage barrage = new Barrage();
        barrage.setVideoId(videoId);
        barrage.setUsername(username);
        barrage.setMsg(msg);
        barrage.setUid(uid);
        barrageDao.insertBarrage(barrage);
    }

    public List<Barrage> getHistory(int videoId, int limit) {
        return barrageDao.selectHistory(videoId, limit);
    }
} 