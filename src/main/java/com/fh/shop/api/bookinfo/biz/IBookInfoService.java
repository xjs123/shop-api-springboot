package com.fh.shop.api.bookinfo.biz;

import com.fh.shop.api.bookinfo.param.BookInfoParam;
import com.fh.shop.common.TResponse;

public interface IBookInfoService {
    TResponse addBook(BookInfoParam bookInfoParam);

}
