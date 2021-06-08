package com.fh.shop.api.token.biz;

import com.fh.shop.api.common.KeyUtil;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.util.RedisUtil;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("tokenService")
public class ITokenServiceImpl implements ITokenService {
    @Override
    public ServerResponse creatToken() {
        String token = UUID.randomUUID().toString();
        RedisUtil.setEx(KeyUtil.TockenKey(token),"",10*60);
        return ServerResponse.success(token);
    }
}
