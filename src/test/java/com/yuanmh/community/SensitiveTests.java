package com.yuanmh.community;

import com.yuanmh.community.utils.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author: Yuanmh
 * @Date: 下午7:23 2024/6/21
 * @Describe:
 */

@SpringBootTest
public class SensitiveTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitive() {
        String text = "这里可以黄***赌***毒，u王阿尔甘如果爱看";
        System.out.println(sensitiveFilter.filter(text));

    }
}
