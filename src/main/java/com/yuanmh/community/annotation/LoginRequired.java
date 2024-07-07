package com.yuanmh.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: Yuanmh
 * @Date: 上午11:36 2024/6/21
 * @Describe: 是否登录才能访问的页面的注解 方法打上这个标记之后，必须登录之后才能访问
 * 结合LoginRequiredInterceptor拦截器 拦截这个注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})//注解作用于方法上 哪些方法可以不登陆就等访问
@Retention(RetentionPolicy.RUNTIME)//注解的生命周期，在运行时期间有效
public @interface LoginRequired {
}
