package com.fh.shop.api.common;

import com.fh.shop.common.ValidateUtil;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@RestController
@RequestMapping("/api")
@Api(tags = "生成图片验证码")
public class ImageCodeController {

    @RequestMapping("/createImage")
    public void createImage(HttpServletRequest req, HttpServletResponse resp) {
        ValidateUtil.createImage(req, resp);
        System.out.println("------------"+req);
        System.out.println(resp);
    }
}
