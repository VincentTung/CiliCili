package com.vincent.funvideo.controller;

import com.vincent.funvideo.db.pojo.Comment;
import com.vincent.funvideo.service.CommentService;
import com.vincent.funvideo.service.UserService;
import com.vincent.funvideo.util.R;
import com.vincent.funvideo.util.SensitiveWordFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController extends BaseController{
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public R addComment(@RequestHeader("token") String token,@RequestBody Comment comment) {
        // 从token获取userId
        Integer userId = jwtUtil.getUserId(token);
        comment.setUserId(userId);

        // 敏感词过滤
        comment.setContent(SensitiveWordFilter.filter(comment.getContent()));
        // Set avatar from user info
        if (userId != null) {
            var user = userService.searchUserInfo(userId);
            if (user != null) {
                comment.setAvatar(user.getFace());
            }
        }
        commentService.addComment(comment);
        return R.ok("评论成功");
    }

    @GetMapping("/list")
    public R getComments(@RequestParam int videoId,
                         @RequestParam(defaultValue = "10") int limit,
                         @RequestParam(defaultValue = "0") int offset) {
        List<Comment> comments = commentService.getCommentsByVideoId(videoId, limit, offset);
        return R.ok().put("data", comments);
    }

    @GetMapping("/replies")
    public R getReplies(@RequestParam long parentId) {
        List<Comment> replies = commentService.getReplies(parentId);
        return R.ok().put("data", replies);
    }

    @GetMapping("/count")
    public R getCommentCount(@RequestParam int videoId) {
        int count = commentService.getCommentCountByVideoId(videoId);
        return R.ok().put("data", count);
    }
} 