package com.vincent.funvideo.db.dao;

import com.vincent.funvideo.db.pojo.Video;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface VideoDao {

    /**
     * id查询
     * @param id
     * @return
     */
    HashMap searchById(Integer id);

    /**
     * vid查询
     * @param vid
     * @return
     */
    Video searchByVid(String vid);

    /**
     * 分类查询
     * @param params
     * @return
     */
    List<HashMap> searchByCategory(HashMap params);


    /**
     * 最热排行
     * @param params
     * @return
     */
    List<HashMap> searchHotRank(HashMap params);

    /**
     * 最新排行
     * @param params
     * @return
     */
    List<HashMap> searchNewRank(HashMap params);

    /**
     * 收藏排行
     * @param params
     * @return
     */
    List<HashMap> searchCollectRank(HashMap params);


    List<HashMap> getRecommend(HashMap params);
}