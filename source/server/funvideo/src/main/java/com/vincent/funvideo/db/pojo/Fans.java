package com.vincent.funvideo.db.pojo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * fans
 * @author 
 */
@Data
public class Fans implements Serializable {
    private Integer id;

    private Integer uid;

    /**
     * 关注的up主id
     */
    private Integer uper;

    /**
     * 1 关注 0取关
     */
    private Integer state;

    /**
     * 时间
     */
    private Date time;

    private static final long serialVersionUID = 1L;
}