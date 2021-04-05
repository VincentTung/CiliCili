package com.vincent.funvideo.db.dao;

import com.vincent.funvideo.db.pojo.Type;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TypeDao {
    int insert(Type record);

    Type selectById(Integer id);

    List<Type> searchAllTypes();


}