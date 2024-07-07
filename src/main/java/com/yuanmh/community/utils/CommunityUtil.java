package com.yuanmh.community.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

/**
 * @Author: Yuanmh
 * @Date: 下午1:13 2024/6/19
 * @Describe: 提供对应方法为了用户注册使用
 */

public class CommunityUtil {
    /**
     * 生成一个随机的字符串
     * 方便后面生成注册码 或者 文件上传的名字...
     *
     * @return UUID
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * MD5加密
     *
     * @param key 要加密的字符串
     * @return 加密后的字符串
     */
    public static String md5(String key) {
        //使用commons-lang3提供的工具对key判断是否为空
        //如果是空串 空格 或者是null 则返回null
        if (StringUtils.isBlank(key)) {
            return null;
        }
        //先使用getBytes()方法将字符串转换为字节数组
        //再使用DigestUtils工具类提供的md5DigestAsHex()方法对字节数组进行加密
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }


    //服务器向浏览器返回数据使用json格式

    /**
     * 根据服务器返回json格式数据的code和msg生成json字符串
     * @param code
     * @param msg
     * @param map 封装返回的数据
     * @return
     */
    public static String getJsonString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    //如果只有一个参数或者两个参数的情况
    public static String getJsonString(int code, String msg) {
        return getJsonString(code, msg, null);
    }

    public static String getJsonString(int code) {
        return getJsonString(code, null, null);
    }
}
