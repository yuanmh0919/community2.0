package com.yuanmh.community;

import com.yuanmh.community.utils.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @Author: Yuanmh
 * @Date: 下午10:57 2024/6/18
 * @Describe:
 */

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    //    添加templateEngine，解决邮件模板渲染问题
    @Autowired
    private TemplateEngine templateEngine;

    //测试发送邮件
    @Test
    public void sendMail() {
        mailClient.sendMail("2396868340@qq.com", "测试邮件", "这是一封测试邮件");
    }

    @Test
    public void sendHTMLMail() {
        //context对象用于渲染模板
        Context context = new Context();
        //设置模板变量
        context.setVariable("username", "yuanmh");
        String content = templateEngine.process("mail/demo", context);
        System.out.println(content);
        mailClient.sendMail("2396868340@qq.com", "测试HTMl邮件", content);

    }


}
