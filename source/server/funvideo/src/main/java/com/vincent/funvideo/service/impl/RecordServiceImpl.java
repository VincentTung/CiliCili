package com.vincent.funvideo.service.impl;

import com.vincent.funvideo.db.dao.FansDao;
import com.vincent.funvideo.db.dao.RecordDao;
import com.vincent.funvideo.db.dao.VideoDao;
import com.vincent.funvideo.db.pojo.Fans;
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

    @Autowired
    private FansDao fansDao;
    @Autowired
    private VideoDao videoDao;
    public static final int LIKE = 1;
    public static final int CANCEL_LIKE = 0;
    public static final int HATE = 2;


    private static final int CANCEL_COLLECT = 0;
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
        int result;
        params.put("like", LIKE);
        Record record = recordDao.searchByVid(params);
        if (record != null) {
            result = recordDao.updateLike(params);
            if (record.getLike() != LIKE && result != 0) {
                videoDao.plusLikeCount(vid);
            }
        } else {
            result = recordDao.insert(getRecord(params));
        }

        if (result != 0) {
            videoDao.plusLikeCount(vid);
        }
        return result;
    }

    @Override
    public int cancelLikeVideo(int uid, int vid) {

        HashMap params = getParams(uid, vid);
        params.put("like", CANCEL_LIKE);
        int result;
        Record record = recordDao.searchByVid(params);
        if (record != null) {
            result = recordDao.updateLike(params);
            if (record.getLike() == LIKE && result != 0) {
                videoDao.minusLikeCount(vid);
            }
        } else {
            result = recordDao.insert(getRecord(params));
        }
        return result;
    }

    @Override
    public int hateVideo(int uid, int vid) {
        HashMap params = getParams(uid, vid);
        params.put("like", HATE);
        int result;
        Record record = recordDao.searchByVid(params);
        if (record != null) {
            result = recordDao.updateLike(params);
            if (record.getLike() == LIKE && result != 0) {
                videoDao.minusLikeCount(vid);
            }
        } else {
            result = recordDao.insert(getRecord(params));
        }
        return result;
    }

    @Override
    public int collectVideo(int uid, int vid) {
        HashMap params = getParams(uid, vid);
        params.put("collect", COLLECT);
        int result;
        Record record = recordDao.searchByVid(params);
        if (record != null) {
            result = recordDao.updateCollect(params);
            if (record.getCollect() == CANCEL_COLLECT && result != 0) {
                videoDao.plusCollectCount(vid);
            }
        } else {
            result = recordDao.insert(getRecord(params));
        }
        return result;
    }

    @Override
    public int cancelCollectVideo(int uid, int vid) {
        HashMap params = getParams(uid, vid);
        params.put("collect", CANCEL_COLLECT);
        int result;
        Record record = recordDao.searchByVid(params);
        if (record != null) {
            result = recordDao.updateCollect(params);

            if (record.getCollect() == COLLECT && result != 0) {
                videoDao.minusCollectCount(vid);
            }
        } else {
            result = recordDao.insert(getRecord(params));
        }
        return result;
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
    public boolean isVideoLike(int uid, int id) {
        HashMap params = new HashMap();
        params.put("uid", uid);
        params.put("id", id);

        return recordDao.isVideoLike(params) == null ? false : true;
    }

    @Override
    public Integer getLikeState(int uid, int id) {
        HashMap params = new HashMap();
        params.put("uid", uid);
        params.put("id", id);

        return recordDao.getLikeSate(params);
    }

    @Override
    public boolean isVideoCollect(int uid, int id) {
        HashMap params = new HashMap();
        params.put("uid", uid);
        params.put("id", id);

        return recordDao.isVideoCollect(params) == null ? false : true;
    }

    @Override
    public Record searRecord(int uid, int id) {
        HashMap params = new HashMap();
        params.put("uid", uid);
        params.put("vid", id);
        return recordDao.searchByVid(params);
    }

    @Override
    public int coinVideo(int uid, int vid) {

        HashMap params = new HashMap();
        params.put("uid", uid);
        params.put("vid", vid);
        int result;
        if (recordDao.searchByVid(params) != null) {
            result = recordDao.coinVideo(params);
        } else {
            Record record = new Record();
            record.setUid(uid);
            record.setVid(vid);
            record.setCoin(1);
            record.setLike(0);
            record.setCollect(0);
            result = recordDao.insert(record);
        }

        if (result != 0) {
            videoDao.coinVideo(vid);
        }
        return result;

    }

    @Override
    public List<HashMap> getCoinList(int uid) {
        return recordDao.getCoinList(uid);
    }

    @Override
    public List<HashMap> getFanList(int uid) {
        return fansDao.getFansList(uid);
    }

    @Override
    public List<HashMap> getFocusList(int uid) {
        return fansDao.getFocusList(uid);
    }


    @Override
    public int cancelFocusUper(int uid, int uper) {
        HashMap params = new HashMap();
        params.put("uid", uid);
        params.put("uper", uper);
        params.put("state", 0);
        return fansDao.updateFocus(params);
    }

    @Override
    public int focusUpper(int uid, int uper) {
        // 先查是否有这条关注关系（不管state）
        Integer exists = fansDao.searchFocusAnyState(uid, uper); // 新增方法
        HashMap params = new HashMap();
        params.put("uid", uid);
        params.put("uper", uper);
        if (exists != null) {
            params.put("state", 1);
            return fansDao.updateFocus(params);
        } else {
            Fans fans = new Fans();
            fans.setUid(uid);
            fans.setState(1);
            fans.setUper(uper);
            return fansDao.insert(fans);
        }
    }

    @Override
    public Integer getFocus(int uid, int uper) {
        return fansDao.searchFocus(uid,uper);
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
        record.setCoin(getIntValue(params, "coin"));

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
