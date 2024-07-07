package com.yuanmh.community.utils;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;


/**
 * @Author: Yuanmh
 * @Date: 下午10:47 2024/6/18
 * @Describe:
 */

@Component
public class MailClient {
    //记录日志
    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender mailSender;


    //邮件发送人
    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            //设置发件人
            helper.setFrom(from);
//            设置收件人
            helper.setTo(to);
            //设置发送主题
            helper.setSubject(subject);
            //设置内容 true表示支持html
            helper.setText(content, true);
            //发送
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("邮件发送失败: ", e.getMessage());
        }

    }

}
