package com.vincent.funvideo.db.pojo;

import java.io.Serializable;
import lombok.Data;

/**
 * type
 * @author 
 */
@Data
public class Type implements Serializable {
    private Integer id;

    private String name;

    private static final long serialVersionUID = 1L;
}