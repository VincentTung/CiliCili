package com.vincent.funvideo.controller;

import com.vincent.funvideo.config.shiro.JwtUtil;
import com.vincent.funvideo.controller.form.FocusForm;
import com.vincent.funvideo.controller.form.RecordForm;
import com.vincent.funvideo.db.pojo.Record;
import com.vincent.funvideo.db.pojo.User;
import com.vincent.funvideo.service.RecordService;
import com.vincent.funvideo.service.UserService;
import com.vincent.funvideo.service.VideoService;
import com.vincent.funvideo.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/action")
@Api("视频操作")
@Slf4j
public class RecordController extends BaseController {


    @Autowired
    private RecordService recordService;

    @Autowired
    private UserService userService;


    @GetMapping("/like")
    @ApiOperation("点赞视频列表")
    public R likeList(@RequestHeader("token") String token) {
        Integer uid = jwtUtil.getUserId(token);
        List<HashMap> list = recordService.getLikeList(uid);
        HashMap map = new HashMap();
        map.put("list", list);
        map.put("total",list.size());
        return R.ok().put("data",map);
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
        int result = recordService.hateVideo(uid, form.getVid());
        return R.ok("操作成功");
    }


    @DeleteMapping("/hate")
    @ApiOperation("取消讨厌视频")
    public R cancelHateVideo(@RequestHeader("token") String token, @Valid RecordForm form) {
        Integer uid = jwtUtil.getUserId(token);
        recordService.cancelLikeVideo(uid, form.getVid());
        return R.ok("操作成功");
    }


    @GetMapping("/collect")
    @ApiOperation("收藏视频列表")
    public R getCollectList(@RequestHeader("token") String token) {
        Integer uid = jwtUtil.getUserId(token);
        HashMap result = new HashMap();
        result.put("list", recordService.getCollectList(uid));
        return R.ok().put("data", result);
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
        List<HashMap> list = recordService.getViewList(uid);
        HashMap map = new HashMap();
        map.put("list", list);
        map.put("total",list.size());
        return R.ok().put("data",map);
    }


    @GetMapping("/coin")
    @ApiOperation("投币视频列表")
    public R getCoinList(@RequestHeader("token") String token) {
        Integer uid = jwtUtil.getUserId(token);
        List<HashMap> list = recordService.getCoinList(uid);
        HashMap map = new HashMap();
        map.put("list", list);
        map.put("total",list.size());
        return R.ok().put("data",map);
    }
    @PostMapping("/coin")
    @ApiOperation("收藏视频")
    public R coinVideo(@RequestHeader("token") String token, @Valid RecordForm form) {
        Integer uid = jwtUtil.getUserId(token);
        User user = userService.searchUserInfo(uid);
        if (user.getCoin() > 0) {
            if (recordService.coinVideo(uid, form.getVid()) != 0) {
                userService.minusCoin(uid);
            }else{
                return R.error("金币不足");
            }

        }
        return R.ok("操作成功");
    }




    @GetMapping("/fans")
    @ApiOperation("粉丝列表")
    public R fansList(@RequestHeader("token") String token) {
        Integer uid = jwtUtil.getUserId(token);
        List<HashMap> list = recordService.getFanList(uid);
        HashMap map = new HashMap();
        map.put("list", list);
        map.put("total",list.size());
        return R.ok().put("data",map);
    }


    @GetMapping("/focus")
    @ApiOperation("关注列表")
    public R focusList(@RequestHeader("token") String token) {
        Integer uid = jwtUtil.getUserId(token);
        List<HashMap> list = recordService.getFocusList(uid);
        HashMap map = new HashMap();
        map.put("list", list);
        map.put("total",list.size());
        return R.ok().put("data",map);
    }

    @PostMapping("/focus")
    @ApiOperation("关注某个up主")
    public R focus(@RequestHeader("token") String token, FocusForm form) {
        Integer uid = jwtUtil.getUserId(token);
        if( recordService.focusUpper(uid, form.getUper())!=0) {
            return R.ok("关注成功");
        }else{
            return R.error("关注失败");
        }
    }

    @DeleteMapping("/focus")
    @ApiOperation("取消关注某个up主")
    public R cancelFocus(@RequestHeader("token") String token, @Valid FocusForm form) {
        Integer uid = jwtUtil.getUserId(token);
        if(recordService.cancelFocusUper(uid, form.getUper())!=0) {
            return R.ok("取消关注成功");
        }else{
            return R.error("取消关注失败");
        }
    }


}
