package com.vincent.funvideo.service;

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

    boolean isVideoLike(int uid, Integer id);

    boolean isVideoCollect(int uid, Integer id);
}
