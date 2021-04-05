package com.vincent.funvideo.controller;

import com.vincent.funvideo.controller.form.DetailFrom;
import com.vincent.funvideo.controller.form.PageForm;
import com.vincent.funvideo.controller.form.RankForm;
import com.vincent.funvideo.controller.form.VideoForm;
import com.vincent.funvideo.db.pojo.Banner;
import com.vincent.funvideo.db.pojo.Type;
import com.vincent.funvideo.service.BannerService;
import com.vincent.funvideo.service.RecordService;
import com.vincent.funvideo.service.TypeService;
import com.vincent.funvideo.service.VideoService;
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
    public R getVideoListByCategory(@Valid VideoForm form) {
        checkApiKey(form);

        String category = form.getCategory();
        if (category.equals(CATEGORY_RECOMMEND)) {
            List<Banner> banners = bannerService.getBanners();
            List<Type> types = typeService.searchAllTypes();
            List<HashMap> video = videoService.getRecommendVideo(form.getPageIndex(), form.getPageSize());
            HashMap result = new HashMap();
            result.put("bannerList", banners);
            result.put("categoryList", types);
            result.put("videoList", video);
            return R.ok().put("data", result);
        } else {

            List<HashMap> list = videoService.getListByCategory(form.getCategory(), form.getPageIndex() == null ? DEFAULT_PAGE : form.getPageIndex(), form.getPageSize() == null ? DEFAULT_PAGE : form.getPageSize());
            HashMap result = new HashMap();
            result.put("videoList", list);
            return R.ok().put("data", result);
        }
    }

    @GetMapping("/detail")
    @ApiOperation("视频详情")
    public R getVideoDetail(@Valid DetailFrom form, @RequestHeader String token) {
        checkApiKey(form);


        HashMap video = videoService.getVideoDetail(form.getId());

        HashMap result = new HashMap();
        result.put("videoInfo", video);

        if (token != null) {
            int uid = jwtUtil.getUserId(token);
            result.put("isLike", recordService.isVideoLike(uid, form.getId()));
            result.put("isFavorite", recordService.isVideoCollect(uid, form.getId()));
        }
        result.put("videoList", videoService.getRecommendVideo(1, 10));
        return R.ok().put("data", result);
    }




    @GetMapping("/rank")
    @ApiOperation("排行")
    public R getRankBySort(@Valid RankForm form) {
        checkApiKey(form);
        String sort = form.getSort();
        List<HashMap> list = null;

        if (sort.equals(RANK_SORT_HOT)) {
            list = videoService.getHotRank(form.getPageIndex() == null ? DEFAULT_PAGE : form.getPageIndex(), form.getPageSize() == null ? DEFAULT_PAGE : form.getPageSize());
        } else if (sort.equals(RANK_SORT_NEW)) {
            list = videoService.getNewRank(form.getPageIndex() == null ? DEFAULT_PAGE : form.getPageIndex(), form.getPageSize() == null ? DEFAULT_PAGE : form.getPageSize());

        } else if (sort.equals(RANK_SORT_COLLECT)) {
            list = videoService.getNewRank(form.getPageIndex() == null ? DEFAULT_PAGE : form.getPageIndex(), form.getPageSize() == null ? DEFAULT_PAGE : form.getPageSize());

        } else {
            throw new VideoException("分类错误");
        }
        HashMap result = new HashMap();
        result.put("list",list);
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
