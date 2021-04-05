package com.vincent.funvideo.util;

import lombok.Data;
@Data
public class VideoException extends

        RuntimeException {
    private String msg;
    private int code = 500;

    public VideoException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public VideoException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public VideoException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public VideoException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

}
