package com.fh.shop.api.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fh.shop.api.mapper.IMailConfirmMapper;
import com.fh.shop.api.po.MailConfirm;
import com.fh.shop.common.SystemConstant;
import com.fh.shop.util.DateCalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class MqConfirmTask {
    @Autowired
    private IMailConfirmMapper mailConfirmMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Scheduled(cron = "0/30 * * * * ?")
    public void resend(){
        //首先要进行查询
        log.info("====================定时扫描表==========================");
        QueryWrapper<MailConfirm> mailConfirmQueryWrapper = new QueryWrapper<>();
        mailConfirmQueryWrapper.in("status",SystemConstant.MAIL_STATUS.MAIL_RUN,SystemConstant.MAIL_STATUS.E_Q_ERROR);
        mailConfirmQueryWrapper.le("retryTime",new Date());
        List<MailConfirm> mailConfirms = mailConfirmMapper.selectList(mailConfirmQueryWrapper);
        //查询出符合条件的数据 遍历判断
        mailConfirms.forEach(x-> {
            int count = x.getCount();
            String msgId = x.getMsgId();
            if(count>=SystemConstant.MAIL_COUNT){
                //修改状态为失败
                MailConfirm mailConfirm = new MailConfirm();
                mailConfirm.setMsgId(msgId);
                mailConfirm.setStatus(SystemConstant.MAIL_STATUS.MAIL_ERROR);
                mailConfirm.setUpdateTime(new Date());
                mailConfirmMapper.updateById(mailConfirm);
            }else {
                //修改 时间+1分钟
                MailConfirm mailConfirm = new MailConfirm();
                mailConfirm.setMsgId(msgId);
                mailConfirm.setUpdateTime(new Date());
                mailConfirm.setCount(x.getCount()+1);
                mailConfirm.setRetryTime(DateCalUtil.addDate(x.getRetryTime(),SystemConstant.MAIL_REATAR_Time));
                mailConfirmMapper.updateById(mailConfirm);
                //重新发送

                CorrelationData correlationData = new CorrelationData(msgId);
                rabbitTemplate.convertAndSend(x.getExchangeName(),x.getRouteKey(),x.getMsg(),correlationData);
            }
        });
    }

}
