package com.yuanmh.community.utils;

/**
 * @Author: Yuanmh
 * @Date: 上午10:41 2024/6/26
 * @Describe: 动态拼接redis key
 */

public class RedisKeyUtil {
    //拼接key 使用:
    private static final String SPLIT = ":";
    //实体赞的前缀
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    //用户收到的赞
    private static final String PREFIX_USER_LIKE = "like:user";

    //关注模块
    //粉丝
    private static final String PREFIX_FOLLOWER = "follower";
    //目标
    private static final String PREFIX_FOLLOWEE = "followee";

    //存储验证码
    private static final String PREFIX_KAPTCHA = "kaptcha";

    //优化登录 存储登录凭证
    private static final String PREFIX_TICKET = "login:ticket";

    //优化查询用户 优先从缓存中获取用户数据
    private static final String PREFIX_USER = "user";

    //某个实体的赞
    //like:entity:entityType:entityId -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 获取某个用户收到的赞的key
     *
     * @param userId
     * @return
     */
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    /**
     * 关注目标
     * follower:userId:entityType -> zset(entityId,now) (按照时间排序)
     *
     * @param userId     追随者的id
     * @param entityType 关注的实体类型
     * @return 关注目标的key 格式为 follower:userId:entityType
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 某个实体拥有的粉丝
     * followee:entityType:entityId-> zset(userId,now) (按照时间排序)
     *
     * @param entityType
     * @param entityId
     * @return 某个实体拥有的粉丝的key 格式为 followee:entityType:entityId
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }


    /**
     * 存储验证码
     * kaptcha:owner -> value (验证码)
     *
     * @param owner 验证码的拥有者
     *              格式为 kaptcha:owner
     * @return 验证码的key
     */
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }


    /**
     * 登录凭证key
     *
     * @param ticket 登录页面传递过来的登录凭证
     * @return 登录凭证的key
     */
    public static String getLoginTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }


    /**
     * 优化查询用户 优先从缓存中获取用户数据 获取用户信息在redis中的key
     *
     * @param userId
     * @return
     */
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }


}
