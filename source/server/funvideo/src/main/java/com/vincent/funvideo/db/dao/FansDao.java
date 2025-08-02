package com.vincent.funvideo.db.dao;

import com.vincent.funvideo.db.pojo.Fans;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
@Mapper
public interface FansDao {
    

    int insert(Fans record);


    List<HashMap> getFansList(int uid);

    List<HashMap> getFocusList(int uid);
    int updateFocus(HashMap params);

    Integer searchFocus(int uid,int uper);
    Integer searchFocusAnyState(int uid, int uper);
}