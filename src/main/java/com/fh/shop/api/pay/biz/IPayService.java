package com.fh.shop.api.pay.biz;

import com.fh.shop.common.ServerResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface IPayService {
    ServerResponse aliPay(String orderId);

    String aliNotify(Map<String,String[]> requestParams);

    String returnUrl(HttpServletRequest request);
}
