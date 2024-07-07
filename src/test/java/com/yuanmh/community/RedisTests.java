package com.yuanmh.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Yuanmh
 * @Date: 下午8:43 2024/6/25
 * @Describe:
 */

@SpringBootTest
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testRedis() {

        String redisKey = "testString:count";
        redisTemplate.opsForValue().set(redisKey, 1);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        //增加和减少
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }


    @Test
    public void testRedisHash() {
        String redisKey = "testHash:user";
        redisTemplate.opsForHash().put(redisKey, "id", "1001");
        redisTemplate.opsForHash().put(redisKey, "username", "jack");
        redisTemplate.opsForHash().put(redisKey, "password", "jack1001");
        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "password"));

    }

    @Test
    public void testRedisList() {
        String redisKey = "testList:ids";
        redisTemplate.opsForList().leftPush(redisKey, "1001");
        redisTemplate.opsForList().leftPush(redisKey, "1002");
        redisTemplate.opsForList().leftPush(redisKey, "1003");
        //获取列表长度
        System.out.println(redisTemplate.opsForList().size(redisKey));
        //获取列表元素
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, -1));
    }

    @Test
    public void testRedisSet() {
        String redisKey = "testSet:ids";
        redisTemplate.opsForSet().add(redisKey, "1001", "1002", "1003", "1004", "1005", "1001");
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        //获取所有元素
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }

    @Test
    public void testRedisZSet() {
        String redisKey = "testZSet:scores";
        redisTemplate.opsForZSet().add(redisKey, "1001", 80);
        redisTemplate.opsForZSet().add(redisKey, "1002", 90);
        redisTemplate.opsForZSet().add(redisKey, "1003", 70);
        System.out.println(redisTemplate.opsForZSet().size(redisKey));
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "1001"));
        System.out.println(redisTemplate.opsForZSet().range(redisKey, 0, -1));
    }

    @Test
    public void testKeys() {
        redisTemplate.delete("testHash:user");
        System.out.println(redisTemplate.hasKey("testHash:user"));
        //设置过期时间 设置test:time的过期时间为10秒
        redisTemplate.opsForValue().set("test:time", "hello world");
        redisTemplate.expire("test:time", 10, TimeUnit.SECONDS);
    }


    //多次访问同一个key，使用绑定key
    @Test
    public void testBoundKey() {
        String redisKey = "test:bound";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        operations.set(100);
        operations.set(101);
        operations.set(102);
        operations.increment();//增量操作
        System.out.println(operations.get());//103
    }

    //测试事务
    @Test
    public void testTransaction() {
        Object object = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:tx";
                //开启事务
                operations.multi();
                operations.opsForSet().add(redisKey, "zhangsan", "lisi", "wangwu");
                System.out.println(operations.opsForSet().members(redisKey));//看查询操作会不会立刻执行
                operations.opsForSet().add(redisKey, "zhaoliu");
                return operations.exec();//提交事务
            }
        });
        System.out.println(object);//提交事务后返回的结果
    }

}

