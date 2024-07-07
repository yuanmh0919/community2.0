package com.yuanmh.community;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.DigestUtils;

import java.util.UUID;


/**
 * @Author: Yuanmh
 * @Date: 下午12:24 2024/6/19
 * @Describe:
 */

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CommonsTests {

    @Test
    public void testUUID() {
        String s = UUID.randomUUID().toString().replaceAll("-", "");
        System.out.println(s);
        //688c3254-00b2-4953-bf2a-6c1061e4255f
    }


    @Test
    public void testMD5() {
        System.out.println("123456".getBytes());//[B@5fc1e4fb
        System.out.println(DigestUtils.md5DigestAsHex("123456".getBytes()));//e10adc3949ba59abbe56e057f20f883e
    }
}
