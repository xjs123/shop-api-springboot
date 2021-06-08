package com.fh.shop.api.cate.controller;

import com.fh.shop.api.cate.biz.ICateService;
import com.fh.shop.common.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;



@RestController
@RequestMapping("/api")
@Api(tags = "类型")
public class CateController {

    @Resource(name = "cateService")
    private ICateService cateService;


    @RequestMapping(value = "/cates",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询分类")
    public ServerResponse findCate(){
        return  cateService.findCate();
    }


}
