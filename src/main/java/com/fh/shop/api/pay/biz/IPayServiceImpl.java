package com.fh.shop.api.pay.biz;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.fh.shop.api.configs.AlipayConfig;
import com.fh.shop.api.exception.ShopException;
import com.fh.shop.api.mapper.IMemberMapper;
import com.fh.shop.api.mapper.ISkuMapper;
import com.fh.shop.api.mq.PayProducer;
import com.fh.shop.api.mapper.IOrderItemMapper;
import com.fh.shop.api.mapper.IOrderMapper;
import com.fh.shop.api.po.Order;
import com.fh.shop.common.ResponseEnum;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.common.SystemConstant;
import com.fh.shop.configs.PayMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service("payService")
@Transactional(rollbackFor = Exception.class)
public class IPayServiceImpl implements IPayService {

    @Autowired
    private IOrderMapper orderMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private ISkuMapper skuMapper;
    @Autowired
    private IOrderItemMapper orderItemMapper;

    @Autowired
    private PayProducer payProducer;

    @Override
    public ServerResponse aliPay(String orderId) {
        //通过id查询订单
        Order order = orderMapper.selectById(orderId);
        if(order==null){
            return ServerResponse.error(ResponseEnum.Order_IS_NULL);
        }
        if(order.getStatus()!=SystemConstant.ORDER_STATUS.NOPAY){
            return ServerResponse.error(ResponseEnum.Order_IS_YES_NO_CLOSE);
        }

        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);

        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no =order.getId() ;
        //付款金额，必填
        String total_amount =order.getTotalPrice().toString();

        //订单名称，必填
        String subject = "薛金生的订单";
        //商品描述，可空
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        try {
            //请求
            String result = alipayClient.pageExecute(alipayRequest).getBody();
            System.out.println(result);
            //输出
            return ServerResponse.success(result);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw  new RuntimeException(e);
        }
    }

    @Override
    public String aliNotify(Map<String, String[]> requestParams) {
        Map<String,String> params = new HashMap<String,String>();
        try {
            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用
                valueStr = new String(valueStr.getBytes("utf-8"), "utf-8");
                params.put(name, valueStr);
            }
            System.out.println(params);
            boolean  signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名

            if(signVerified){
                    String trade_status = params.get("trade_status");
                    if(trade_status.equals("TRADE_SUCCESS")){

                        //进行商家的业务
                        String out_trade_no = params.get("out_trade_no");
                        Order order = orderMapper.selectById(out_trade_no);
                     //1、需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
                     if(order==null){
                         return "fail";
                     }
                     //2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
                        String total_amount = params.get("total_amount");
                        String orderPrice = order.getTotalPrice().toString();
                        if(!orderPrice.equals(total_amount)){
                            return "fail";
                        }
                        String seller_id = params.get("seller_id");
                        // 3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
                         if(!SystemConstant.SELLER_ID.equals(seller_id)){
                             return "fail";
                         }

                        //4、验证app_id是否为该商户本身。
                        String app_id = params.get("app_id");
                         if(!app_id.equals(AlipayConfig.app_id)){
                             return "fail";
                         }

                        //如果订单状态已经支付则没必要再次支付
                        if(order.getStatus()!=SystemConstant.ORDER_STATUS.NOPAY){
                            return "success";
                        }

                        //修改状态
                        Order order1 = new Order();
                        order1.setId(out_trade_no);
                        order1.setStatus(SystemConstant.ORDER_STATUS.YESPAY);
                        order1.setPayTime(new Date());
                        orderMapper.updateById(order1);

                        String count = params.get("receipt_amount");

                        PayMessage payMessage = new PayMessage();
                        payMessage.setMemberId(order.getMemberId());
                        payMessage.setCount(count);
                        payMessage.setOrderId(out_trade_no);

                        payProducer.paySend(payMessage);
//                        //给会员加积分
//
//                        double integral = Math.floor(Double.parseDouble(price));
//                        memberMapper.updateIntegral(order.getMemberId(), integral);
//
//                        //给sku加销量
//                        String id = order.getId();
//                        QueryWrapper<OrderItem> orderItemQueryWrapper = new QueryWrapper<>();
//                        orderItemQueryWrapper.eq("orderId",id);
//                        List<OrderItem> orderItems = orderItemMapper.selectList(orderItemQueryWrapper);
//                        orderItems.forEach(x->{
//                            Long skuId = x.getSkuId();
//                            long skuCount = x.getSkuCount();
//                            skuMapper.updateSales(skuId,skuCount);
//                        });
                        return "success";
                    }
                }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return "fail";
    }

    @Override
    public String returnUrl(HttpServletRequest request) {
        try {
            Map<String,String> params = new HashMap<String,String>();
            Map<String,String[]> requestParams = request.getParameterMap();
            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用
                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
                params.put(name, valueStr);
            }
            boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名

            //——请在这里编写您的程序（以下代码仅作参考）——
            if(signVerified) {

                //商户订单号
                String orderId = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

                //支付宝交易号
                String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

                //付款金额
                String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"),"UTF-8");


//                Order order = orderMapper.selectById(orderId);
//                if(order.getStatus()!=SystemConstant.ORDER_STATUS.NOPAY){
//                    throw new ShopException(ResponseEnum.TOKEN_ERROR);
//                }

                return total_amount;
            }else {
                throw new ShopException(ResponseEnum.ERROR);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
