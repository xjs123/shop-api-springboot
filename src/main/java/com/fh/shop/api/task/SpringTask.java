package com.fh.shop.api.task;

import com.fh.shop.api.goods.biz.ISkuService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SpringTask {

    @Resource(name = "skuService")
    private ISkuService skuService;

    @Scheduled(fixedDelay = 10*60000)
    public void  emailSku(){
        //skuService.emailSku();
    }



}
