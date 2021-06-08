package com.fh.shop.api.exception;

import com.fh.shop.common.ResponseEnum;

public class ShopException extends RuntimeException{


    private ResponseEnum responseEnum;


    public ShopException(ResponseEnum responseEnum){
        this.responseEnum=responseEnum;
    }


    public ResponseEnum getShopException(){
      return this.responseEnum;
    }
}
