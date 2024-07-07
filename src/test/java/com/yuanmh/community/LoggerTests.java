package com.yuanmh.community;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * @Author: Yuanmh
 * @Date: 下午7:49 2024/6/18
 * @Describe:
 */

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerTests.class);


    @Test
    public void testLogger() {
        System.out.println(LOGGER.getName());
        LOGGER.debug("debug log");
        LOGGER.info("info log");
        LOGGER.warn("warn log");
        LOGGER.error("error log");

    }
}
