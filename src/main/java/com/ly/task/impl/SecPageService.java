package com.ly.task.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.ly.capture.IParsePageInfo;
import com.ly.excel.ExcelUtils;
import com.ly.task.ITaskService;
import com.ly.util.GloableConstant;
import com.ly.util.HttpUtil;
import com.ly.util.PropertieUtil;
import com.ly.util.RedisUtil;

@Service("secPageList")
public class SecPageService implements ITaskService {

	@Autowired
	private RedisUtil redisUtil;
	
	static Map<String, String> cookieMap = new HashMap<>();
	
	static List<String> title = Arrays.asList("detailUrl","listUrl","Trade Register Number","VAT Number","Customer Services Address","Business Address","Phone number","Business Type","Business Name");
	
	@Autowired
	IParsePageInfo parseAmazonUkPage;
	
	@Override
	public void runTask(String redisKey) {

		String dealInfo = redisUtil.lPop(redisKey);
		
		System.out.println(redisKey + "  dealUrl = " + dealInfo);

		
		if (StringUtils.isEmpty(dealInfo)) {
			return;
		}
		
		String[] dealUrls = dealInfo.split(GloableConstant.SPLIT_COMMA);
		
		if(dealUrls.length<2){
			System.out.println("地址格式错误");
			return;
		}
		
		String detailUrl = dealUrls[0];
		
		String listUrl = dealUrls[1];
		
		String excelFilePath = PropertieUtil.getValue("excelFilePath");
		
		Document pageDom = HttpUtil.getPageInfo(detailUrl,
				HttpUtil.getCookieMap(GloableConstant.UK_COOKIE_URL, cookieMap));
		
		Map<String, String> result = new HashMap<>();
		
		try {
			result = parseAmazonUkPage.parsePageInfo(pageDom);
			result.put("detailUrl", detailUrl);
			result.put("listUrl", listUrl);
		} catch (Exception e) {
			redisUtil.lSet(redisKey, dealInfo);
			e.printStackTrace();
			return;
		}
		
		if(ObjectUtils.isEmpty(result)){
			redisUtil.lSet(redisKey, dealInfo);
			return;
		}
		
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		list.add(result);
		
		
		//写excel
		try {
			ExcelUtils.writeExcel(excelFilePath, "页面内容", title, list, 1);
		} catch (EncryptedDocumentException | InvalidFormatException | IllegalArgumentException | IOException e1) {
			redisUtil.lSet(redisKey, dealInfo);
			e1.printStackTrace();
			return;
		}
		
		
	}

}
