package com.fh.shop.api.bookinfo.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data

public class BookInfoParam implements Serializable {

    private String bookName;

    private String author;

    private BigDecimal bookPrice;

    private String pubilshDate;


}
