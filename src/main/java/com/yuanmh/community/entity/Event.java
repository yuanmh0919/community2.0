package com.yuanmh.community.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Yuanmh
 * @Date: 上午10:13 2024/7/1
 * @Describe: 将发布系统通知作为一个个的事件处理
 */

public class Event {
    //事件主题 给帖子点赞 关注 评论都是事件的主题
    private String topic;
    //事件的发起者
    private Integer userId;
    //事件的实体类型
    private Integer EntityType;
    //实体id
    private Integer entityId;
    //实体作者
    private Integer entityUserId;
    //事件对象可能还会有其他特殊数据需要记录
    private Map<String, Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    /**
     * 设置事件主题 并且返回事件对象 方便链式调用
     *
     * @param topic 事件主题
     * @return Event
     */
    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public Integer getUserId() {
        return userId;
    }

    public Event setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public Integer getEntityType() {
        return EntityType;
    }

    public Event setEntityType(Integer entityType) {
        EntityType = entityType;
        return this;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public Event setEntityId(Integer entityId) {
        this.entityId = entityId;
        return this;
    }

    public Integer getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(Integer entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }
}
