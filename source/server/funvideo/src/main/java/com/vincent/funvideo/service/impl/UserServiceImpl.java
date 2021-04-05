package com.vincent.funvideo.service.impl;

import com.vincent.funvideo.db.dao.UserDao;
import com.vincent.funvideo.db.pojo.User;
import com.vincent.funvideo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


@Service
@Scope("prototype")
@Slf4j
class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Override
    public User searchUserInfo(int uid) {
        return userDao.selectById(uid);
    }
}