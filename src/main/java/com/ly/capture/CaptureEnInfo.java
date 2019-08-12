package com.ly.capture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jsoup.nodes.Document;
import org.springframework.util.ObjectUtils;

import com.ly.excel.ExcelUtils;
import com.ly.util.HttpUtil;
import com.ly.util.PropertieUtil;
import com.ly.util.ThreadUtil;
import com.ly.vo.PageMainInfoVO;
import com.ly.vo.ParentPageVO;

public class CaptureEnInfo {
	
	static String cookieUrl = "https://www.amazon.co.uk";
	
	static Set<String> dealList = new HashSet<>();
	
	static List<String> title = Arrays.asList("thrUrl","secUrl","nextUrl","Trade Register Number","VAT Number","Customer Services Address","Business Address","Phone number","Business Type","Business Name");
	
	static Map<String, String> cookieMap = new HashMap<>();

	public static Map<String, String> getCookieMap(){
		if(ObjectUtils.isEmpty(cookieMap)){
			cookieMap = HttpUtil.getCookie(cookieUrl, cookieMap);
		}
		return cookieMap;
	}
	/**
	 * 解析当页所有url，获取下一页url，遍历url读取页面内容
	 * 
	 * @param parentPageVO
	 * @param maxPage
	 * @param satrtRow
	 * @return
	 */
	public static ParentPageVO exportPageView(ParentPageVO parentPageVO, int maxPage, int satrtRow) {
		ParentPageVO nextPageVO = null;
		
		try {
			Document pageDom = HttpUtil.getPageInfo(parentPageVO.getCurrentUrl(),
					getCookieMap());
			
			if(null == pageDom || ParsePageUtil.judgeRobot(pageDom)){
				ThreadUtil.sleepTime(300000L);
				exportPageView(parentPageVO, maxPage, satrtRow);
			}
			nextPageVO = ParsePageUtil.getNextPage(parentPageVO, pageDom);

			Set<String> urlList = ParsePageUtil.getPageUrlInfo(pageDom);
			
			nextPageVO.setPageSize(urlList.size());

			List<PageMainInfoVO> pageMainInfolist = getSecPage(urlList,satrtRow,parentPageVO);
			
		} catch (Exception e) {
			e.printStackTrace();
			ThreadUtil.sleepTime(300000L);
			exportPageView(parentPageVO, maxPage, satrtRow);
		}
		return nextPageVO;
	}
	
	public static List<PageMainInfoVO> getSecPage(Set<String> urlList,int rowIndex,ParentPageVO parentPageVO) {
		String excelFilePath = PropertieUtil.getValue("excelFilePath");
		List<PageMainInfoVO> result = new ArrayList<>();
		
		for(String url:urlList){
			if(url.length()<60){
				continue;
			}
			if(dealList.contains(url)){
				continue;
			}else{
				dealList.add(url);
			}
			
			System.out.println("url=" + url);
			
			Document doc = HttpUtil.getPageInfo(url, getCookieMap());

			if(doc == null || ParsePageUtil.judgeRobot(doc)){
				ThreadUtil.sleepTime(300000L);
				clearCookie();
				getCookieMap();
				getSecPage(urlList,rowIndex,parentPageVO);
			}
			String secUrl = ParsePageUtil.getSecEnPageUrl(doc);
			
			Document secDoc = HttpUtil.getPageInfo(secUrl, getCookieMap());
			
			Map<String,Object> pageInfo = ParsePageUtil.getEnPageInfo(secDoc);
			
			System.out.println("第二页url=" + secUrl);
			if(null != pageInfo){
				pageInfo.put("thrUrl", secUrl);
				pageInfo.put("secUrl", url);
				pageInfo.put("nextUrl", parentPageVO.getNextPage().getCurrentUrl());
				List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
				list.add(pageInfo);
				//写excel
				try {
					ExcelUtils.writeExcel(excelFilePath, "页面内容", title, list, rowIndex);
				} catch (EncryptedDocumentException | InvalidFormatException | IllegalArgumentException | IOException e1) {
					e1.printStackTrace();
					return result;
				}
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			rowIndex = rowIndex + 1;
		}
		return result;

	}
	private static void clearCookie(){
		cookieMap = new HashMap<>();
	}
//	public static void main(String[] args) {
//		String url = "https://www.amazon.co.uk/Anker-PowerCore-Ultra-Compact-Fast-Charging-Technology/dp/B019GJLER8?ref_=Oct_DLandingS_PC_0fb9ea91_0&smid=A2PGPJL0BBLHLX";
//		Set<String> urlList = new HashSet<>();
//		urlList.add(url);
//		getSecPage(urlList,1);
//	}
}
