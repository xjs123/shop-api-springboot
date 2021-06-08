package com.fh.shop.api.Interceptor;

import com.fh.shop.api.annotations.Check;
import com.fh.shop.api.annotations.Token;
import com.fh.shop.api.common.KeyUtil;
import com.fh.shop.api.exception.ShopException;
import com.fh.shop.common.ResponseEnum;
import com.fh.shop.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class TokenIntercepTor extends HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HandlerMethod handlerMethod= (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        if(!method.isAnnotationPresent(Token.class)){
            return true;
        }
        String header = request.getHeader("x-token");
        if(StringUtils.isEmpty(header)){
            throw  new ShopException(ResponseEnum.TOKEN_HEADER_IS_NULL);
        }
        Long status = RedisUtil.del(KeyUtil.TockenKey(header));
        if(status==0){
            throw new ShopException(ResponseEnum.TOKEN_ERROR);
        }
        return true;
    }
}
