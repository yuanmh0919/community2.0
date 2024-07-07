package com.yuanmh.community.entity;

import java.util.Date;

/**
 * @Author: Yuanmh
 * @Date: 下午1:48 2024/6/17
 * @Describe: 帖子实体类
 */

public class DiscussPost {

    private Integer id;
    private String userId;
    private String title;//标题
    private String content;//内容
    private Integer type;//是否置顶 0 正常 1 置顶
    private Integer status;//状态 0 正常 1 精华 2 拉黑
    private Date createTime;
    private Integer commentCount;//评论数
    private Double score;//评分 用于后面帖子排名

    public DiscussPost() {
    }

    public DiscussPost(String userId, String title, String content, Integer type, Integer status, Date createTime, Integer commentCount, Double score) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.type = type;
        this.status = status;
        this.createTime = createTime;
        this.commentCount = commentCount;
        this.score = score;
    }

    public DiscussPost(Integer id, String userId, String title, String content, Integer type, Integer status, Date createTime, Integer commentCount, Double score) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.type = type;
        this.status = status;
        this.createTime = createTime;
        this.commentCount = commentCount;
        this.score = score;
    }

    @Override
    public String toString() {
        return "DiscussPost{" + "id=" + id + ", userId='" + userId + '\'' + ", title='" + title + '\'' + ", content='" + content + '\'' + ", type=" + type + ", status=" + status + ", createTime=" + createTime + ", commentCount=" + commentCount + ", score=" + score + '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
