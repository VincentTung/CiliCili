package com.vincent.funvideo.controller;

import com.vincent.funvideo.config.shiro.JwtUtil;
import com.vincent.funvideo.controller.form.BaseForm;
import com.vincent.funvideo.controller.form.LoginForm;
import com.vincent.funvideo.db.pojo.User;
import com.vincent.funvideo.service.AuthoriseService;
import com.vincent.funvideo.service.BannerService;
import com.vincent.funvideo.service.RecordService;
import com.vincent.funvideo.service.UserService;
import com.vincent.funvideo.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.Cacheable;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Api("用户接口相关")
@Slf4j
public class UserController  extends  BaseController{

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private AuthoriseService authoriseService;
    @Autowired
    private UserService userService;

    @Autowired
    private RecordService recordService;

    @Autowired
    private BannerService bannerService;

    @Value("${video.jwt.cache-expire}")
    private int cacheExpire;

    @PostMapping("/login")
    @ApiOperation("登录")
    public R login(@Valid LoginForm form) {
        checkApiKey(form);
        
        // 使用加密密码验证
        Integer uid = authoriseService.searchByEncryptedPwd(form.getUsername(), form.getPwd(), form.getEncryptType());
        
        if (uid != null) {
            String token = jwtUtil.createToken(uid);
            saveCacheToken(token, uid);
            
            // 记录登录日志（不记录密码）
            log.info("用户登录成功 - 用户名: {}, UID: {}", form.getUsername(), uid);
            
            return R.ok().put("token", token).put("uid", uid);
        } else {
            log.warn("用户登录失败 - 用户名: {}", form.getUsername());
            return R.error("账号信息错误");
        }
    }

    @GetMapping("/profile")
    @ApiOperation("个人信息")
    public R getDetail(@RequestHeader String token , @Valid BaseForm form) {
        checkApiKey(form);
        int uid = jwtUtil.getUserId(token);
        User user = userService.searchUserInfo(uid);
        int collectSize = recordService.getCollectList(uid).size();
        int likeSize = recordService.getLikeList(uid).size();
        int viewSize = recordService.getViewList(uid).size();
        List fansList = recordService.getFanList(uid);

        HashMap result = new HashMap();
        result.put("name", user.getName());
        result.put("face", user.getFace());
        result.put("fans", fansList == null? 0:fansList.size());
        result.put("collect", collectSize);
        result.put("like", likeSize);
        result.put("view", viewSize);
        result.put("coin",user.getCoin());
        result.put("bannerList", bannerService.getBanners());
        result.put("courseList", Collections.emptyList());
        result.put("benefitList",Collections.emptyList());

        return R.ok().put("data", result);

    }

    private void saveCacheToken(String token, int userId) {
        redisTemplate.opsForValue().set(token, userId + "", cacheExpire, TimeUnit.DAYS);
    }
}
