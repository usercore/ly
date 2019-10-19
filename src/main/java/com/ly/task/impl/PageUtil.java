package com.ly.task.impl;

import com.alibaba.druid.util.StringUtils;
import com.ly.util.RedisUtil;
import com.ly.util.UrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
public class PageUtil {

    @Autowired
    private RedisUtil redisUtil;

    public boolean savePageUrl(String dealUrl,String nextUrl, int totalPage) throws Exception {
        boolean result = false;

        if(StringUtils.isEmpty(nextUrl)){
            redisUtil.lSet("firstPageList", dealUrl);

            for (int i = 1; i < totalPage; i++) {

                Map<String,String> map = UrlUtil.getQuestParam(dealUrl);
                map.put("page" ,(i + 1) + "");
                map.put("ref","sr_pg_" + (i + 1));
                String tempUrl = UrlUtil.assembleUrl(map.get("host"),map);
                redisUtil.lSet("firstPageList", tempUrl);
            }

            result = true;

        }else if (!StringUtils.isEmpty(nextUrl)) {

            redisUtil.lSet("firstPageList", dealUrl);

            redisUtil.lSet("firstPageList", nextUrl);

            for (int i = 2; i < totalPage; i++) {

                if (null != nextUrl && !nextUrl.equals("")) {
                    nextUrl = nextUrl.replace(i + "", (i + 1) + "");
                }

                redisUtil.lSet("firstPageList", nextUrl);
            }

            result = true;
        }

        return result;
    }
}
