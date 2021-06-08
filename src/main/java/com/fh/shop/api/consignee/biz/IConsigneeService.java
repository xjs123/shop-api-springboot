package com.fh.shop.api.consignee.biz;


import com.fh.shop.api.consignee.po.Consignee;
import com.fh.shop.common.ServerResponse;

public interface IConsigneeService {
    ServerResponse addConfignee(Consignee consignee);

    ServerResponse findConfignee(Long id);

    ServerResponse updateConsignee( Consignee consignee);

    ServerResponse deleteConfignee( Long id);

    ServerResponse updateStatus(Long memberId, Long id);

    ServerResponse selectById( Long id);
}
