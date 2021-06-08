package com.fh.shop.api.order.controller;

import com.fh.shop.api.annotations.Check;
import com.fh.shop.api.annotations.Token;
import com.fh.shop.api.biz.IOrderInfoService;
import com.fh.shop.api.common.SecretLogin;
import com.fh.shop.api.member.vo.MemberVo;
import com.fh.shop.api.order.biz.IOrderService;
import com.fh.shop.api.param.OrderParam;
import com.fh.shop.common.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/orders")
@Api(tags = "订单接口")
public class OrderController {


    @Autowired
    private HttpServletRequest request;
    @Resource(name = "orderService")
    private IOrderService orderService;
    @Resource(name = "orderInfoService")
    private IOrderInfoService orderInfoService;


    @PostMapping("/addOrder")
    @Check
    @ApiOperation("订单新增")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth",value = "请求头",paramType = "header",required = true,dataType = "java.lang.String"),
    })
    @Token
    public ServerResponse addOrder(OrderParam orderParam){
        MemberVo memberVo = (MemberVo) request.getAttribute(SecretLogin.MEMVO);
        Long id = memberVo.getId();
        orderParam.setMemberId(id);
        return  orderService.addOrder(orderParam);
    }


    @PostMapping("/findList")
    @Check
    @ApiOperation("订单查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth",value = "请求头",paramType = "header",required = true,dataType = "java.lang.String")
    })
    public ServerResponse findList(){
        MemberVo memberVo = (MemberVo) request.getAttribute(SecretLogin.MEMVO);
        Long id = memberVo.getId();
        return orderService.findList(id);
    }



    @PostMapping("/closeOrder")
    @Check
    @ApiOperation("取消订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth",value = "请求头",paramType = "header",required = true,dataType = "java.lang.String")
    })
    public ServerResponse closeOrder(String orderId){
        return orderInfoService.closeOrder(orderId);
    }



}
