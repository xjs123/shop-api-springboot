package com.fh.shop.api.pay.controller;

import com.fh.shop.api.annotations.Check;
import com.fh.shop.api.order.biz.IOrderService;
import com.fh.shop.api.pay.biz.IPayService;
import com.fh.shop.common.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/pay")
public class PayController {

    @Resource(name = "payService")
    private IPayService payService;

    @Autowired
    private HttpServletResponse response;
    @Autowired
    private HttpServletRequest request;

    @Value("${order.url}")
    private String url;

    @PostMapping("/aliPay")
    @Check
    public ServerResponse aliPay(String orderId){
      return payService.aliPay(orderId);
    }

    @PostMapping("/aliNotify")
    public String aliNotify(){
        Map<String,String[]> requestParams = request.getParameterMap();
        System.out.println(requestParams);
        return payService.aliNotify(requestParams);
    }


    @GetMapping("/returnUrl")
    public void returnUrl(HttpServletRequest request){
        try {
            String price=payService.returnUrl(request);
            response.sendRedirect(url+"?price="+price);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }


}
