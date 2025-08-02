package com.vincent.funvideo.util;

import com.vincent.funvideo.controller.form.VideoForm;

public class CommonUtil {


    public static int getStart(int page, int length) {

        if(page <=0 ) page = 1;
        if(length<=0) length = 10;
        return (page - 1) * length;
    }
    public  static int getStart(VideoForm form){
        return getStart(form.getPageIndex(),form.getPageSize());
    }
}
