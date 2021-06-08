package com.fh.shop.api.token.controller;

import com.fh.shop.api.token.biz.ITokenService;
import com.fh.shop.common.ServerResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/tokens")
public class TokenController {

    @Resource(name = "tokenService")
    private ITokenService tokenService;

    @GetMapping("/creatToken")
    public ServerResponse creatToken(){
        return tokenService.creatToken();
    }


}
