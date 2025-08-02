package com.vincent.funvideo.service;

public interface AuthoriseService {

    Integer searchByPwd(String name,String pwd);
    
    /**
     * 验证加密密码
     * @param name 用户名
     * @param encryptedPwd 加密后的密码
     * @param encryptType 加密类型
     * @return 用户ID，验证失败返回null
     */
    Integer searchByEncryptedPwd(String name, String encryptedPwd, String encryptType);
}
