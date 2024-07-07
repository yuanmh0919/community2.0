package com.yuanmh.community.controller;

import com.yuanmh.community.entity.Event;
import com.yuanmh.community.entity.User;
import com.yuanmh.community.event.EventConsumer;
import com.yuanmh.community.event.EventProducer;
import com.yuanmh.community.service.LikeService;
import com.yuanmh.community.utils.CommunityConstant;
import com.yuanmh.community.utils.CommunityUtil;
import com.yuanmh.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Yuanmh
 * @Date: 上午11:07 2024/6/26
 * @Describe: 点赞
 */

@Controller
public class LikeController implements CommunityConstant {
    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private EventConsumer eventConsumer;


    //异步请求实现点赞功能
    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        User user = hostHolder.getUser();
        //点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        //返回的结果
        Map<String, Object> result = new HashMap<>();
        result.put("likeCount", likeCount);
        result.put("likeStatus", likeStatus);

        //触发点赞事件
        //点赞：1，取消点赞：0 如果是点赞，则发送消息 取消点赞则不发送消息
        if (likeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(user.getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId);
            Map<String, Object> data = new HashMap<>();
            data.put("postId", postId);
            event.setData(data);
            eventProducer.fireEvent(event);
        }


        return CommunityUtil.getJsonString(0, null, result);
    }
}
