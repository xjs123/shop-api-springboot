package com.fh.shop.api.book.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;

import java.util.Date;

@Data
@ApiModel
public class Book implements Serializable {

    @ApiModelProperty(value = "主键id",example = "0")
    private Long id;
    @ApiModelProperty(value = "图书名称",required = true)
    private  String  bookName;
    @ApiModelProperty(value = "图书价格",required = true,example = "0")
    private BigDecimal price;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @ApiModelProperty(value = "出版时间",required = true)
    private Date bookDate;
    @ApiModelProperty(value = "图书作者",required = true)
    private String bookUser;

}
