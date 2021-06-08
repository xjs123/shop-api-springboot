package com.fh.shop.api.common;

public class
KeyUtil {

    public static String getksy(Long id){
        return "mamber:"+id;
    }

    public static String buildImageCodeKey(String id){
        return "image:code:"+id;
    }

    public static String ActionKey(String id){
        return "member:active:"+id;
    }


    public static String
    buildCartKey(Long id){
        return "Cart:"+id;
    }

    public static String MemBerConsignee(String id) {
        return "MemBerConsignee:"+id;
    }


    public static String ConsigneeKey(String id) {
        return "Consignee:"+id;
    }

    public static String
    TockenKey(String id) {
        return "token:"+id;
    }


}
