package com.vincent.funvideo.db.pojo;

import java.io.Serializable;
import lombok.Data;

/**
 * record
 * @author 
 */
@Data
public class Record implements Serializable {
    private Integer id;

    /**
     * 用户
     */
    private Integer uid;

    /**
     * 视频
     */
    private Integer vid;

    /**
     * 0无操作 1点赞 2不喜欢
     */
    private Integer like;

    /**
     * 0默认 1收藏
     */
    private Integer collect;




    private static final long serialVersionUID = 1L;
}