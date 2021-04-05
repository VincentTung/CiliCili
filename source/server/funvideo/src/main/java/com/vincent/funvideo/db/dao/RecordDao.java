package com.vincent.funvideo.db.dao;

import com.vincent.funvideo.db.pojo.Record;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface RecordDao {

    int insert(Record record);

    Record searchByVid(HashMap params);

    int updateLike(HashMap params);

    int updateCollect(HashMap params);

    List<HashMap> getCollectList(int uid);

    List<HashMap> getLikeList(int uid);

    List<HashMap> getViewList(int uid);

    int updateView(HashMap params);

    Integer isVideoLike(HashMap params);

    Integer isVideoCollect(HashMap params);
}