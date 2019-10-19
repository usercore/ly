package com.ly.task.impl.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ly.capture.IParsePageInfo;
import com.ly.task.ITaskService;
import com.ly.util.GloableConstant;
import com.ly.util.HttpUtil;
import com.ly.util.RedisUtil;

@Service("firstPageList")
public class FirstPageService implements ITaskService{

	@Autowired
	private RedisUtil redisUtil;
	
	static Map<String, String> cookieMap = new HashMap<>();
	
	@Autowired
	IParsePageInfo parseAmazonUkPage;
	
	@Override
	public void runTask(String redisKey) {
		
		System.out.println(redisKey);
		
		String dealUrl = redisUtil.lPop(redisKey);
		
		if(StringUtils.isEmpty(dealUrl)){
			return ;
		}
		
		Document pageDom = HttpUtil.getPageInfo(dealUrl,
				HttpUtil.getCookieMap(GloableConstant.UK_COOKIE_URL, cookieMap));
		
		Set<String> urls = new HashSet<>();
		
		try {
			urls = parseAmazonUkPage.getPageAllUrl(pageDom);
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
		
		redisUtil.lSetList("secPageList", urlList);
		
	}
	
	
	

}
