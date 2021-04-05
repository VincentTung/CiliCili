package com.vincent.funvideo.controller;

import com.vincent.funvideo.config.shiro.JwtUtil;
import com.vincent.funvideo.controller.form.BaseForm;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseController {

    @Autowired
    protected JwtUtil jwtUtil;


    protected void checkApiKey(BaseForm baseForm) {
        jwtUtil.checkApiKey(baseForm.getApikey());
    }

}
