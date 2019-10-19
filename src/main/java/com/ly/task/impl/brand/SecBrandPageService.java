package com.ly.task.impl.brand;

import com.ly.capture.IParsePageInfo;
import com.ly.task.ITaskService;
import com.ly.util.GloableConstant;
import com.ly.util.HttpUtil;
import com.ly.util.RedisUtil;
import com.ly.util.UrlUtil;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.HashMap;
import java.util.Map;

@Service("secBrandPageList")
public class SecBrandPageService implements ITaskService {

    @Autowired
    private RedisUtil redisUtil;
    static Map<String, String> cookieMap = new HashMap<>();
    String finalBrandPage = "finalBrandPage";
    @Autowired
    IParsePageInfo parseAmazonUkPage;

    @Override
    public void runTask(String redisKey) {
        String dealInfo = redisUtil.lPop(redisKey);
        try {
            System.out.println(redisKey + "  dealUrl = " + dealInfo);
            if (StringUtils.isEmpty(dealInfo)) {
                return;
            }
            String[] dealUrls = dealInfo.split(GloableConstant.SPLIT_COMMA);
            if (dealUrls.length < 2) {
                System.out.println("地址格式错误");
                return;
            }
            String detailUrl = dealUrls[0];
            //临时代码
            if (detailUrl.indexOf("qid") == -1) {
                return;
            }
            Document pageDom = HttpUtil.getPageInfo(detailUrl,
                    HttpUtil.getCookieMap(UrlUtil.parseHostUrl(detailUrl), cookieMap));
            String url = parseAmazonUkPage.getThrEnPageUrl(pageDom,detailUrl);
            //如果存在返回
            if (redisUtil.sHasKey(finalBrandPage, url)) {
                return;
            }
            if (!StringUtils.isEmpty(url)) {
                redisUtil.sSet(finalBrandPage, url);
                redisUtil.lSet("thrPageList", url + GloableConstant.SPLIT_COMMA + dealInfo);
            }
        } catch (Exception e) {
            System.out.println("发生异常，放回队列");
            redisUtil.lSet(redisKey, dealInfo);
            e.printStackTrace();
            return;
        }

    }

}
