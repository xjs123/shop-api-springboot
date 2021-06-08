package com.fh.shop.api.order.biz;

import com.fh.shop.api.param.OrderParam;
import com.fh.shop.common.ServerResponse;

public interface IOrderService {

    public ServerResponse addOrder(OrderParam orderParam);





    ServerResponse findList(Long id);




}
