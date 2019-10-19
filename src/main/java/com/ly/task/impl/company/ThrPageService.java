package com.ly.task.impl.company;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.druid.support.json.JSONUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.ly.capture.IParsePageInfo;
import com.ly.excel.ExcelUtils;
import com.ly.task.ITaskService;
import com.ly.util.DateUtil;
import com.ly.util.GloableConstant;
import com.ly.util.HttpUtil;
import com.ly.util.PropertieUtil;
import com.ly.util.RedisUtil;

@Service("thrPageList")
public class ThrPageService implements ITaskService {

	@Autowired
	private RedisUtil redisUtil;
	
	static Map<String, String> cookieMap = new HashMap<>();
	
	static List<String> title = Arrays.asList("detailUrl","listUrl","productsUrl","Trade Register Number","VAT Number","Customer Services Address","Business Address","Phone number","Business Type","Business Name");
	
	@Autowired
	IParsePageInfo parseAmazonUkPage;

	@Override
	public void runTask(String redisKey) {

		String dealInfo = redisUtil.lPop(redisKey);
		
		System.out.println(redisKey + "  dealUrl = " + dealInfo);
		
		try {
		if (StringUtils.isEmpty(dealInfo)) {
			return;
		}
		
		String[] dealUrls = dealInfo.split(GloableConstant.SPLIT_COMMA);
		
		if(dealUrls.length<3){
			System.out.println("地址格式错误");
			return;
		}
		
		String url = dealUrls[0];
		
		String detailUrl = dealUrls[1];
		
		String listUrl = dealUrls[2];
		
		String excelFilePath = PropertieUtil.getValue("excelFilePath") + DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYMMDD)
		+ PropertieUtil.getValue("excelFilePostfix");
		
		Document pageDom = HttpUtil.getPageInfo(url,
				HttpUtil.getCookieMap(GloableConstant.UK_COOKIE_URL, cookieMap));
		
		Map<String, String> result = new HashMap<>();

			result = parseAmazonUkPage.parsePageInfo(pageDom);
			
			if(result != null){
				result.put("detailUrl", detailUrl);
				result.put("listUrl", listUrl);
				
				List<Map<String,String>> list = new ArrayList<>();
				list.add(result);

				redisUtil.sSet(GloableConstant.COMPANY_PAGE_INFO, JSONUtils.toJSONString(result));

				//写excel
				try {
					ExcelUtils.writeExcel(excelFilePath, "页面内容", title, list, 1);
				} catch (EncryptedDocumentException | InvalidFormatException | IllegalArgumentException | IOException e1) {
					redisUtil.lSet(redisKey, dealInfo);
					e1.printStackTrace();
					return;
				}
			}
		} catch (Exception e) {
			redisUtil.lSet(redisKey, dealInfo);
			e.printStackTrace();
			return;
		}
		
		
		
		
	}

}
