package com.vincent.android.cili.util

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object HashUtil {
    
    /**
     * 对字符串进行MD5加密
     * @param input 需要加密的字符串
     * @return MD5加密后的字符串，如果加密失败返回null
     */
    fun md5(input: String): String? {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val digest = md.digest(input.toByteArray())
            val hexString = StringBuilder()
            
            for (b in digest) {
                val hex = Integer.toHexString(0xff and b.toInt())
                if (hex.length == 1) {
                    hexString.append('0')
                }
                hexString.append(hex)
            }
            
            hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 对字符串进行MD5加密（大写）
     * @param input 需要加密的字符串
     * @return MD5加密后的字符串（大写），如果加密失败返回null
     */
    fun md5UpperCase(input: String): String? {
        return md5(input)?.uppercase()
    }
} 