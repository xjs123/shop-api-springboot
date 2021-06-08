package com.fh.shop.api.consignee.controller;

import com.fh.shop.api.annotations.Check;
import com.fh.shop.api.common.SecretLogin;
import com.fh.shop.api.consignee.biz.IConsigneeService;
import com.fh.shop.api.consignee.po.Consignee;
import com.fh.shop.api.member.vo.MemberVo;
import com.fh.shop.common.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/consignees")
@Api(tags = "收件人接口")
public class ConsigneeController {
        @Resource(name = "consigneeService")
        private IConsigneeService configneeService;

        @Autowired
        private HttpServletRequest request;



        @PostMapping("/addConfignee")
        @Check
        @ApiImplicitParams({
                @ApiImplicitParam(name = "x-auth",value = "请求头",paramType = "header",required = true,dataType = "java.lang.String"),
        })
        public ServerResponse addConfignee(Consignee consignee){
            MemberVo member = (MemberVo) request.getAttribute(SecretLogin.MEMVO);
            Long id = member.getId();
            consignee.setMemberId(id);
            return  configneeService.addConfignee(consignee);
        }


    @GetMapping("/findConfignee")
    @Check
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth",value = "请求头",paramType = "header",required = true,dataType = "java.lang.String"),
    })
    public ServerResponse findConfignee(){
        MemberVo member = (MemberVo) request.getAttribute(SecretLogin.MEMVO);
        Long id = member.getId();
        return  configneeService.findConfignee(id);
    }

    @PostMapping("/updateConsignee")
    @Check
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth",value = "请求头",paramType = "header",required = true,dataType = "java.lang.String"),
    })
    public ServerResponse updateConfignee(Consignee consignee){

        return  configneeService.updateConsignee(consignee);
    }


    @PostMapping("/updateStatus")
    @Check
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth",value = "请求头",paramType = "header",required = true,dataType = "java.lang.String"),
    })
    public ServerResponse updateStatus(Long id){
        MemberVo member = (MemberVo) request.getAttribute(SecretLogin.MEMVO);
        Long memberId = member.getId();
        return  configneeService.updateStatus(memberId,id);
    }


    @DeleteMapping("/deleteConfignee")
    @Check
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth",value = "请求头",paramType = "header",required = true,dataType = "java.lang.String"),
    })
    public ServerResponse deleteConfignee(Long id){
        return  configneeService.deleteConfignee(id);
    }


    @GetMapping("/selectById")
    @Check
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth",value = "请求头",paramType = "header",required = true,dataType = "java.lang.String"),
    })
    public ServerResponse selectById(Long id){

        return  configneeService.selectById(id);
    }











}
