package com.vincent.funvideo.db.pojo;

import java.io.Serializable;
import lombok.Data;

/**
 * user
 * @author 
 */
@Data
public class User implements Serializable {
    private Integer id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 头像
     */
    private String face;

    /**
     * 粉丝数
     */
    private Long fans;

    /**
     * 金币
     */
    private  Integer coin;

    private static final long serialVersionUID = 1L;
}