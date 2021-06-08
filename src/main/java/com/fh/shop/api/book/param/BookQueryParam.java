package com.fh.shop.api.book.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fh.shop.common.page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel
public class BookQueryParam extends page implements Serializable {
    @ApiModelProperty(value = "图书名称",example = "0")
    private String bookName;
    @ApiModelProperty(value = "最小价格",example = "0")
    private BigDecimal minPrice;
    @ApiModelProperty(value = "最大价格",example = "0")
    private BigDecimal maxPrice;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @ApiModelProperty(value = "最小日期")
    private Date minDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @ApiModelProperty(value = "最大日期")
    private Date maxDate;
    @ApiModelProperty(value = "作者")
    private String bookUser;
}
