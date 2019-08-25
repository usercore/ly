package com.ly.task.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class InitTaskService implements ITaskService{

	@Autowired
	private RedisUtil redisUtil;
	
	static Map<String, String> cookieMap = new HashMap<>();
	
	@Autowired
	IParsePageInfo parseAmazonUkPage;
	
	@Override
	public void runTask(String redisKey) {
		
		String dealInfo = redisUtil.lPop(redisKey);
		
		if(StringUtils.isEmpty(dealInfo)){
			return ;
		}
		
		String[] dealUrls = dealInfo.split(GloableConstant.SPLIT_COMMA);
		
		if(dealUrls.length<2){
			System.out.println("地址格式错误");
			return;
		}
		String url = dealUrls[0];
		int totalPage = Integer.parseInt(dealUrls[1]);
		
		System.out.println("initTask =" + url);
		
		try {
			url = savePageUrl(url,totalPage);
		} catch (Exception e) {
			redisUtil.rPush(redisKey, dealInfo);
			e.printStackTrace();
			return ;
		}
	}
	
	private String savePageUrl(String dealUrl,int totalPage) throws Exception{
		
		Document pageDom = HttpUtil.getPageInfo(dealUrl,
				HttpUtil.getCookieMap(GloableConstant.UK_COOKIE_URL, cookieMap));
		
		
		Set<String> urls = parseAmazonUkPage.getPageAllUrl(pageDom);
		//保存第一页数据
		List<String> result = new ArrayList<>(urls);
		
		//第一页所有url
		for(String url:urls){
			result.add(url + GloableConstant.SPLIT_COMMA + dealUrl);
		}
		
		redisUtil.lSetList("secPageList", result);
		
		//第二页
		String nextUrl = parseAmazonUkPage.getNextUrl(pageDom, 1, dealUrl);
		redisUtil.lSet("firstPageList", nextUrl);
		
		for(int i=2;i<totalPage;i++){
			
			if (null != nextUrl && !nextUrl.equals("")) {
				nextUrl = nextUrl.replace(i + "",(i + 1) + "");
			}
			
			redisUtil.lSet("firstPageList", nextUrl);
		}
		return nextUrl;
	}
}
