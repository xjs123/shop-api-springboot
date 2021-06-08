package com.fh.shop.api.mq;

import com.alibaba.fastjson.JSON;
import com.fh.shop.configs.MQConstant;
import com.fh.shop.configs.PayMessage;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PayProducer {

    @Autowired
    private AmqpTemplate amqpTemplate;


    public void paySend(PayMessage message){
        String payMessage = JSON.toJSONString(message);
        amqpTemplate.convertAndSend(MQConstant.PAY_EXCHANGE,"",payMessage);
    }


}
