package com.fh.shop.api.code;

import com.fh.shop.api.common.KeyUtil;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.util.RedisUtil;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class CodeController {

    @Autowired
    private DefaultKaptcha defaultKaptcha;
    @Autowired
    private HttpServletResponse response;


    @GetMapping("/code")
    public ServerResponse code(String id){
        //生成文字码
        String text = defaultKaptcha.createText();
        //存数据
        //如果为空，就重新生成
        if (StringUtils.isEmpty(id)){
            id = UUID.randomUUID().toString();
        }
        //存在就覆盖
        RedisUtil.setEx(KeyUtil.buildImageCodeKey(id),text,5*60);
        //根据文字码 生成对应的图片码
        BufferedImage image = defaultKaptcha.createImage(text);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            //将动态生成的图片，写入到ByteArrayOutputStream
            ImageIO.write(image, "jpg", bos);
            //将ByteArrayOutputStream转换为对应的字节数据
            byte[] bytes = bos.toByteArray();
            //将字节数据，进行base64转码
            String imageBase64 = Base64.getEncoder().encodeToString(bytes);
            Map<String,String> map = new HashMap<>();
            map.put("id",id);
            map.put("imageByte",imageBase64);
            return ServerResponse.success(map);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }




    @GetMapping("/code2")
    public void code2(){
        String text = defaultKaptcha.createText();
        BufferedImage image = defaultKaptcha.createImage(text);
        ServletOutputStream outputStream =null;
        try {
            // Set to expire far in the past.
            response.setDateHeader("Expires", 0);
            // Set standard HTTP/1.1 no-cache headers.
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            // Set standard HTTP/1.0 no-cache header.
            response.setHeader("Pragma", "no-cache");
            // return a jpeg
            response.setContentType("image/jpeg");


            outputStream = response.getOutputStream();
            ImageIO.write(image, "jpg", outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
