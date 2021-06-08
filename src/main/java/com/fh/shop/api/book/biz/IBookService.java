package com.fh.shop.api.book.biz;

import com.fh.shop.api.book.param.BookQueryParam;
import com.fh.shop.api.book.po.Book;
import com.fh.shop.common.DataTableResult;
import com.fh.shop.common.ServerResponse;


public interface IBookService {
    DataTableResult findList(BookQueryParam bookQueryParam);

    ServerResponse addBook(Book book);

    ServerResponse deleteBook(Long id);

    ServerResponse deleteBatch(String ids);

    ServerResponse updateBook(Book book);

    ServerResponse findBookById(Long id);

}
