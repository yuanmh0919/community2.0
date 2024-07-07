package com.yuanmh.community.controller;

import com.yuanmh.community.annotation.LoginRequired;
import com.yuanmh.community.dao.DiscussPostMapper;
import com.yuanmh.community.dao.UserMapper;
import com.yuanmh.community.entity.DiscussPost;
import com.yuanmh.community.entity.Page;
import com.yuanmh.community.entity.User;
import com.yuanmh.community.service.LikeService;
import com.yuanmh.community.utils.CommunityConstant;
import com.yuanmh.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Yuanmh
 * @Date: 下午12:29 2024/6/18
 * @Describe:
 */

@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @LoginRequired
    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String index(Model model, Page page) {
        int userId = hostHolder.getUser().getId();
//        page.setRows(discussPostMapper.selectDiscussPostRows(userId));
        page.setRows(discussPostMapper.selectDiscussPostRows(null));
        page.setPath("/index");

        //获取当前登录的用户发表的前10篇帖子
//        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(userId, page.getOffSet(), page.getLimit());
        //查询所有帖子
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(null, page.getOffSet(), page.getLimit());


        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userMapper.selectById(Integer.parseInt(post.getUserId()));
                map.put("user", user);

                //添加点赞数量
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        return "index";
    }


    /**
     * 获取错误页面
     */
    @GetMapping("/error")
    public String getErrorPage() {
        return "/error/500";
    }
}
