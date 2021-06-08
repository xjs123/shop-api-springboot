package com.fh.shop.api.mq;

import com.fh.shop.configs.MQConstant;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderProducer {

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void
    sendOrderMessage(String orderId,String time){
        amqpTemplate.convertAndSend(MQConstant.ORDER_EX, MQConstant.ORDER_KEY,orderId,new  MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration(time);
                return message;
            }
        });
    }
}
