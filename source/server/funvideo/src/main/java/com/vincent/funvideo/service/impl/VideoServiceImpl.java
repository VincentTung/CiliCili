package com.vincent.funvideo.service.impl;

import cn.hutool.db.handler.HandleHelper;
import com.vincent.funvideo.db.dao.VideoDao;
import com.vincent.funvideo.db.pojo.Video;
import com.vincent.funvideo.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@Scope("prototype")
@Slf4j
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideoDao videoDao;

    @Override
    public List<HashMap> getHotRank(int start, int length) {
        HashMap params = new HashMap();

        params.put("start",getRealStart(start,length));
        params.put("length", length);
        return videoDao.searchHotRank(params);
    }

    @Override
    public List<HashMap> getNewRank(int start, int length) {
        HashMap params = new HashMap();

        params.put("start",getRealStart(start,length));
        params.put("length", length);
        return videoDao.searchNewRank(params);
    }

    @Override
    public List<HashMap> getCollectRank(int start, int length) {
        HashMap params = new HashMap();

        params.put("start",getRealStart(start,length));
        params.put("length", length);
        return videoDao.searchCollectRank(params);
    }

    @Override
    public List<HashMap> getListByCategory(String category, int start, int length) {
        HashMap params = new HashMap();

        params.put("start",getRealStart(start,length));
        params.put("length",length);
        params.put("category",category);
        return videoDao.searchByCategory(params);
    }

    @Override
    public HashMap getVideoDetail(int id) {
        return videoDao.searchById(id);
    }

    @Override
    public List<HashMap> getRecommendVideo(int start, int length) {
        HashMap params = new HashMap();
        params.put("start",getRealStart(start,length));
        params.put("length",length);
        return videoDao.getRecommend(params);
    }

    @Override
    public void viewVideo(int id) {
        videoDao.viewVideo(id);
    }

    @Override
    public List<HashMap> searchVideoByWord(String keyword) {
        return videoDao.searchVideoByWord(keyword);
    }


    private int getRealStart(int start,int length){
        return   start;
    }
}
