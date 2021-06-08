package com.fh.shop.api.consignee.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fh.shop.api.consignee.mapper.IConsigneeMapper;
import com.fh.shop.api.consignee.po.Consignee;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.common.SystemConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("consigneeService")
public class IConfigneeServiceImpl implements IConsigneeService {


    @Autowired
    private IConsigneeMapper consigneeMapper;

    @Override
    public ServerResponse addConfignee(Consignee consignee) {
        consigneeMapper.insert(consignee);
        return ServerResponse.success();
    }

    @Override
    public ServerResponse findConfignee(Long id) {
        QueryWrapper<Consignee> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("memberId",id);
        List<Consignee> consigneeList = consigneeMapper.selectList(queryWrapper);
        return ServerResponse.success(consigneeList);
    }

    @Override
    public ServerResponse updateConsignee(Consignee consignee) {
        consigneeMapper.updateById(consignee);
        return ServerResponse.success();
    }

    @Override
    public ServerResponse deleteConfignee( Long id) {
        consigneeMapper.deleteById(id);
        return ServerResponse.success();
    }

    @Override
    public ServerResponse updateStatus(Long memberId, Long id) {
        //首先将该会员数据库中的状态都改为0
        Consignee consignee = new Consignee();
        consignee.setDefaultSite(SystemConstant.NO_CONSIGNEESTATUS);
        QueryWrapper<Consignee> updateWrapper = new QueryWrapper<>();
        updateWrapper.eq("memberId",memberId);
        consigneeMapper.update(consignee, updateWrapper);
        //然后修改指定的收件人id
        Consignee consignee1 = new Consignee();
        consignee1.setDefaultSite(SystemConstant.CONSIGNEESTATUS);
        QueryWrapper<Consignee> updateWrapper1 = new QueryWrapper<>();
        updateWrapper1.eq("id",id);
        consigneeMapper.update(consignee1, updateWrapper1);
        return ServerResponse.success();
    }

    @Override
    public ServerResponse selectById(Long id) {
        Consignee consignee = consigneeMapper.selectById(id);

        return ServerResponse.success(consignee);
    }


//    @Override
//    public ServerResponse addConfignee(Consignee consignee) {
//        String consigneeName = consignee.getConsigneeName();
//        String phone = consignee.getPhone();
//        String site = consignee.getSite();
//        if(StringUtils.isEmpty(consigneeName) || StringUtils.isEmpty(phone) || StringUtils.isEmpty(site)){
//            return ServerResponse.error(ResponseEnum.CONSIGNEE_IS_NULL);
//        }
//        int i = (int) ((Math.random() * 9 + 1) * 1000);
//
//        String consigneeStr = JSON.toJSONString(consignee);
//        RedisUtil.hset(KeyUtil.MemBerConsignee(consignee.getMemberId()+""),KeyUtil.ConsigneeKey(i+""),consigneeStr);
//        return ServerResponse.success();
//    }
//
//    @Override
//    public ServerResponse findConfignee(Long id) {
//        Map<String, String> stringStringMap = RedisUtil.hgetAll(KeyUtil.MemBerConsignee(id + ""));
//        List<Consignee> consigneeList=new ArrayList<>();
//        stringStringMap.forEach((x,y)-> {
//            String[] split = x.split(":");
//            String s = split[1];
//            Consignee consignee = JSON.parseObject(y, Consignee.class);
//            consignee.setId(Long.parseLong(s));
//            consigneeList.add(consignee);
//        });
//        return ServerResponse.success(consigneeList);
//    }
//
//    @Override
//    public ServerResponse updateConfignee(Long memberId, Consignee consignee) {
//        String consigneeName = consignee.getConsigneeName();
//        String phone = consignee.getPhone();
//        String site = consignee.getSite();
//        Long id = consignee.getId();
//        if(StringUtils.isEmpty(consigneeName) || StringUtils.isEmpty(phone) || StringUtils.isEmpty(site)){
//            return ServerResponse.error(ResponseEnum.CONSIGNEE_IS_NULL);
//        }
//        //先删除
//        Map<String, String> stringStringMap = RedisUtil.hgetAll(KeyUtil.MemBerConsignee(memberId + ""));
//        stringStringMap.forEach((x,y)-> {
//            Consignee consigneeInfo = JSON.parseObject(y, Consignee.class);
//            if(id==consigneeInfo.getId()){
//                stringStringMap.remove(KeyUtil.ConsigneeKey(id+""));
//            }
//        });
//        //刷新redis
//        stringStringMap.put(KeyUtil.ConsigneeKey(id+""),JSON.toJSONString(consignee));
//        RedisUtil.hmset(KeyUtil.MemBerConsignee(memberId+""),stringStringMap);
//        return ServerResponse.success();
//    }
//
//    @Override
//    public ServerResponse deleteConfignee(Long memberId, Long id) {
//
//        Map<String, String> stringStringMap = RedisUtil.hgetAll(KeyUtil.MemBerConsignee(memberId + ""));
//                stringStringMap.remove(KeyUtil.ConsigneeKey(id+""));
//        RedisUtil.hdel(KeyUtil.MemBerConsignee(memberId+""),KeyUtil.ConsigneeKey(id+""));
//        RedisUtil.hmset(KeyUtil.MemBerConsignee(memberId+""),stringStringMap);
//        return ServerResponse.success();
//    }
}
