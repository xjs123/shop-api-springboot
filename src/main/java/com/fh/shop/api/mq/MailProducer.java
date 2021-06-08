package com.fh.shop.api.mq;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.configs.MQConfig;
import com.fh.shop.api.mapper.IMailConfirmMapper;
import com.fh.shop.api.po.MailConfirm;
import com.fh.shop.common.SystemConstant;
import com.fh.shop.configs.MailConfig;
import com.fh.shop.configs.MailPo;
import com.fh.shop.util.DateCalUtil;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class
MailProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private AmqpTemplate amqpTemplate;



    @Autowired
    private IMailConfirmMapper mailConfirmMapper;

    public void sendMail(MailPo mailPo){

            String mailConfirmStr = JSON.toJSONString(mailPo);
            amqpTemplate.convertAndSend(MailConfig.MAIL_EXCHANGE,MailConfig.MAIL_KEY,mailConfirmStr);
        }

    public void sendMailOk(MailPo mailPo){
        //插入数据库
        String msgId = UUID.randomUUID().toString();
        mailPo.setMsgId(msgId);
        String mailConfirmStr = JSON.toJSONString(mailPo);
        MailConfirm mailConfirm = new MailConfirm();
        mailConfirm.setMsgId(msgId);
        mailConfirm.setExchangeName(MailConfig.MAIL_EXCHANGE);
        mailConfirm.setRouteKey(MailConfig.MAIL_KEY+"xx");
        mailConfirm.setMsg(mailConfirmStr);
        Date date = new Date();
        mailConfirm.setUpdateTime(date);
        mailConfirm.setInsertTime(date);
        mailConfirm.setStatus(SystemConstant.MAIL_STATUS.MAIL_RUN);
        mailConfirm.setRetryTime(DateCalUtil.addDate(date,SystemConstant.MAIL_REATAR_Time));
        mailConfirm.setCount(0);
        mailConfirmMapper.insert(mailConfirm);
        //将id传入回调方法
        CorrelationData correlationData = new CorrelationData(msgId);
        rabbitTemplate.convertAndSend(MailConfig.MAIL_EXCHANGE,MailConfig.MAIL_KEY+"xx",mailConfirmStr,correlationData);

    }



}

