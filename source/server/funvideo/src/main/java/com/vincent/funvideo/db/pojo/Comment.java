package com.vincent.funvideo.db.pojo;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class Comment implements Serializable {
    private Long id;
    private Integer videoId;
    private Integer userId;
    private String username;
    private String content;
    private Date createTime;
    private Long parentId;
    private String avatar;
} 