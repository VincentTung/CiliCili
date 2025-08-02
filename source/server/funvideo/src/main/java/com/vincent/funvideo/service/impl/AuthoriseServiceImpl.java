package com.vincent.funvideo.service.impl;

import com.vincent.funvideo.db.dao.AuthoriseDao;
import com.vincent.funvideo.service.AuthoriseService;
import com.vincent.funvideo.util.PasswordUtil;
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
        HashMap result = authoriseDao.searchByPwd(name);

        if(result!= null && result.containsKey("pwd")){

            if(result.get("pwd").equals(pwd)){
                return (Integer) result.get("uid");
            }
        }
        return null;
    }
    
    @Override
    public Integer searchByEncryptedPwd(String name, String encryptedPwd, String encryptType) {
        HashMap result = authoriseDao.searchByPwd(name);
        
        if (result != null && result.containsKey("pwd")) {
            String storedPassword = (String) result.get("pwd");
            
            // 根据加密类型验证密码
            if ("SHA256".equalsIgnoreCase(encryptType)) {
                // 如果数据库存储的是明文，需要先加密再比较
                String encryptedStoredPwd = PasswordUtil.sha256Encrypt(storedPassword);
                if (encryptedPwd.equalsIgnoreCase(encryptedStoredPwd)) {
                    return (Integer) result.get("uid");
                }
            } else {
                // 默认使用MD5
                String encryptedStoredPwd = PasswordUtil.md5Encrypt(storedPassword);
                if (encryptedPwd.equalsIgnoreCase(encryptedStoredPwd)) {
                    return (Integer) result.get("uid");
                }
            }
        }
        return null;
    }
}
