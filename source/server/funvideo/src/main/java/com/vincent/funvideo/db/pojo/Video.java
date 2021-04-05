package com.vincent.funvideo.db.pojo;

import java.io.Serializable;
import lombok.Data;

/**
 * video
 * @author 
 */
@Data
public class Video implements Serializable {
    private Integer id;

    private String vid;

    private String title;

    private String tname;

    private String url;

    private String cover;

    private Integer pubdate;

    private String desc;

    private Integer view;

    private Integer duration;

    private String owner;

    private Integer reply;

    private Integer favorite;

    private Integer like;

    private Integer coin;

    private Integer share;

    private String createtime;

    private Integer size;

    private static final long serialVersionUID = 1L;
}