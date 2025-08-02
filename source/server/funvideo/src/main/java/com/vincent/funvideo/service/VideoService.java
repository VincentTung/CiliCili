package com.vincent.funvideo.service;

import com.vincent.funvideo.db.pojo.Video;
import org.apache.shiro.crypto.hash.Hash;

import java.util.HashMap;
import java.util.List;

public interface VideoService {

    List<HashMap> getHotRank(int start,int length);

    List<HashMap> getNewRank(int start,int length);

    List<HashMap> getCollectRank(int start,int length);

    List<HashMap> getListByCategory(String category,int start,int length);

    HashMap getVideoDetail(int id);

    List<HashMap> getRecommendVideo(int pageIndex, int pageSize);

    void viewVideo(int id);

    List<HashMap> searchVideoByWord(String keyword);
}
