package com.yuanmh.community.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: Yuanmh
 * @Date: 下午10:31 2024/6/20
 * @Describe:
 */

public class CookieUtil {
    /**
     * 获取cookie值ticket
     *
     * @param request
     * @param name
     * @return String cookie值  用户凭证ticket
     */
    public static String getValue(HttpServletRequest request, String name) {
        if (request == null || StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        // 获取cookie数组 拿到所有cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
