package com.fh.shop.api.goods.controller;

import com.fh.shop.api.goods.biz.ISkuService;
import com.fh.shop.common.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api")
@Api(tags = "商品管理")
public class SkuController {

        @Resource(name = "skuService")
        private ISkuService skuService;

        @ApiOperation("商品查询")
        @GetMapping("/skus/status")
        public ServerResponse findStatusList(){
            return skuService.findStatusList();
        }



}
