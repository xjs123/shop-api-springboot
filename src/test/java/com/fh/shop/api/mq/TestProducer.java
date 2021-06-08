package com.fh.shop.api.mq;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.po.MsgError;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestProducer {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void sendMessage(){
        for (int i = 1; i <10 ; i++) {
            String uuid = UUID.randomUUID().toString();
            MsgError msgError = new MsgError();
            msgError.setMessageId(uuid);
            msgError.setMessage("xxxx"+i);
            String msgErrorStr = JSON.toJSONString(msgError);
            amqpTemplate.convertAndSend(TestMqConfig.TEST_EX,TestMqConfig.TEST_KEY,msgErrorStr);
        }
        try {

            Thread.sleep(10000);
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }

}
