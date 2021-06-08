package com.fh.shop.api.bookinfo.biz;

import com.fh.shop.api.bookinfo.mapper.IBookInfoMapper;
import com.fh.shop.api.bookinfo.param.BookInfoParam;
import com.fh.shop.api.bookinfo.po.BookInfo;
import com.fh.shop.common.TResponse;
import com.fh.shop.util.DateForMat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service("bookinfoService")
@Transactional(rollbackFor = Exception.class)
public class IBookInfoServiceImpl implements IBookInfoService {


    @Autowired
    private IBookInfoMapper bookInfoMapper;

    @Override
    public TResponse addBook(BookInfoParam bookInfoParam) {
        BookInfo bookInfo = new BookInfo();
        bookInfo.setBookName(bookInfoParam.getBookName());
        bookInfo.setAuthor(bookInfoParam.getAuthor());
        bookInfo.setBookPrice(bookInfoParam.getBookPrice());
        bookInfo.setInsertDate(new Date());
        bookInfo.setPubilshDate(DateForMat.str2Date(bookInfoParam.getPubilshDate(),DateForMat.Date_Y_M));
        bookInfoMapper.insert(bookInfo);
        return TResponse.success();
    }

}
