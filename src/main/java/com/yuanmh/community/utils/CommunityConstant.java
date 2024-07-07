package com.yuanmh.community.utils;

/**
 * @Author: Yuanmh
 * @Date: 下午4:26 2024/6/19
 * @Describe: 常量
 */

public interface CommunityConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认状态失效时间 12小时
     */
    int DEFAULT_EXPIRED_SECONDS = 60 * 60 * 12;

    /**
     * 记住我状态失效时间 100天
     */
    int REMEMBER_EXPIRED_SECONDS = 60 * 60 * 24 * 100;

    /**
     * 设置实体类型 用户评论的是帖子还是回复
     * 帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 设置实体类型 用户评论的是帖子还是
     * 回复
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型：用户
     */
    int ENTITY_TYPE_USER = 3;

    /**
     * 主题：评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * 主题：点赞
     */
    String TOPIC_LIKE = "like";

    /**
     * 主题：关注
     */
    String TOPIC_FOLLOW = "follow";


    /**
     * 系统用户ID
     */
    int SYSTEM_USER_ID = 1;

}
