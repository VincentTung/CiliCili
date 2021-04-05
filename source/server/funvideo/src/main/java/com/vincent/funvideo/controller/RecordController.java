package com.vincent.funvideo.controller;

import com.vincent.funvideo.config.shiro.JwtUtil;
import com.vincent.funvideo.controller.form.RecordForm;
import com.vincent.funvideo.db.pojo.Record;
import com.vincent.funvideo.service.RecordService;
import com.vincent.funvideo.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;


@RestController
@RequestMapping("/action")
@Api("视频操作")
@Slf4j
public class RecordController extends  BaseController {


    @Autowired
    private RecordService recordService;

    @GetMapping("/like")
    @ApiOperation("点赞视频列表")
    public R listList(@RequestHeader("token") String token) {
        Integer uid = jwtUtil.getUserId(token);
        return R.ok("操作成功").put("list",recordService.getLikeList(uid));
    }

    @PostMapping("/like")
    @ApiOperation("点赞视频")
    public R likeVideo(@RequestHeader("token") String token, @Valid RecordForm form) {
        Integer uid = jwtUtil.getUserId(token);
        recordService.likeVideo(uid, form.getVid());
        return R.ok("操作成功");
    }

    @DeleteMapping("/like")
    @ApiOperation("取消点赞")
    public R cancelLikeVideo(@RequestHeader("token") String token, @Valid RecordForm form) {
        Integer uid = jwtUtil.getUserId(token);
        recordService.cancelLikeVideo(uid, form.getVid());
        return R.ok("操作成功");
    }


    @PostMapping("/hate")
    @ApiOperation("讨厌视频")
    public R hateVideo(@RequestHeader("token") String token, @Valid RecordForm form) {
        Integer uid = jwtUtil.getUserId(token);
        recordService.hateVideo(uid, form.getVid());
        return R.ok("操作成功");
    }


    @DeleteMapping("/hate")
    @ApiOperation("取消讨厌视频")
    public R cancelHateVideo(@RequestHeader("token") String token,@Valid RecordForm form) {
        Integer uid = jwtUtil.getUserId(token);
        recordService.cancelLikeVideo(uid, form.getVid());
        return R.ok("操作成功");
    }



    @GetMapping("/collect")
    @ApiOperation("收藏视频列表")
    public R getCollectList(@RequestHeader("token") String token) {
        Integer uid = jwtUtil.getUserId(token);
        HashMap result = new HashMap();
        result.put("list",recordService.getCollectList(uid));
        return R.ok().put("data",result);
    }
    @PostMapping("/collect")
    @ApiOperation("收藏视频")
    public R collectVideo(@RequestHeader("token") String token, @Valid RecordForm form) {
        Integer uid = jwtUtil.getUserId(token);
        recordService.collectVideo(uid, form.getVid());
        return R.ok("操作成功");
    }


    @DeleteMapping("/collect")
    @ApiOperation("取消收藏视频")
    public R cancelCollectVideo(@RequestHeader("token") String token, @Valid RecordForm form) {
        Integer uid = jwtUtil.getUserId(token);
        recordService.cancelCollectVideo(uid, form.getVid());
        return R.ok("操作成功");
    }

    @PostMapping("/view")
    @ApiOperation("浏览记录+1")
    public R updateView(@RequestHeader("token") String token, @Valid RecordForm form) {
        Integer uid = jwtUtil.getUserId(token);
        recordService.viewVideo(uid, form.getVid());
        return R.ok("操作成功");
    }
    @GetMapping("/view")
    @ApiOperation("浏览视频列表")
    public R getViewList(@RequestHeader("token") String token) {
        Integer uid = jwtUtil.getUserId(token);
        return R.ok("操作成功").put("list",recordService.getViewList(uid));
    }
}
