package com.fh.shop.api.mq;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.mapper.ImsgErrorMapper;
import com.fh.shop.api.po.MsgError;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

@Component
public class TestComsumer {

    @Autowired
    private ImsgErrorMapper msgErrorMapper;

    @RabbitListener(queues = TestMqConfig.TEST_QUEUE)
    @Transactional(rollbackFor = Exception.class)
    public void testComsumer(String msg, Message message, Channel channel) throws IOException {
        MsgError msgError = JSON.parseObject(msg, MsgError.class);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            if(msg.contains("xxxx3")){
                System.out.println(2/0);
            }
            //从队列中剔除
            channel.basicAck(deliveryTag,false);
        } catch (Exception e) {
            e.printStackTrace();
            //插入数据库让人工处理
            try {
                MsgError msgError1 = new MsgError();
                msgError1.setMessage(msg.substring(0,msg.length()>2000?2000:msg.length()));
                msgError1.setMessageId(msgError.getMessageId());
                msgError1.setInsertTime(new Date());
                msgErrorMapper.insert(msgError1);

                channel.basicAck(deliveryTag,false);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    Throwable cause = e1.getCause();
                    if(cause instanceof SQLException){
                        channel.basicAck(deliveryTag,false);
                    }
                    throw new RuntimeException(e1);
                }
        }
    }



}
