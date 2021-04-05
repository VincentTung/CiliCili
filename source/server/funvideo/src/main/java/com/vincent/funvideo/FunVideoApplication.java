package com.vincent.funvideo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ServletComponentScan
@Slf4j
@EnableAsync
public class FunVideoApplication {


    public static void main(String[] args) {
        SpringApplication.run(FunVideoApplication.class, args);
    }

}
