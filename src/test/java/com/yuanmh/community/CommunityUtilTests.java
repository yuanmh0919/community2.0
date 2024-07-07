package com.yuanmh.community;

import com.yuanmh.community.utils.CommunityUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

/**
 * @Author: Yuanmh
 * @Date: 下午1:26 2024/6/22
 * @Describe:
 */

@SpringBootTest
public class CommunityUtilTests {

    //测试json转String
    @Test
    public void testJsonToString() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", "yuanmh");
        map.put("age", 24);
        String jsonString = CommunityUtil.getJsonString(101, "test", map);
        System.out.println(jsonString);
    }
}
