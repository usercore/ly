package com.ly.task.impl.brand;

import com.ly.capture.IParsePageInfo;
import com.ly.excel.ExcelUtils;
import com.ly.task.ITaskService;
import com.ly.util.DateUtil;
import com.ly.util.GloableConstant;
import com.ly.util.PropertieUtil;
import com.ly.util.RedisUtil;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Service("secBrandPageList")
public class SecBrandPageService implements ITaskService {

    @Autowired
    private RedisUtil redisUtil;
    String finalBrandPage = "finalBrandPage";
    @Autowired
    IParsePageInfo parseAmazonUkPage;
    String excelFilePath = PropertieUtil.getValue("excelFilePath") + DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYMMDD) + "brand"
            + PropertieUtil.getValue("excelFilePostfix");

    static List<String> title = Arrays.asList("detailUrl","title","brand","start","customView","ASIN",
            "Date First Available","Best Sellers Rank","Business Type","finalUrl");
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
            Map<String,String> result = parseAmazonUkPage.getBrandInfo(detailUrl);
            String url = result.get("companyInfoUrl");
            //保存最终URL
            if (!StringUtils.isEmpty(url)) {
                redisUtil.sSet(finalBrandPage, url);
            }
            List<Map<String,String>> list = new ArrayList<>();
            list.add(result);
            //写excel
            try {
                ExcelUtils.writeExcel(excelFilePath, "页面内容", title, list, 1);
            } catch (EncryptedDocumentException | InvalidFormatException | IllegalArgumentException | IOException e1) {
                redisUtil.lSet(redisKey, dealInfo);
                e1.printStackTrace();
                return;
            }
        } catch (Exception e) {
            System.out.println("发生异常，放回队列");
            redisUtil.lSet(redisKey, dealInfo);
            e.printStackTrace();
            return;
        }
    }
}
