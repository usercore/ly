package com.ly.task.impl;

import com.alibaba.druid.util.StringUtils;
import com.ly.capture.IParsePageInfo;
import com.ly.util.GloableConstant;
import com.ly.util.HttpUtil;
import com.ly.util.RedisUtil;
import com.ly.util.UrlUtil;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
public class PageUtil {

    @Autowired
    private RedisUtil redisUtil;
    static Map<String, String> cookieMap = new HashMap<>();
    @Autowired
    IParsePageInfo parseAmazonUkPage;

    public void parseFirstPage(String dealInfo,String redisKey,String saveRedis){
        String[] dealUrls = dealInfo.split(GloableConstant.SPLIT_COMMA);
        if (dealUrls.length < 2) {
            System.out.println("地址格式错误");
            return;
        }
        String url = "";
        int totalPage = 0;
        String nextPage = "";
        if(dealUrls.length == 2){
            url = dealUrls[0];
            totalPage = Integer.parseInt(dealUrls[1]);
        }else if(dealUrls.length == 3){
            url = dealUrls[0];
            nextPage = dealUrls[1];
            totalPage = Integer.parseInt(dealUrls[2]);
        }

        System.out.println("initTask =" + url);
        try {
            savePageUrl(url,nextPage, totalPage,saveRedis);
        } catch (Exception e) {
            //发生异常将处理内容重新放入队列
            System.out.println("InitTaskService 发生错误");
            redisUtil.rPush(redisKey, dealInfo);
            e.printStackTrace();
            return;
        }
    }
    private boolean savePageUrl(String dealUrl,String nextUrl, int totalPage,String redisKey) throws Exception {
        boolean result = false;
        if(StringUtils.isEmpty(nextUrl)){
            redisUtil.lSet(redisKey, dealUrl);
            for (int i = 1; i < totalPage; i++) {
                Map<String,String> map = UrlUtil.getQuestParam(dealUrl);
                map.put("page" ,(i + 1) + "");
                map.put("ref","sr_pg_" + (i + 1));
                String tempUrl = UrlUtil.assembleUrl(map.get("host"),map);
                redisUtil.lSet(redisKey, tempUrl);
            }
            result = true;
        }else if (!StringUtils.isEmpty(nextUrl)) {
            redisUtil.lSet(redisKey, dealUrl);
            redisUtil.lSet(redisKey, nextUrl);
            for (int i = 2; i < totalPage; i++) {
                if (null != nextUrl && !nextUrl.equals("")) {
                    nextUrl = nextUrl.replace(i + "", (i + 1) + "");
                }
                redisUtil.lSet(redisKey, nextUrl);
            }
            result = true;
        }
        return result;
    }

    /**
     * 解析第二页数据
     * @param dealUrl
     * @param redisKey
     */
    public void parseSecPage(String dealUrl,String redisKey,String saveRedis){
        Document pageDom = HttpUtil.getPageInfo(dealUrl,
                HttpUtil.getCookieMap(UrlUtil.parseHostUrl(dealUrl), cookieMap));
        Set<String> urls = new HashSet<>();
        try {
            urls = parseAmazonUkPage.getPageAllUrl(pageDom,dealUrl);
        } catch (Exception e) {
            redisUtil.rPush(redisKey, dealUrl);
            e.printStackTrace();
            return;
        }
        if(CollectionUtils.isEmpty(urls)){
            redisUtil.rPush(redisKey, dealUrl);
            return ;
        }
        List<String> urlList = new ArrayList<>();
        for(String url:urls){
            if(url.indexOf("qid") != -1){
                urlList.add(url + GloableConstant.SPLIT_COMMA + dealUrl);
            }
        }
        redisUtil.lSetList(saveRedis, urlList);
    }
}
