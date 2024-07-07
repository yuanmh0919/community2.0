package com.yuanmh.community.controller;

import com.yuanmh.community.entity.Comment;
import com.yuanmh.community.entity.DiscussPost;
import com.yuanmh.community.entity.Page;
import com.yuanmh.community.entity.User;
import com.yuanmh.community.service.CommentService;
import com.yuanmh.community.service.DiscussPostsService;
import com.yuanmh.community.service.LikeService;
import com.yuanmh.community.service.UserService;
import com.yuanmh.community.utils.CommunityConstant;
import com.yuanmh.community.utils.CommunityUtil;
import com.yuanmh.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @Author: Yuanmh
 * @Date: 上午11:14 2024/6/18
 * @Describe:
 */

@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostsController implements CommunityConstant {
    @Autowired
    private DiscussPostsService discussPostsService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;


    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    //添加帖子
    @PostMapping(path = "/add")
    @ResponseBody
    public String addDiscussPost(@RequestParam("title") String title, @RequestParam("content") String content) {
        if (hostHolder.getUser() == null) {
            return CommunityUtil.getJsonString(403, "您还没有登录噢！");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(String.valueOf(hostHolder.getUser().getId()));
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostsService.addDiscussPost(discussPost);
        return CommunityUtil.getJsonString(0, "发布成功！");
    }


    //根据id查询帖子 注意：只要是声明在参数列表中的对象，都会自动实例化存入Model中
    @GetMapping(path = "/detail/{discussPostId}")
    public String getDiscussPostDetail(@PathVariable int discussPostId, Model model, Page page) {
        DiscussPost discussPost = discussPostsService.findDiscussPostById(discussPostId);
        model.addAttribute("discussPost", discussPost);
        //根据帖子中的userId查找到对应的用户信息
        User user = userService.findUserById(Integer.parseInt(discussPost.getUserId()));
        model.addAttribute("user", user);

        //点赞 获取点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId());
        model.addAttribute("likeCount", likeCount);
        //当前用户的点赞状态
        int likeStatus = hostHolder.getUser() == null ? 0 :
                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, discussPost.getId());
        model.addAttribute("likeStatus", likeStatus);
        //帖子详细页面的评论分页查询
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(discussPost.getCommentCount());
        //查询评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, discussPost.getId(), page.getOffSet(), page.getLimit());

        //遍历commentList，将其中的用户信息查询出来并添加到commentVoList中
        //什么是vo呢 就是view object的缩写，就是视图对象，就是用来承载数据的对象，
        //评论Vo列表 就是用来承载评论及其对应的用户信息的对象
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                //评论Vo
                Map<String, Object> commentVo = new HashMap<>();
                //评论
                commentVo.put("comment", comment);
                //评论作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));
                //回复 对回复不做分页处理，因为回复数量一般不会太多
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);


                //点赞 获取点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);
                //当前用户的点赞状态
                likeStatus = hostHolder.getUser() == null ? 0 :
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeStatus", likeStatus);

                //回复Vo列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        //回复
                        replyVo.put("reply", reply);
                        //回复的作者
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        //回复的目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);


                        //点赞 获取点赞数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);
                        //当前用户的点赞状态
                        likeStatus = hostHolder.getUser() == null ? 0 :
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeStatus", likeStatus);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replyList", replyVoList);

                //评论的回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("commentList", commentVoList);
        return "site/discuss-detail";
    }

    //查询我的帖子
    @GetMapping(path = "/my-post")
    public String getMyDiscussPosts(Model model, Page page) {
        //获取当前登录的用户
        User user = hostHolder.getUser();
        model.addAttribute("user", user);
        //分页查询我的帖子
        //根据用户id查询帖子数量
        int rows = discussPostsService.findDiscussPostsRows(user.getId());
        //设置访问路径
        page.setPath("/discuss/my-post");
        //设置分页信息
        page.setRows(rows);
        page.setLimit(5);
        //分页查询我的帖子
        List<DiscussPost> list = discussPostsService.findDiscussPosts(user.getId(), page.getOffSet(), page.getLimit());
        //遍历我的帖子
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                //添加点赞数量
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        return "site/my-post";
    }


}
