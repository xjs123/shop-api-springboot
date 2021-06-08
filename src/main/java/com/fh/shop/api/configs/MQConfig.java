package com.fh.shop.api.configs;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.mapper.IMailConfirmMapper;
import com.fh.shop.api.po.MailConfirm;
import com.fh.shop.common.SystemConstant;
import com.fh.shop.configs.MQConstant;
import com.fh.shop.configs.MailConfig;
import com.fh.shop.configs.MailPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.UnsupportedEncodingException;
import java.lang.String;
import java.util.Date;

@Configuration
@Slf4j
public class MQConfig {

    @Autowired
    private ConnectionFactory connectionFactory;
    @Autowired
    private IMailConfirmMapper mailConfirmMapper;



    @Bean
    public RabbitTemplate rabbitTemplate(){
        StringBuilder flg=new StringBuilder();
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause)->{
            if(ack){
                //消息发送成功了 修改状态为发送成功
                String msgId = correlationData.getId();
                MailConfirm mailConfirm1 = mailConfirmMapper.selectById(msgId);
                if(mailConfirm1.getStatus()==SystemConstant.MAIL_STATUS.MAIL_RUN){
                    MailConfirm mailConfirm = new MailConfirm();
                    mailConfirm.setStatus(SystemConstant.MAIL_STATUS.MAIL_SUCCESS);
                    mailConfirm.setMsgId(msgId);
                    mailConfirm.setUpdateTime(new Date());
                    mailConfirmMapper.updateById(mailConfirm);
                }
            }else {
                //消息发送失败了
                log.info("p-e发送失败了 消息:{} 原因:{} ",correlationData,cause);
            }
        });
        // e--q
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.info("消息从Exchange路由到Queue失败: exchange: {}, route: {}, replyCode: {}, replyText: {}, message: {}", exchange, routingKey, replyCode, replyText, message);
            //修改状态为4 e-q失败
            byte[] body = message.getBody();
            try {
                String info = new String(body,"utf-8");
                MailPo mailPo = JSON.parseObject(info, MailPo.class);
                String msgId = mailPo.getMsgId();
                MailConfirm mailConfirm = new MailConfirm();
                mailConfirm.setStatus(SystemConstant.MAIL_STATUS.E_Q_ERROR);
                mailConfirm.setMsgId(msgId);
                mailConfirm.setUpdateTime(new Date());
                mailConfirmMapper.updateById(mailConfirm);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public DirectExchange mailExchange(){
        return new DirectExchange(MailConfig.MAIL_EXCHANGE,true,false);
    }

    @Bean
    public Queue mailQueue(){
        return new Queue(MailConfig.MAIL_QUEUE,true);
    }

    @Bean
    public Queue queueBuilder(){
        return   QueueBuilder.durable(MailConfig.MAIL_QUEUE).build();
    }

    @Bean
    public Binding mailBinding(){
        return BindingBuilder.bind(mailQueue()).to(mailExchange()).with(MailConfig.MAIL_KEY);
    }

    //死信队列
    @Bean
    public DirectExchange OrderEx(){
        return new DirectExchange(MQConstant.ORDER_EX,true,false);
    }

    @Bean
    public Queue OrderQueue(){
        Queue queue = new Queue(MQConstant.ORDER_queue, true);
        queue.addArgument("x-dead-letter-exchange",MQConstant.ORDER_DEAD_EX);
        queue.addArgument("x-dead-letter-routing-key",MQConstant.ORDER_DEAD_KEY);
        return  queue;
    }
    @Bean
    public Binding orderBinding(){
        return BindingBuilder.bind(OrderQueue()).to(OrderEx()).with(MQConstant.ORDER_KEY);
    }


    @Bean
    public DirectExchange OrderDeadEx(){
        return new DirectExchange(MQConstant.ORDER_DEAD_EX,true,false);
    }
    @Bean
    public Queue OrderDeadQueue(){
        return new Queue(MQConstant.ORDER_DEAD_QUEUE,true);
    }
    @Bean
    public Binding orderDeadBinding(){
        return BindingBuilder.bind(OrderDeadQueue()).to(OrderDeadEx()).with(MQConstant.ORDER_DEAD_KEY);
    }








    //积分
    @Bean
    public FanoutExchange payExchange(){
        return new FanoutExchange(MQConstant.PAY_EXCHANGE,true,false);
    }
    @Bean
    public Queue CountQueue(){
        return new Queue(MQConstant.PAY_COUNT_QUEUE,true);
    }

    @Bean
    public Queue CellQueue(){
        return QueueBuilder.durable(MQConstant.PAY_CELL_QUEUE).build();
    }


    @Bean
    public Binding countBinding(){
        return BindingBuilder.bind(CountQueue()).to(payExchange());
    }

    @Bean
    public Binding cleeBinging(){
        return BindingBuilder.bind(CellQueue()).to(payExchange());
    }

}
