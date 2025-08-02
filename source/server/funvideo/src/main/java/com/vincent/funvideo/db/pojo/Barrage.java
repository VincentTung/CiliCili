package com.vincent.funvideo.db.pojo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class Barrage implements Serializable {
    private Long id;
    private Integer videoId;
    private String username;
    private String msg;
    private Date sendTime;
    private Integer uid;
    private static final long serialVersionUID = 1L;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }
} 