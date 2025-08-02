package com.vincent.funvideo.service;

import com.vincent.funvideo.db.pojo.Record;
import org.apache.shiro.crypto.hash.Hash;

import java.util.HashMap;
import java.util.List;

public interface RecordService {


    int viewVideo(int uid,int vid);
    int likeVideo(int uid, int vid);

    int cancelLikeVideo(int uid, int vid);

    int hateVideo(int uid, int vid);

    int collectVideo(int uid, int vid);

    int cancelCollectVideo(int uid, int vid);

    List<HashMap> getCollectList(int uid);

    List<HashMap> getViewList(int uid);

    List<HashMap> getLikeList(int uid);

    boolean isVideoLike(int uid, int id);
    Integer getLikeState(int uid,int id);
    boolean isVideoCollect(int uid, int id);

    Record searRecord(int uid, int id);

    int  coinVideo(int uid, int vid);

    List<HashMap> getCoinList(int uid);

    List<HashMap> getFanList(int uid);

    List<HashMap> getFocusList(int uid);

    int focusUpper(int uid, int uper);

    Integer getFocus(int uid,int uper);

    int cancelFocusUper(int uid, int uper);
}
