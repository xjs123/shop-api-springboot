package com.fh.shop.api.goods.biz;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.common.MailUtil;
import com.fh.shop.api.goods.vo.SkuVo;
import com.fh.shop.api.mapper.ISkuMapper;
import com.fh.shop.api.po.Sku;
import com.fh.shop.api.vo.SkuEmailVo;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.stream.Collectors;

@Service("skuService")
public class ISkuServiceImpl implements ISkuService {

    @Autowired
    private ISkuMapper skuMapper;
    @Autowired
    private MailUtil mailUtil;

    @Value("${fh-xjs}")
    private String to;

   public ServerResponse findStatusList(){
       //先看redis缓存有没有数据
       String skuVoLis = RedisUtil.get("skuList");
       if(StringUtils.isNotEmpty(skuVoLis)){
           List<SkuVo> skuList = JSON.parseArray(skuVoLis, SkuVo.class);
           return ServerResponse.success(skuList);
       }
        //redis没有值
       List<Sku> statusList = skuMapper.findStatusList();
       List<SkuVo> skuVoList = statusList.stream().map(a -> {
           SkuVo skuVo = new SkuVo();
           skuVo.setId(a.getId());
           skuVo.setImage(a.getImage());
           skuVo.setPrice(a.getPrice().toString());
           skuVo.setSkuName(a.getSkuName());
           return skuVo;
       }).collect(Collectors.toList());
       String skuList = JSON.toJSONString(skuVoList);
       RedisUtil.setEx("skuList",skuList,30);
       return ServerResponse.success(skuVoList);
   }

    @Override
    public void emailSku() {
       List<SkuEmailVo> skuEmailVos=skuMapper.selectSkuStock();
        ModelMap model = new ModelMap();
        String html = html(skuEmailVos, model);

//        StringBuilder html = new StringBuilder();
//        html.append("<table  border=\"1\" cellspacing=\"0\">\n" +
//                "<tr style='color:blue'>\n" +
//                "<td>sku名字</td>\n" +
//                "<td>价格</td>\n" +
//                "<td>库存</td>\n" +
//                "<td>品牌名</td>\n" +
//                "<td>分类名</td>\n" +
//                "</tr>");
//        for (SkuEmailVo sku : skuEmailVos) {
//            html.append("<tr style='color:red'>");
//            html.append("<td>" + sku.getSkuName() + "</td>");
//            html.append("<td>" + sku.getPrice()
// + "</td>");
//            html.append("<td>" + sku.getStock() + "</td>");
//            html.append("<td>" + sku.getBrandName() + "</td>");
//            html.append("<td>" + sku.getCateName() + "</td>");
//            html.append("</tr>");
//        }
//        html.append("</table>");

        mailUtil.sendMailHtml(to,"库存警告-薛金生",html);

    }

    public static String html(List<SkuEmailVo> skuEmailVos,ModelMap model){
        model.addAttribute("skus",skuEmailVos);
        return "mail";
    }


}
