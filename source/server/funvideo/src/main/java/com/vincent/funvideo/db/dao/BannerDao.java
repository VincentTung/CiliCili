package com.vincent.funvideo.db.dao;


import com.vincent.funvideo.db.pojo.Banner;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface BannerDao {

    List<Banner> getAllBanners();
}