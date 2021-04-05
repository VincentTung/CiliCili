package com.vincent.funvideo.config;

import com.vincent.funvideo.util.VideoException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Component
public class ExceptionAdvice {

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String validExceptionHandler(Exception e) {
        log.error("执行异常了", e);
        if (e instanceof MethodArgumentNotValidException) {

            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
            return exception.getBindingResult().getFieldError().getDefaultMessage();

        } else if (e instanceof BindException) {
            return "参数错误";
        } else if (e instanceof VideoException) {

            return ((VideoException) e).getMsg();

        } else if (e instanceof UnauthenticatedException) {

            return "你不具备相关权限";

        } else {

            return "后端执行异常";
        }
    }
}
