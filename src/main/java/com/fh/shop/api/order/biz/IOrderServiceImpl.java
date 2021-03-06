package com.fh.shop.api.order.biz;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fh.shop.api.cart.vo.CartVo;
import com.fh.shop.api.common.KeyUtil;
import com.fh.shop.api.consignee.mapper.IConsigneeMapper;
import com.fh.shop.api.consignee.po.Consignee;
import com.fh.shop.api.exception.ShopException;
import com.fh.shop.api.mapper.IMemberMapper;
import com.fh.shop.api.mapper.IOrderItemMapper;
import com.fh.shop.api.mapper.IOrderMapper;
import com.fh.shop.api.mapper.ISkuMapper;
import com.fh.shop.api.mq.OrderProducer;
import com.fh.shop.api.param.OrderParam;
import com.fh.shop.api.po.Order;
import com.fh.shop.api.po.OrderItem;
import com.fh.shop.api.po.Sku;
import com.fh.shop.api.vo.CartSkuVo;
import com.fh.shop.api.vo.OrderVo;
import com.fh.shop.common.ResponseEnum;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.common.SystemConstant;
import com.fh.shop.util.DateForMat;
import com.fh.shop.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service("orderService")
@Transactional(rollbackFor = Exception.class)
public class IOrderServiceImpl implements IOrderService {

    @Autowired
    private IOrderItemMapper orderItemMapper;
    @Autowired
    private IOrderMapper orderMapper;
    @Autowired
    private ISkuMapper skuMapper;
    @Autowired
    private IConsigneeMapper consigneeMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private OrderProducer orderProducer;
    @Value("${order.status.ok}")
    private Integer yesOrder;


    @Override
    public ServerResponse addOrder(OrderParam orderParam) {
        if(orderParam.getConsigneeId()==null){
            return ServerResponse.error(ResponseEnum.Order_CONSIGNEE_IS_NULL);
        }
        //??????redis??????????????????
        Long memberId = orderParam.getMemberId();
        String cartVoRedis = RedisUtil.hget(KeyUtil.buildCartKey(memberId), SystemConstant.FIELD);
        if(StringUtils.isEmpty(cartVoRedis)){
            return ServerResponse.error(ResponseEnum.CART_SKU_NULL);
        }
        CartVo cartVo = JSON.parseObject(cartVoRedis, CartVo.class);
        List<CartSkuVo> cartVoList = cartVo.getCartVoList();
        //???????????????
        //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        //?????????????????????????????????????????????

        for (CartSkuVo cartSkuVo : cartVoList) {
            //???????????????????????????????????????????????????
            Sku sku = skuMapper.selectById(cartSkuVo.getSkuId());

            if(cartSkuVo.getCount()>sku.getStock()){
                throw new ShopException(ResponseEnum.STOCK_IN_ERROR);
            }

            //???????????????????????????
            Integer a=skuMapper.updateSkuStock(cartSkuVo);

            if(a==0){
            //?????????????????? ?????????????????????
                throw new ShopException(ResponseEnum.STOCK_IN_ERROR);
           }
        }
        //????????????
        Order order = new Order();
        order.setMemberId(memberId);
        order.setTotalCount(cartVo.getCartCount());
        order.setTotalPrice(new BigDecimal(cartVo.getSumPrice()));
        String idStr = IdWorker.getIdStr();
        order.setId(idStr);
        order.setCreateTime(new Date());
        order.setStatus(SystemConstant.buildOrder);
        order.setPayType(SystemConstant.wechat);

        //??????i????????????????????????
        Consignee consignee = consigneeMapper.selectById(orderParam.getConsigneeId());
        order.setConsigneePhone(consignee.getPhone());
        order.setConsigneeAddr(consignee.getSite());
        order.setConsigneeName(consignee.getConsigneeName());
        if(order!=null){
            orderMapper.insert(order);
        }
        String orderId = order.getId();
        //?????????????????????
        List<OrderItem> orderItemList = cartVoList.stream().map(x -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setMemberId(orderParam.getMemberId());
            orderItem.setOrderId(orderId);
            orderItem.setSkuCount(x.getCount());
            orderItem.setSkuId(x.getSkuId());
            orderItem.setSkuName(x.getSkuName());
            orderItem.setSkuImage(x.getImage());
            orderItem.setSkuPrice(new BigDecimal(x.getPrice()));
            orderItem.setSubPrice(new BigDecimal(x.getSubPrice()));
            return orderItem;
        }).collect(Collectors.toList());
        //???????????????????????????
        if(orderItemList.size()>0){
            orderItemMapper.addList(orderItemList);
        }
        RedisUtil.del(KeyUtil.buildCartKey(memberId));
       // sendDelayMessageCancelOrder(orderId);
        orderProducer.sendOrderMessage(orderId,"30000");
        return ServerResponse.success();
    }








    @Override
    public ServerResponse findList( Long id) {
        //??????????????????
        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("memberId",id);
        List<Order> orderList = orderMapper.selectList(orderQueryWrapper);

        //??????stream??????????????? vo
        List<OrderVo> orderVoList = orderList.stream().map(x -> {
            OrderVo orderVo = new OrderVo();
            orderVo.setId(x.getId());
            orderVo.setConsigneeAddr(x.getConsigneeAddr());
            orderVo.setConsigneeName(x.getConsigneeName());
            orderVo.setConsigneePhone(x.getConsigneePhone());
            orderVo.setCreateTime(DateForMat.date2str(x.getCreateTime(),DateForMat.Date_Str));
            orderVo.setPayType(x.getPayType());
            orderVo.setStatus(x.getStatus());
            orderVo.setTotalCount(x.getTotalCount());
            orderVo.setTotalPrice(x.getTotalPrice().toString());
            return orderVo;
        }).collect(Collectors.toList());
        return ServerResponse.success(orderVoList);
    }


}
