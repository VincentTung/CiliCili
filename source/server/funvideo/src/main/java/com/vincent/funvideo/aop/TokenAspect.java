package com.vincent.funvideo.aop;

import cn.hutool.core.util.StrUtil;

import com.vincent.funvideo.config.shiro.ThreadLocalToken;
import com.vincent.funvideo.util.R;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
        Object result = point.proceed();
        String token = threadLocalToken.getToken();
        if (result instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
            Object body = responseEntity.getBody();
            if (body instanceof R) {
                R r = (R) body;
                if (!StrUtil.isBlank(token)) {
                    r.put("token", token);
                }
                // 返回新的ResponseEntity，保持原有header和status
                return ResponseEntity.status(responseEntity.getStatusCode())
                        .headers(responseEntity.getHeaders())
                        .body(r);
            } else {
                return result;
            }
        } else if (result instanceof R) {
            R r = (R) result;
            if (!StrUtil.isBlank(token)) {
                r.put("token", token);
            }
            return r;
        } else {
            return result;
        }
    }
}
