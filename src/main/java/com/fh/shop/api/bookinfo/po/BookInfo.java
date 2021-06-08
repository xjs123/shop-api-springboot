package com.fh.shop.api.bookinfo.po;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
@ApiModel
@Data
public class BookInfo implements Serializable {

    private Long id;

    private String bookName;


    private String author;

    private BigDecimal bookPrice;

    private Date pubilshDate;

    private Date insertDate;

}
