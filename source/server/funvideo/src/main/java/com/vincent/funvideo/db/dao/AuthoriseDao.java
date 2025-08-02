package com.vincent.funvideo.db.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;

@Mapper
public interface AuthoriseDao {
    HashMap searchByPwd(String   name);
}