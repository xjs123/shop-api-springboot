package com.fh.shop.api.exception;

import com.fh.shop.common.ResponseEnum;
import com.fh.shop.common.ServerResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerException {

    @ExceptionHandler(ShopException.class)
    public ServerResponse exception(ShopException ex){
        ResponseEnum shopEnum = ex.getShopException();
        return ServerResponse.error(shopEnum);
    }

}
