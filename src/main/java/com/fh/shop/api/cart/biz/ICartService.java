package com.fh.shop.api.cart.biz;

import com.fh.shop.common.ServerResponse;

public interface ICartService {
    public ServerResponse addCart(Long id,Long skuId,Long count);

    ServerResponse
    findCart(Long id);


    ServerResponse deleteCartSku(Long id, Long skuId);

    ServerResponse deleteBatchCartSku(Long id, String ids);
}
