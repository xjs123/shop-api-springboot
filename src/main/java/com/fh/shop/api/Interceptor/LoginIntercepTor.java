package com.fh.shop.api.Interceptor;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.annotations.Check;
import com.fh.shop.api.common.KeyUtil;
import com.fh.shop.api.common.SecretLogin;
import com.fh.shop.api.exception.ShopException;
import com.fh.shop.api.member.vo.MemberVo;
import com.fh.shop.common.ResponseEnum;
import com.fh.shop.util.Md5Util;
import com.fh.shop.util.RedisUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Base64;

public class LoginIntercepTor extends HandlerInterceptorAdapter {


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //解决跨域
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,"*");

        //解决自定义heanders
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,"x-auth,content-type,x-token");

        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,"DELETE,POST,PUT,GET");
        //解决 options 请求
        String methodHTTP = request.getMethod();
        if(methodHTTP.equalsIgnoreCase("OPTIONS")){
            return false;
        }

        HandlerMethod handlerMethod= (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        if(!method.isAnnotationPresent(Check.class)){
            return true;
        }
        //获取头信息
        String header = request.getHeader("x-auth");
        //判断有没有头信息
        if(header==null){
            throw new ShopException(ResponseEnum.TOKEN_HEADER_IS_NULL);
        }

        //判断头信息是否规范
        String[] headerArr = header.split("\\.");
        if(headerArr.length!=2){
            throw new ShopException(ResponseEnum.TOKEN_HEADER_IS_FULL);
        }
        //然后就可以进行验签
        //获取签名
        String memberVo64 = headerArr[0];
        String sout64 = headerArr[1];
        String member = new String(Base64.getDecoder().decode(memberVo64),"utf-8");
        String sout = new String(Base64.getDecoder().decode(sout64),"utf-8");
        if(!Md5Util.sout(member,SecretLogin.SECRE).equals(sout)){
            throw new ShopException(ResponseEnum.TOKEN_HEADER_ERROR);
        }
        MemberVo membervo = JSON.parseObject(member, MemberVo.class);
        request.setAttribute(SecretLogin.MEMVO,membervo);

        Long id = membervo.getId();
        Boolean ex = RedisUtil.ex(KeyUtil.getksy(id));
        if(!ex){
            throw new ShopException(ResponseEnum.TOKEN_IS_NULL);
        }
        RedisUtil.er(KeyUtil.getksy(id),SecretLogin.SECODE);
        return true;
    }


}
