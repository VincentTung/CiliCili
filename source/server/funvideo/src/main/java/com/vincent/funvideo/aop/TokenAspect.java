package com.vincent.funvideo.aop;

import cn.hutool.core.util.StrUtil;

import com.vincent.funvideo.config.shiro.ThreadLocalToken;
import com.vincent.funvideo.util.R;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class TokenAspect {
    @Autowired
    private ThreadLocalToken threadLocalToken;


    @Pointcut("execution(public  * com.vincent.funvideo.controller.*.*(..) ))")
    public void aspect(){

    }

    @Around("aspect()")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        R r = (R)point.proceed();
        String token = threadLocalToken.getToken();
        if(!StrUtil.isBlank(token)){
            r.put("token",token);
        }
        return r;
    }
}
