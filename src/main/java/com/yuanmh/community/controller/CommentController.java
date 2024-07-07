package com.yuanmh.community.controller;

import com.yuanmh.community.entity.Comment;
import com.yuanmh.community.entity.DiscussPost;
import com.yuanmh.community.entity.Event;
import com.yuanmh.community.event.EventConsumer;
import com.yuanmh.community.event.EventProducer;
import com.yuanmh.community.service.CommentService;
import com.yuanmh.community.service.DiscussPostsService;
import com.yuanmh.community.utils.CommunityConstant;
import com.yuanmh.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Yuanmh
 * @Date: 上午9:50 2024/6/24
 * @Describe: 评论
 */

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {


    @Autowired
    private CommentService commentService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private EventConsumer eventConsumer;


    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DiscussPostsService discussPostsService;


    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {

        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        //增加帖子
        commentService.addComment(comment);

        //评论需要传递帖子id 不是所有事件都需要帖子id 所以放到其他数据类型中
        Map<String, Object> data = new HashMap<>();
        data.put("postId", discussPostId);

        //触发评论事件 系统发布信息
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData(data);
        //加上帖子评论或者评论的回复的作者的id
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostsService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(Integer.valueOf(target.getUserId()));
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }


        eventProducer.fireEvent(event);
        return "redirect:/discuss/detail/" + discussPostId;
    }
}