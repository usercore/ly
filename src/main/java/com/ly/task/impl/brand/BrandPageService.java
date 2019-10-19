package com.ly.task.impl.brand;

import com.alibaba.druid.support.json.JSONUtils;
import com.ly.task.ITaskService;
import com.ly.util.GloableConstant;
import com.ly.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("companyPageInfo")
public class BrandPageService implements ITaskService {

    @Autowired
    private RedisUtil redisUtil;
    @Override
    public void runTask(String redisKey) {
        String dealInfo = redisUtil.lPop(redisKey);

        Map<String,String> companyInfo = (HashMap<String,String>)JSONUtils.parse(dealInfo);

        String productUrl = companyInfo.get(GloableConstant.PRODUCT_URL);

    }
}
