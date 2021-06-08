package com.fh.shop.api.bookinfo.controller;

import com.fh.shop.api.bookinfo.biz.IBookInfoService;
import com.fh.shop.api.bookinfo.param.BookInfoParam;
import com.fh.shop.common.TResponse;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api")
@Api(tags = "图书2")
public class BookInfoController {

    @Resource(name = "bookinfoService")
    private IBookInfoService bookInfoService;


    @PostMapping("/books/addBook")
    public TResponse addBook(@RequestBody BookInfoParam bookInfoParam){
    return bookInfoService.addBook(bookInfoParam);
    }

}
