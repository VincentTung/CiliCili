package com.vincent.funvideo.service.impl;

import com.vincent.funvideo.db.dao.AuthoriseDao;
import com.vincent.funvideo.service.AuthoriseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Scope("prototype")
@Slf4j
public class AuthoriseServiceImpl implements AuthoriseService {
    @Autowired
    private AuthoriseDao authoriseDao;
    @Override
    public Integer searchByPwd(String name, String pwd) {
        HashMap params=new HashMap();
        params.put("name",name);
        params.put("pwd",pwd);
        return authoriseDao.searchByPwd(params);
    }
}
