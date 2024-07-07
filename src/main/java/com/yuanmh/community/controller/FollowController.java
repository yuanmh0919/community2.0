package com.yuanmh.community.controller;

import com.yuanmh.community.annotation.LoginRequired;
import com.yuanmh.community.entity.Event;
import com.yuanmh.community.entity.Page;
import com.yuanmh.community.entity.User;
import com.yuanmh.community.event.EventConsumer;
import com.yuanmh.community.event.EventProducer;
import com.yuanmh.community.service.FollowService;
import com.yuanmh.community.service.UserService;
import com.yuanmh.community.utils.CommunityConstant;
import com.yuanmh.community.utils.CommunityUtil;
import com.yuanmh.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @Author: Yuanmh
 * @Date: 下午1:42 2024/6/27
 * @Describe: 关注 、取消关注
 */

@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private EventConsumer eventConsumer;

    @Autowired
    private FollowService followService;

    //为了使用当前用户 需要注入当前用户信息
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    // 点击关注和取消关注 是异步刷新页面
    @PostMapping("/follow")
    @ResponseBody
    @LoginRequired// 登录验证
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);

        //触发关注事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setUserId(user.getId())
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJsonString(0, "已关注！");
    }


    // 点击关注和取消关注 是异步刷新页面
    @PostMapping("/unfollow")
    @ResponseBody
    @LoginRequired// 登录验证
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(), entityType, entityId);
        return CommunityUtil.getJsonString(0, "已取消关注！");
    }

    //查询关注列表
    @GetMapping("/followees/{userId}")
    @LoginRequired// 登录验证
    public String followees(@PathVariable int userId, Model model, Page page) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("该用户不存在！");
        }
        model.addAttribute("user", user);
        page.setLimit(5);//每页显示5条记录
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER));
        //查询关注列表
        List<Map<String, Object>> userList = followService.findFollowees(userId, page.getOffSet(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                //查看关注列表 的关注状态
                boolean hasFollowed = isFollowed(u.getId());
                map.put("hasFollowed", hasFollowed);
            }
        }
        model.addAttribute("userList", userList);
        return "site/followee";
    }

    //查询粉丝列表
    @GetMapping("/followers/{userId}")
    @LoginRequired// 登录验证
    public String followers(@PathVariable int userId, Model model, Page page) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("该用户不存在！");
        }
        model.addAttribute("user", user);
        page.setLimit(5);//每页显示5条记录
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER, userId));
        //查询关注列表
        List<Map<String, Object>> userList = followService.findFollowers(userId, page.getOffSet(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                //查看关注列表 的关注状态
                boolean hasFollowed = isFollowed(u.getId());
                map.put("hasFollowed", hasFollowed);
            }
        }
        model.addAttribute("userList", userList);
        return "site/follower";
    }


    //查看关注状态
    private boolean isFollowed(int userId) {
        User user = hostHolder.getUser();
        if (user == null) {
            return false;
        }
        return followService.isFollowed(user.getId(), ENTITY_TYPE_USER, userId);
    }


}
