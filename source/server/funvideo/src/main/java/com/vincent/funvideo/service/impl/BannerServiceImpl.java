package com.vincent.funvideo.service.impl;

import com.vincent.funvideo.db.dao.BannerDao;
import com.vincent.funvideo.db.pojo.Banner;
import com.vincent.funvideo.service.BannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Scope("prototype")
@Slf4j
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerDao bannerDao;
    @Override
    public List<Banner> getBanners() {
        return bannerDao.getAllBanners();
    }
}
