package com.luckcat.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Oriental
 * @version 1.0
 * @description 邮件发送工具类
 * @date 2023/3/26 13:27
 */

@Data
@Component
public class sendMail {
    @Resource
    private JavaMailSenderImpl mailSender;

    @Resource
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;


    public void sendTemplateMail(String email,int captcha) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true);
            messageHelper.setFrom(from);// 发送人的邮箱
            messageHelper.setTo(email);//发给谁  对方邮箱
            messageHelper.setSubject("找回密码"); //标题
            //使用模板thymeleaf
            //Context是导这个包import org.thymeleaf.context.Context;
            Context context = new Context();
            Map<String, Object> map = new HashMap<>();
            map.put("from",from);
            map.put("captcha",captcha);
            //定义模板数据
            context.setVariables(map);
            //获取thymeleaf的html模板
            String emailContent = templateEngine.process("findPassword",context); //指定模板路径
            messageHelper.setText(emailContent,true);
            //发送邮件
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
