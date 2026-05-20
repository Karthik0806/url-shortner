package com.karthik.urlshortener.common.util;

public class Base62Encoder {
    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String encode(Long value){
        if (value==0) return "a";
        StringBuilder shortCode  =  new StringBuilder();
        while (value>0){
            int remainder = (int) (value%62);
            shortCode.append(BASE62.charAt(remainder));
            value/=62;
        }
        return shortCode.reverse().toString();
    }
}
