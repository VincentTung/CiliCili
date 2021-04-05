package com.vincent.funvideo.db.dao;

import com.vincent.funvideo.db.pojo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;

@Mapper
public interface UserDao {

    User selectById(int uid);

    HashMap searchUserDetailById(int uid);
}