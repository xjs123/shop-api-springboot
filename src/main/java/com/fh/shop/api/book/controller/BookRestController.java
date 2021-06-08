package com.fh.shop.api.book.controller;

import com.fh.shop.api.book.biz.IBookService;
import com.fh.shop.api.book.param.BookQueryParam;
import com.fh.shop.api.book.po.Book;
import com.fh.shop.common.DataTableResult;
import com.fh.shop.common.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;



@CrossOrigin
@RestController
@RequestMapping("/api")
@Api(tags = "图书管理")
public class BookRestController {

    @Resource(name = "bookService")
    private IBookService bookService;

    //新增
    @ApiOperation("图书新增")
    @RequestMapping(value = "/books",method = RequestMethod.POST)
    public ServerResponse addBook(Book book){
        return bookService.addBook(book);
    }

    //查询
    @ApiOperation("图书查询")
    @RequestMapping(value = "/books",method = RequestMethod.GET)
    public DataTableResult findList(BookQueryParam bookQueryParam){
        return bookService.findList(bookQueryParam);
    }

    //删除
    @ApiOperation("图书删除")
    @ApiImplicitParam(value = "图书删除",name = "id",example = "0",required = true,dataType  = "java.lang.Long",paramType = "path")
    @RequestMapping(value = "/books/{id}",method = RequestMethod.DELETE)
    public ServerResponse deleteBook(@PathVariable("id")Long id){
        return bookService.deleteBook(id);
    }

    //批量删除
    @ApiOperation("图书批量删除")
    @RequestMapping(value = "/books",method = RequestMethod.DELETE)
    @ApiImplicitParam(name = "ids",value = "批量删除根据id，1，2，3",required = true,dataType = "java.lang.String",paramType = "query")
    public ServerResponse deleteBatch(String ids){
        return bookService.deleteBatch(ids);
    }

    //修改
    @ApiOperation("图书修改")
    @RequestMapping(value = "/books",method = RequestMethod.PUT)
    public  ServerResponse updateBook(@RequestBody Book book){
            return bookService.updateBook(book);
    }

    //回填的查询
    @ApiOperation("图书回填查询")
    @RequestMapping(value = "/books/{id}",method = RequestMethod.GET)
    @ApiImplicitParam(name = "id",value = "根据id查询单条数据",example = "0",required = true,paramType = "path",dataType = "java.lang.Long")
    public ServerResponse findBookById(@PathVariable("id")Long id){
            return bookService.findBookById(id);
    }


}
