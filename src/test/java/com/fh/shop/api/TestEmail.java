package com.fh.shop.api;

import com.fh.shop.api.common.MailUtil;
import net.bytebuddy.implementation.bytecode.Throw;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestEmail {

    @Autowired
    private JavaMailSender mailSender;
    @Value("${fh-xjs}")
    private String rootName;
    @Autowired
    private MailUtil mailUtil;

    @Test
    public void test1() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setText("薛金生最帅");
        simpleMailMessage.setSubject("哈哈哈");
        simpleMailMessage.setTo("1124992392@qq.com");
        simpleMailMessage.setFrom(rootName);
        mailSender.send(simpleMailMessage);
    }

    @Test
    public void test2() {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setSubject("发送html");
            mimeMessageHelper.setText("<h1 style='color : red'>薛金生很帅</h1>", true);
            mimeMessageHelper.setTo("1124992392@qq.com");
            mimeMessageHelper.setFrom(rootName);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test3() {
        mailUtil.sendMailHtml("1124992392@qq.com", "xjs", "<a src='http://localhost:8084/api/member/updateStatus'>aa</a>");
    }

    @Test
    public void test4() {
        List<String> url = new ArrayList<>();
        url.add("E:/图片/1.JPG");
        url.add("E:/图片/3.JPG");
        mailUtil.sendMailHtmlAttachment("1124992392@qq.com", "666", "222", url);
    }
}