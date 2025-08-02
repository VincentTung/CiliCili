package com.vincent.funvideo.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码加密工具类
 */
public class PasswordUtil {
    
    /**
     * MD5加密
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String md5Encrypt(String password) {
        if (password == null || password.isEmpty()) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }
    
    /**
     * SHA256加密
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String sha256Encrypt(String password) {
        if (password == null || password.isEmpty()) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA256加密失败", e);
        }
    }
    
    /**
     * 验证密码（支持MD5和SHA256）
     * @param inputPassword 用户输入的密码（已加密）
     * @param storedPassword 数据库中存储的密码
     * @return 是否匹配
     */
    public static boolean verifyPassword(String inputPassword, String storedPassword) {
        if (inputPassword == null || storedPassword == null) {
            return false;
        }
        return inputPassword.equalsIgnoreCase(storedPassword);
    }
} 