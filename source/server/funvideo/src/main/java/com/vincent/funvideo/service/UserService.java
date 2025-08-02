package com.vincent.funvideo.service;

import com.vincent.funvideo.db.pojo.User;

import java.util.HashMap;

public interface UserService {

    User searchUserInfo(int uid);

    void minusCoin(int uid);
}
