package com.fh.shop.api.goods.biz;

import com.fh.shop.common.ServerResponse;

public interface ISkuService {


    ServerResponse findStatusList();

    void emailSku();

}
