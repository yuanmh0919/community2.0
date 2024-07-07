package com.yuanmh.community.service;

import com.yuanmh.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @Author: Yuanmh
 * @Date: 上午10:51 2024/6/26
 * @Describe: 实现点赞业务逻辑
 */

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 点赞 取消点赞
     *
     * @param userId       点赞者的用户id
     * @param entityType   帖子或者评论的类型 1 帖子 2 评论 后续肯能还会添加其他类型
     * @param entityId     帖子或者评论的id
     * @param entityUserId 帖子或者评论的作者id
     */
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //获取点赞类型的key
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                //获取帖子作者的key
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                //查看当前用户是否对这条信息点过赞 (将查询放在事务之外，查询要么在事务前查询，要么在事务后查询，在事务中查询不会立即得到结果)
                Boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
                //开启事务
                operations.multi();
                if (isMember) {
                    //已经点赞，再点击一次则是取消点赞
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    //未点赞，则点赞
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                return operations.exec();//提交事务
            }
        });
    }


    /**
     * 查询实体的点赞数量
     *
     * @param entityType
     * @param entityId
     * @return set的长度就是点赞数量
     */
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    //查询某人对某实体的点赞状态

    /**
     * 查询某人对某实体的点赞状态
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @return 1 点赞 0 未点赞 以后可以扩展 -1表示点击踩
     */
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    //查询某个用户获得的赞
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }


}








