package com.fh.shop.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan({"com.fh.shop.api.*.mapper","com.fh.shop.api.mapper"})
@EnableScheduling
public class ShopApiSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopApiSpringbootApplication.class, args);
    }


}
