package com.fh.shop.api;

import com.fh.shop.api.mapper.IMailConfirmMapper;
import com.fh.shop.api.mq.MailProducer;
import com.fh.shop.configs.MailPo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class mail {

    @Autowired
    private MailProducer mailProducer;

    @Test
    public void test(){
        MailPo mailPo = new MailPo();
        mailPo.setTo("1124992392@qq.com");
        mailPo.setTitle("xx");
        mailPo.setBody("xxxx");
        mailProducer.sendMailOk(mailPo);

        try {
            Thread.sleep(30*60*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
