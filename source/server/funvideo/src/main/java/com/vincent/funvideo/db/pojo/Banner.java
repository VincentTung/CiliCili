package com.vincent.funvideo.db.pojo;

import java.io.Serializable;
import lombok.Data;

/**
 * banner
 * @author 
 */
@Data
public class Banner implements Serializable {
    private Integer id;

    private Integer sticky;

    private String type;

    private String title;

    private String subtitle;

    private String url;

    private String cover;

    private String createtime;

    private static final long serialVersionUID = 1L;
}