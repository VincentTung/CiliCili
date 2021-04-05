package com.vincent.funvideo.service;

import com.vincent.funvideo.db.pojo.Video;

import java.util.HashMap;
import java.util.List;

public interface VideoService {

    List<HashMap> getHotRank(int start,int length);

    List<HashMap> getNewRank(int start,int length);

    List<HashMap> getCollectRank(int start,int length);

    List<HashMap> getListByCategory(String category,int start,int length);

    HashMap getVideoDetail(Integer id);

    List<HashMap> getRecommendVideo(Integer pageIndex, Integer pageSize);
}
