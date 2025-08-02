package com.vincent.funvideo.db.dao;

import com.vincent.funvideo.db.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CommentDao {
    int insertComment(Comment comment);
    List<Comment> selectCommentsByVideoId(@Param("videoId") int videoId, @Param("limit") int limit, @Param("offset") int offset);
    List<Comment> selectReplies(@Param("parentId") long parentId);
    int countCommentsByVideoId(@Param("videoId") int videoId);
} 