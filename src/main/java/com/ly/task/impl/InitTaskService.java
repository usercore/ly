package com.ly.task.impl;

import java.util.HashMap;
import java.util.Map;
import com.ly.util.UrlUtil;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.druid.util.StringUtils;
import com.ly.capture.IParsePageInfo;
import com.ly.task.ITaskService;
import com.ly.util.GloableConstant;
import com.ly.util.HttpUtil;
import com.ly.util.RedisUtil;

@Service("initTask")
public class InitTaskService implements ITaskService {

	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	PageUtil pageUtil;
	static Map<String, String> cookieMap = new HashMap<>();

	@Autowired
	IParsePageInfo parseAmazonUkPage;

	@Override
	public void runTask(String redisKey) {

		String dealInfo = redisUtil.lPop(redisKey);

		if (StringUtils.isEmpty(dealInfo)) {
			return;
		}

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
			pageUtil.savePageUrl(url,nextPage, totalPage);

		} catch (Exception e) {
			redisUtil.rPush(redisKey, dealInfo);
			e.printStackTrace();
			return;
		}
	}
}
