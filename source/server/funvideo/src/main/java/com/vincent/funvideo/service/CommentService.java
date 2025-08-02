package com.vincent.funvideo.service;

import com.vincent.funvideo.db.dao.CommentDao;
import com.vincent.funvideo.db.pojo.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentDao commentDao;

    public void addComment(Comment comment) {
        commentDao.insertComment(comment);
    }

    public List<Comment> getCommentsByVideoId(int videoId, int limit, int offset) {
        return commentDao.selectCommentsByVideoId(videoId, limit, offset);
    }

    public List<Comment> getReplies(long parentId) {
        return commentDao.selectReplies(parentId);
    }

    public int getCommentCountByVideoId(int videoId) {
        return commentDao.countCommentsByVideoId(videoId);
    }
} 