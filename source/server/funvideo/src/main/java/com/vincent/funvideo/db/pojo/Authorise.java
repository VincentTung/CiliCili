package com.vincent.funvideo.db.pojo;

import java.io.Serializable;
import lombok.Data;

/**
 * authorise
 * @author 
 */
@Data
public class Authorise implements Serializable {
    private Integer id;

    private String name;

    private String pwd;

    private Integer uid;

    private static final long serialVersionUID = 1L;
}