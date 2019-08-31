package com.ly.task.impl;

import java.util.HashMap;
import java.util.Map;
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
			String nextUrl = savePageUrl(url,nextPage, totalPage);

			if (StringUtils.isEmpty(nextUrl)) {
				System.out.println("找不到第二页！！！！！");
				redisUtil.rPush(redisKey, dealInfo);
				return;
			}
		} catch (Exception e) {
			redisUtil.rPush(redisKey, dealInfo);
			e.printStackTrace();
			return;
		}
	}

	private String savePageUrl(String dealUrl,String nextUrl, int totalPage) throws Exception {

		if(StringUtils.isEmpty(nextUrl)){
			Document pageDom = HttpUtil.getPageInfo(dealUrl,
					HttpUtil.getCookieMap(GloableConstant.UK_COOKIE_URL, cookieMap));
			nextUrl = parseAmazonUkPage.getNextUrl(pageDom, 1);
		}

		if (!StringUtils.isEmpty(nextUrl)) {
			
			redisUtil.lSet("firstPageList", dealUrl);
			
			redisUtil.lSet("firstPageList", nextUrl);

			for (int i = 2; i < totalPage; i++) {

				if (null != nextUrl && !nextUrl.equals("")) {
					nextUrl = nextUrl.replace(i + "", (i + 1) + "");
				}

				redisUtil.lSet("firstPageList", nextUrl);
			}
		}

		return nextUrl;
	}
	
}
