package com.vincent.funvideo.controller;

import com.vincent.funvideo.controller.form.*;
import com.vincent.funvideo.db.pojo.Banner;
import com.vincent.funvideo.db.pojo.Record;
import com.vincent.funvideo.db.pojo.Type;
import com.vincent.funvideo.service.BannerService;
import com.vincent.funvideo.service.RecordService;
import com.vincent.funvideo.service.TypeService;
import com.vincent.funvideo.service.VideoService;
import com.vincent.funvideo.util.CommonUtil;
import com.vincent.funvideo.util.R;
import com.vincent.funvideo.util.VideoException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import java.util.concurrent.TimeUnit;

import static com.vincent.funvideo.service.impl.RecordServiceImpl.HATE;
import static com.vincent.funvideo.service.impl.RecordServiceImpl.LIKE;

@RestController
@RequestMapping("/video")
@Api("视频列表接口")
public class VideoController extends BaseController {


    private final static int DEFAULT_PAGE = 1;
    private final static int DEFAULT_SIZE = 10;
    private final static String CATEGORY_RECOMMEND = "推荐";
    private final static String RANK_SORT_HOT = "hot";
    private final static String RANK_SORT_NEW = "new";
    private final static String RANK_SORT_COLLECT = "collect";
    @Autowired
    private VideoService videoService;

    @Autowired
    private BannerService bannerService;

    @Autowired
    private RecordService recordService;
    @Autowired
    private TypeService typeService;

    @GetMapping("/list")
    @ApiOperation("视频列表")
    public ResponseEntity<R> getVideoListByCategory(@Valid VideoForm form) {
        checkApiKey(form);

        String category = form.getCategory();
        R response;
        if (category.equals(CATEGORY_RECOMMEND)) {
            List<Banner> banners = bannerService.getBanners();
            List<Type> types = typeService.searchAllTypes();
            List<HashMap> video = videoService.getRecommendVideo(CommonUtil.getStart(form), form.getPageSize());
            HashMap result = new HashMap();
            result.put("bannerList", banners);
            result.put("categoryList", types);
            result.put("videoList", video);
            response = R.ok().put("data", result);
        } else {
            int page = form.getPageIndex() == null ? DEFAULT_PAGE : form.getPageIndex();
            int size = form.getPageSize() == null ? DEFAULT_SIZE : form.getPageSize();

            int start = CommonUtil.getStart(page, size);
            List<HashMap> list = videoService.getListByCategory(form.getCategory(),start,size);
            HashMap result = new HashMap();
            result.put("videoList", list);
            response = R.ok().put("data", result);
        }
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS).cachePublic())
            .body(response);
    }
    @GetMapping("/search")
    @ApiOperation("视频列表")
    public R searchVieo(@Valid SearchForm form) {
        checkApiKey(form);

        HashMap result= new HashMap();
        result.put("videoList",videoService.searchVideoByWord(form.getKeyword()));
        return R.ok().put("data",result);
    }

    @GetMapping("/detail")
    @ApiOperation("视频详情")
    public ResponseEntity<R> getVideoDetail(@Valid DetailFrom form, @RequestHeader String token) {
        checkApiKey(form);


        HashMap video = videoService.getVideoDetail(form.getId());

        HashMap result = new HashMap();


        if (token != null) {
            int uid = jwtUtil.getUserId(token);
            Object uperObj = video.get("uper");
            Integer uper = null;
            if (uperObj instanceof String) {
                uper = Integer.parseInt((String) uperObj);
            } else if (uperObj instanceof Integer) {
                uper = (Integer) uperObj;
            }
            boolean isFocus = false;
            if (uper != null) {
                isFocus = recordService.getFocus(uid, uper) != null;
            }
            video.put("isFocus",isFocus);
            Record record = recordService.searRecord(uid,form.getId());
            if (record != null) {
                int likeSate = record.getLike();
                result.put("isLike", likeSate == LIKE);
                result.put("isHate", likeSate == HATE);
                result.put("isCoin",record.getCoin() != 0);
            } else {
                result.put("isLike", false);
                result.put("isHate", false);
                result.put("isCoin",false);
            }
            result.put("isFavorite", recordService.isVideoCollect(uid, form.getId()));
            recordService.viewVideo(uid,form.getId());
            videoService.viewVideo(form.getId());
        }
        result.put("videoInfo", video);
        result.put("videoList", videoService.getRecommendVideo(0, 10));
        R response = R.ok().put("data", result);
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(0, TimeUnit.SECONDS).cachePublic())
            .body(response);
    }


    @GetMapping("/rank")
    @ApiOperation("排行")
    public R getRankBySort(@Valid RankForm form) {
        checkApiKey(form);
        String sort = form.getSort();
        List<HashMap> list = null;

        int page = form.getPageIndex() == null ? DEFAULT_PAGE : form.getPageIndex();
        int size = form.getPageSize() == null ? DEFAULT_SIZE : form.getPageSize();

        int start = CommonUtil.getStart(page, size);
        if (sort.equals(RANK_SORT_HOT)) {
            list = videoService.getHotRank(start,size);
        } else if (sort.equals(RANK_SORT_NEW)) {
            list = videoService.getNewRank(start,size);

        } else if (sort.equals(RANK_SORT_COLLECT)) {
            list = videoService.getNewRank(start,size);

        } else {
            throw new VideoException("分类错误");
        }
        HashMap result = new HashMap();
        result.put("list", list);
        return R.ok().put("data", result);
    }
//    @GetMapping("/hot")
//    @ApiOperation("最热排行")
//    public R getHotRank(@Valid PageForm form) {
//        checkApiKey(form);
//        List<HashMap> list = videoService.getHotRank(form.getPageIndex() == null ? DEFAULT_PAGE : form.getPageIndex(), form.getPageSize() == null ? DEFAULT_PAGE : form.getPageSize());
//        return R.ok().put("data", list);
//    }
//
//    @GetMapping("/new")
//    @ApiOperation("最新排行")
//    public R getNewRank(@Valid PageForm form) {
//        checkApiKey(form);
//        List<HashMap> list = videoService.getNewRank(form.getPageIndex() == null ? DEFAULT_PAGE : form.getPageIndex(), form.getPageSize() == null ? DEFAULT_PAGE : form.getPageSize());
//        return R.ok().put("data", list);
//    }
//
//    @GetMapping("/collect")
//    @ApiOperation("收藏排行")
//    public R getCollectRank(@Valid PageForm form) {
//        checkApiKey(form);
//        List<HashMap> list = videoService.getCollectRank(form.getPageIndex() == null ? DEFAULT_PAGE : form.getPageIndex(), form.getPageSize() == null ? DEFAULT_PAGE : form.getPageSize());
//        return R.ok().put("data", list);
//    }
}
