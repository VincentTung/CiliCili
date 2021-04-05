package com.vincent.funvideo.service.impl;

import com.vincent.funvideo.db.dao.RecordDao;
import com.vincent.funvideo.db.pojo.Record;
import com.vincent.funvideo.service.RecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@Scope("prototype")
@Slf4j
public class RecordServiceImpl implements RecordService {

    @Autowired
    private RecordDao recordDao;

    private static final int LIKE = 1;
    private static final int CANCEL_LIKE = 0;
    private static final int HATE = 1;


    private static final int CANCEL_COLLECT = 1;
    private static final int COLLECT = 1;

    @Override
    public int viewVideo(int uid, int vid) {
        HashMap params = getParams(uid, vid);
        if (hasRecord(params)) {
            return recordDao.updateView(params);
        } else {
            return recordDao.insert(getRecord(params));
        }
    }

    @Override
    public int likeVideo(int uid, int vid) {

        HashMap params = getParams(uid, vid);
        params.put("like", LIKE);
        if (hasRecord(params)) {
            return recordDao.updateLike(params);
        } else {
            return recordDao.insert(getRecord(params));
        }

    }

    @Override
    public int cancelLikeVideo(int uid, int vid) {

        HashMap params = getParams(uid, vid);
        params.put("like", CANCEL_LIKE);
        if (hasRecord(params)) {
            return recordDao.updateLike(params);
        } else {
            return recordDao.insert(getRecord(params));
        }
    }

    @Override
    public int hateVideo(int uid, int vid) {
        HashMap params = getParams(uid, vid);
        params.put("like", HATE);
        if (hasRecord(params)) {
            return recordDao.updateLike(params);
        } else {
            return recordDao.insert(getRecord(params));
        }
    }

    @Override
    public int collectVideo(int uid, int vid) {
        HashMap params = getParams(uid, vid);
        params.put("collect", COLLECT);
        if (hasRecord(params)) {
            return recordDao.updateCollect(params);
        } else {
            return recordDao.insert(getRecord(params));
        }
    }

    @Override
    public int cancelCollectVideo(int uid, int vid) {
        HashMap params = getParams(uid, vid);
        params.put("collect", CANCEL_COLLECT);
        if (hasRecord(params)) {
            return recordDao.updateCollect(params);
        } else {
            return recordDao.insert(getRecord(params));
        }
    }

    @Override
    public List<HashMap> getCollectList(int uid) {
        return recordDao.getCollectList(uid);
    }

    @Override
    public List<HashMap> getViewList(int uid) {
        return recordDao.getViewList(uid);

    }

    @Override
    public List<HashMap> getLikeList(int uid) {
        return recordDao.getLikeList(uid);

    }

    @Override
    public boolean isVideoLike(int uid, Integer id) {
        HashMap params = new HashMap();
        params.put("uid", uid);
        params.put("id", id);

        return recordDao.isVideoLike(params) == null ? false : true;
    }

    @Override
    public boolean isVideoCollect(int uid, Integer id) {
        HashMap params = new HashMap();
        params.put("uid", uid);
        params.put("id", id);

        return recordDao.isVideoCollect(params) == null ? false : true;
    }

    private HashMap getParams(int uid, int vid) {

        HashMap params = new HashMap();
        params.put("uid", uid);
        params.put("vid", vid);
        return params;
    }

    private boolean hasRecord(HashMap params) {
        return recordDao.searchByVid(params) != null;
    }

    private Record getRecord(HashMap params) {

        Record record = new Record();
        record.setLike(getIntValue(params, "like"));
        record.setCollect(getIntValue(params, "collect"));
        record.setVid((Integer) params.get("vid"));
        record.setUid((Integer) params.get("uid"));

        return record;
    }

    private int getIntValue(HashMap params, String key) {
        Object object = params.get(key);
        if (object != null) {
            return (int) object;
        } else {
            return 0;
        }
    }
}
