package com.ly.util;

import java.util.HashMap;
import java.util.Map;

public class UrlUtil {

    public static Map<String,String> getQuestParam(String url){
        Map<String,String> result = new HashMap<>();
        String[] urls = url.split("\\?");

        String host = urls[0];

        String[] params = urls[1].split("&");

        for(String param : params){
            String[] paramKeyValue = param.split("=");
            result.put(paramKeyValue[0],paramKeyValue[1]);
        }
        result.put("host",host);

        return result;
    }

    public static String assembleUrl(String host,Map<String,String> params){
        String url = host;
        url = url + "?";
        params.remove("host");
        
        for (String key : params.keySet()){
            url = url + key + "=" + params.get(key) + "&";
        }
        
        if(url.lastIndexOf("&") == url.length() - 1){
        	url = url.substring(0,url.length() -1);
        }
        return url;
    }

    public static void main(String[] args){
        Map<String,String> map = new HashMap();
        map = getQuestParam(" https://www.amazon.co.uk/s?k=Headphones&i=electronics&rh=n%3A4085731&lo=list&page=1&qid=1567329316&ref=sr_pg_1");
        String host = map.get("host");
        map.remove("host");

        String url = assembleUrl(host,map);

        System.out.println(url);

    }
}

