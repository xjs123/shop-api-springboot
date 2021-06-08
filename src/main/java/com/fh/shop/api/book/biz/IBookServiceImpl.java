package com.fh.shop.api.book.biz;

import com.fh.shop.api.book.mapper.IBookMapper;
import com.fh.shop.api.book.param.BookQueryParam;
import com.fh.shop.api.book.po.Book;
import com.fh.shop.common.DataTableResult;
import com.fh.shop.common.ServerResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("bookService")
public class IBookServiceImpl implements IBookService {

    @Autowired
    private IBookMapper bookMapper;


    @Override
    public DataTableResult findList(BookQueryParam bookQueryParam) {
        //求总条数
        Long count=bookMapper.findCount(bookQueryParam);

        List<Book> bookList=bookMapper.findPageList(bookQueryParam);

        return new DataTableResult(bookQueryParam.getDraw(),count,count,bookList);
    }

    @Override
    public ServerResponse addBook(Book book) {
        bookMapper.insert(book);
        return ServerResponse.success();
    }

    @Override
    public ServerResponse deleteBook(Long id) {
        bookMapper.deleteById(id);
        return ServerResponse.success();
    }

    @Override
    public ServerResponse deleteBatch(String ids) {
        if(StringUtils.isEmpty(ids)){
            return ServerResponse.error();
        }
        String[] idsArr = ids.split(",");
        List<Long> idsList = Arrays.stream(idsArr).map(a -> Long.parseLong(a)).collect(Collectors.toList());
        bookMapper.deleteBatchIds(idsList);
        return ServerResponse.success();
    }

    @Override
    public ServerResponse updateBook(Book book) {
        bookMapper.updateById(book);
        return ServerResponse.success();
    }

    @Override
    public ServerResponse findBookById(Long id) {
        Book book = bookMapper.selectById(id);

        return ServerResponse.success(book);
    }
}
