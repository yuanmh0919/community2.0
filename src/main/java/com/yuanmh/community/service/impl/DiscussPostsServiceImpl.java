package com.yuanmh.community.service.impl;

import com.yuanmh.community.dao.DiscussPostMapper;
import com.yuanmh.community.entity.DiscussPost;
import com.yuanmh.community.service.DiscussPostsService;
import com.yuanmh.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @Author: Yuanmh
 * @Date: 上午11:09 2024/6/18
 * @Describe:
 */
@Service
public class DiscussPostsServiceImpl implements DiscussPostsService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    @Override
    public int findDiscussPostsRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    @Override
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    @Override
    public int addDiscussPost(DiscussPost post) {
        //先判断参数
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        //参数不为空 ，需要对数据进行过滤 标题 内容 进行敏感词过滤
        //如果用户输入的标题或者内容带有标签元素，可能会导致显示到页面上，需要进行转义处理
        //转义HTMl标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));
        //插入数据库
        return discussPostMapper.insertDiscussPost(post);
    }

    @Override
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }
}
