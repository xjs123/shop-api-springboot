package com.fh.shop.api.common;

import com.alibaba.fastjson.JSON;
import com.fh.shop.common.ResponseEnum;
import com.fh.shop.common.SMSutil;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.common.ZzUtil;
import com.fh.shop.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin
@Api(tags = "发送短信")
@RequestMapping("/api")
public class SMSController {

        @ApiOperation("发送短信")
        @PostMapping("/sms/getSms")
        @ApiImplicitParam(name = "phone",value = "手机号",dataType = "java.lang.String",required = true)
        public ServerResponse getSms(String phone){
            //判断手机号为不为空
            if(StringUtils.isEmpty(phone)){
                ServerResponse.error(ResponseEnum.MEMBER_PHONE_ISNULL);
            }
            //判断手机号合法嘛
            Boolean zzphone = ZzUtil.Zzphone(phone);
            if(zzphone!=true){
                ServerResponse.error(ResponseEnum.PHONE_ERROR);
            }
            //调用工具类发送验证码
            String res = SMSutil.getSmS(phone);
            Map map = JSON.parseObject(res, Map.class);
            //获取验证码存入redis
             int smsCode= (int) map.get("code");
            if(smsCode!=200){
                ServerResponse.error(ResponseEnum.SMS_CODE_ERROR);
            }
            String code = (String) map.get("obj");
            RedisUtil.setEx(phone,code,5*60);
            return ServerResponse.success(ResponseEnum.SMS_CODE_SUCCESS);
        }

}
