package com.ly.util;

public class StringUtil {
    /**
     * 首字母转小写
     * @param str
     */
    public static String convertFirstWordToLower(String str){
        str=str.substring(0,1).toUpperCase().concat(str.substring(1).toLowerCase());
        return str;
    }
}
