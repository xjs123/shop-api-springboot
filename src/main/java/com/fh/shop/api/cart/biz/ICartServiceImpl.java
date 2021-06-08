package com.fh.shop.api.cart.biz;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.cart.vo.CartVo;
import com.fh.shop.api.common.KeyUtil;
import com.fh.shop.api.mapper.ISkuMapper;
import com.fh.shop.api.po.Sku;
import com.fh.shop.api.vo.CartSkuVo;
import com.fh.shop.common.ResponseEnum;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.common.SystemConstant;
import com.fh.shop.util.BigDecimalUtil;
import com.fh.shop.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service("cartService")
public class ICartServiceImpl implements ICartService {
    @Autowired
    private ISkuMapper skuMapper;


    @Override
    public ServerResponse addCart(Long id, Long skuId, Long count) {
        if(count>10){
            return ServerResponse.success(ResponseEnum.CART_SKU_SO_MANNY);
        }
        //1·判断 有否有没有该商品
        Sku sku = skuMapper.selectById(skuId);
        if(sku==null){
            return ServerResponse.error(ResponseEnum.CART_SKU_IS_NULL);
        }
        //2·判断该商品是否上架
        if(sku.getStatus().equals(SystemConstant.CARTSTATUS)){
            return ServerResponse.error(ResponseEnum.CART_SKU_STATUS_IS_NO);
        }
        //3·判断库存是否大于用户所添加的数量
        if(sku.getStock()<count.intValue()){
            return ServerResponse.error(ResponseEnum.CART_SKU_STOCK_NOT);
        }
        //4·需要判断redis中有没有购物车
        String key = KeyUtil.buildCartKey(id);

        String cart = RedisUtil.hget(key, SystemConstant.FIELD);
        if(StringUtils.isEmpty(cart)){
            if(count<0){
                return ServerResponse.error(ResponseEnum.CART_SKU_110_error);
            }
            //·如果没有购物车创建一个购物车并且插入该商品
            CartVo cartVo = new CartVo();
            CartSkuVo cartSkuVo = new CartSkuVo();
            cartSkuVo.setCount(count);
            cartSkuVo.setImage(sku.getImage());
            String price = sku.getPrice().toString();
            cartSkuVo.setPrice(price);
            cartSkuVo.setSkuName(sku.getSkuName());
            cartSkuVo.setSkuId(skuId);
            String sumprice = BigDecimalUtil.mul(price, count+"").toString();
            cartSkuVo.setSubPrice(sumprice);
            cartVo.getCartVoList().add(cartSkuVo);
            cartVo.setCartCount(count);
            cartVo.setSumPrice(sumprice);
            Map<String, String> map = new HashMap<>();
            map.put(SystemConstant.FIELD,JSON.toJSONString(cartVo));
            map.put(SystemConstant.COUNT,count+"");
            RedisUtil.hmset(key,map);

        }else {
            //6·如果有购物车
            //·判断购物车中的商品是否存在
            CartVo cartVo = JSON.parseObject(cart, CartVo.class);
            List<CartSkuVo> cartVoList = cartVo.getCartVoList();
            Optional<CartSkuVo> first = cartVoList.stream().filter(x -> x.getSkuId().longValue() == skuId.longValue()).findFirst();

            //·已经存在的话 需要修改商品的数量和小计
            if(first.isPresent()){
                CartSkuVo cartSkuVo = first.get();
                Long count1 = cartSkuVo.getCount();
                Long newcount = count1.longValue() + count.longValue();
                if(newcount>10){
                    return ServerResponse.success(ResponseEnum.CART_SKU_SO_MANNY);
                }
                if(newcount<1){
                    //我们需要删除这件商品
                    cartVoList.removeIf(x->x.getSkuId().longValue()==skuId.longValue());
                    updateCart(key, cartVo, cartVoList);
                    return ServerResponse.success();
                }
                if(cartVoList.size()==0){
                    RedisUtil.del(key);
                    return ServerResponse.error(ResponseEnum.CART_SKU_NULL);
                }
                cartSkuVo.setCount(newcount);
                String price = cartSkuVo.getPrice();
                BigDecimal pricebig = new BigDecimal(cartSkuVo.getSubPrice());
                String newsubprice = pricebig.add(BigDecimalUtil.mul(price, count+"")).toString();
                cartSkuVo.setSubPrice(newsubprice);
                //7·更新购物车
                updateCart(key, cartVo, cartVoList);
            }else {
                if(count<0){
                    return ServerResponse.error(ResponseEnum.CART_SKU_110_error);
                }
                //不存在的话直接添加
                CartSkuVo cartSkuVo = new CartSkuVo();
                cartSkuVo.setCount(count);
                String price = sku.getPrice().toString();
                cartSkuVo.setPrice(price);
                cartSkuVo.setImage(sku.getImage());
                cartSkuVo.setSkuName(sku.getSkuName());
                cartSkuVo.setSkuId(skuId);
                String sumprice = BigDecimalUtil.mul(price, count+"").toString();
                cartSkuVo.setSubPrice(sumprice);
                cartVo.getCartVoList().add(cartSkuVo);
                updateCart(key, cartVo, cartVoList);
            }
        }
        return ServerResponse.success();
    }

    private void updateCart(String key, CartVo cartVo, List<CartSkuVo> cartVoList) {
        long countAll=0;
        BigDecimal priceAll = new BigDecimal(0);
        for (CartSkuVo vo : cartVoList) {
            countAll+=vo.getCount();
            priceAll=priceAll.add(new BigDecimal(vo.getSubPrice()));
        }
        cartVo.setCartCount(countAll);
        cartVo.setSumPrice(priceAll.toString());
        Map<String, String> map = new HashMap<>();
        map.put(SystemConstant.FIELD,JSON.toJSONString(cartVo));
        map.put(SystemConstant.COUNT,+cartVo.getCartCount()+"");
        RedisUtil.hmset(key,map);
    }

    @Override
    public ServerResponse findCart(Long id) {
        String cartAllStr = RedisUtil.hget(KeyUtil.buildCartKey(id),SystemConstant.FIELD);
        if(StringUtils.isEmpty(cartAllStr)){
            return ServerResponse.error(ResponseEnum.CART_SKU_NULL);
        }
        CartVo cartVo = JSON.parseObject(cartAllStr, CartVo.class);
        if(cartVo.getCartVoList().size()==0){
            return ServerResponse.error(ResponseEnum.CART_SKU_NULL);
        }
        return ServerResponse.success(cartVo);
    }

    @Override
    public ServerResponse deleteCartSku(Long id, Long skuId) {
        String key = KeyUtil.buildCartKey(id);
        String cartAllStr = RedisUtil.hget(key,SystemConstant.FIELD);
        CartVo cartVo = JSON.parseObject(cartAllStr, CartVo.class);
        List<CartSkuVo> cartVoList = cartVo.getCartVoList();
        Optional<CartSkuVo> first = cartVoList.stream().filter(x -> x.getSkuId().longValue() == skuId.longValue()).findFirst();
        if(!first.isPresent()){
            return ServerResponse.error(ResponseEnum.CART_SKU_110_error);
        }
        cartVoList.removeIf(x->x.getSkuId().longValue()==skuId.longValue());
        if(cartVoList.size()==0){
            RedisUtil.del(key);
            return ServerResponse.error(ResponseEnum.CART_SKU_NULL);
        }
        updateCart(key, cartVo, cartVoList);
        return ServerResponse.success();
    }

    @Override
    public ServerResponse deleteBatchCartSku(Long id, String ids) {
        if(StringUtils.isEmpty(ids)){
            return ServerResponse.error(ResponseEnum.SPEC_ERROR_NULL);
        }
        String key = KeyUtil.buildCartKey(id);
        String cartAllStr = RedisUtil.hget(key,SystemConstant.FIELD);
        CartVo cartVo = JSON.parseObject(cartAllStr, CartVo.class);
        List<CartSkuVo> cartVoList = cartVo.getCartVoList();
        Arrays.stream(ids.split(",")).forEach(x->cartVoList.removeIf(y->y.getSkuId().longValue()==Long.parseLong(x)));
        if(cartVoList.size()==0){
            RedisUtil.del(key);
            return ServerResponse.error(ResponseEnum.CART_SKU_NULL);
        }
        updateCart(key, cartVo, cartVoList);
        return ServerResponse.success();
    }


}
