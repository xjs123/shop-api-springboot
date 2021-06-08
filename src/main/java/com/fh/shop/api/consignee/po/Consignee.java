package com.fh.shop.api.consignee.po;

import lombok.Data;

@Data
public class Consignee {

    private Long id;

    private Long memberId;

    private String consigneeName;

    private String phone;

    private String site;

    private int defaultSite;
}
