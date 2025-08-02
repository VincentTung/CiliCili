package com.vincent.funvideo.db.dao;

import com.vincent.funvideo.db.pojo.Barrage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BarrageDao {
    int insertBarrage(Barrage barrage);
    List<Barrage> selectHistory(@Param("videoId") int videoId, @Param("limit") int limit);
} 