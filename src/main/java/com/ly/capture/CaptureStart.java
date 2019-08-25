package com.ly.capture;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Document;

import com.ly.excel.ExcelUtils;
import com.ly.util.HttpUtil;
import com.ly.util.PropertieUtil;
import com.ly.util.ThreadUtil;
import com.ly.vo.PageMainInfoVO;
import com.ly.vo.ParentPageVO;

public class CaptureStart {

	static String cookieUrl = "https://www.amazon.com/gp/redirection/mexico.html";

	// static String cookieUrl =
	// "https://www.amazon.com/gp/sponsored-products/logging/log-action.html?qualifier=1564804697&id=7705943495396256&widgetName=sp_phone_search_atf&adId=200014234839021&eventType=1&adIndex=0";

	static Map<String, String> cookieMap = new HashMap<>();

	public static List<PageMainInfoVO> getSecPage(Set<String> urlList) {

		List<PageMainInfoVO> result = new ArrayList<>();

		for (String url : urlList) {
			Document doc = HttpUtil.getPageInfo(url, HttpUtil.getCookieMap(cookieUrl, cookieMap));
			if(doc == null || ParsePageUtil.judgeRobot(doc)){
				ThreadUtil.sleepTime(300000L);
				result.clear();
				getSecPage(urlList);
			}
			PageMainInfoVO by = ParsePageUtil.getPageMainInfo(doc);
			System.out.println("解析页面内容=" + by + "   url=" + url);
			by.setUrl(url);
			result.add(by);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return result;

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
					HttpUtil.getCookieMap(cookieUrl, cookieMap));
			
			if(null == pageDom || ParsePageUtil.judgeRobot(pageDom)){
				ThreadUtil.sleepTime(300000L);
				exportPageView(parentPageVO, maxPage, satrtRow);
			}
			nextPageVO = ParsePageUtil.getNextPage(parentPageVO, pageDom);

			Set<String> urlList = ParsePageUtil.getPageUrlInfo(pageDom);
			
			nextPageVO.setPageSize(urlList.size());

			List<PageMainInfoVO> pageMainInfolist = getSecPage(urlList);
			
			writeExcel(pageMainInfolist, satrtRow, parentPageVO);
			
		} catch (Exception e) {
			e.printStackTrace();
			ThreadUtil.sleepTime(300000L);
			exportPageView(parentPageVO, maxPage, satrtRow);
		}
		return nextPageVO;
	}

	/**
	 * 写excel
	 * 
	 * @param pageMainInfolist
	 * @param rowIndex
	 */
	public static void writeExcel(List<PageMainInfoVO> pageMainInfolist, int rowIndex, ParentPageVO parentPageVO) {
		String excelFilePath = PropertieUtil.getValue("excelFilePath");
		List<Map<String, String>> list = new ArrayList<>();
		pageMainInfolist.forEach(pageMainInfo -> {
			Map<String, String> map = new HashMap<>();
			map.put("by", pageMainInfo.getByInfo());
			map.put("productFirstWord", pageMainInfo.getFirstWord());
			map.put("star", pageMainInfo.getStar());
			map.put("customer", pageMainInfo.getCustomerViews());
			map.put("url", pageMainInfo.getUrl());
			map.put("currentPage", parentPageVO.getCurrentPage() + "");
			map.put("currnetPageUrl", parentPageVO.getCurrentUrl());
			if (null == parentPageVO.getNextPage().getCurrentUrl()) {
				map.put("nextPageUrl", "");
			} else {
				map.put("nextPageUrl", parentPageVO.getNextPage().getCurrentUrl());
			}

			list.add(map);
		});
		List<String> title = Arrays.asList("by", "productFirstWord", "star", "customer", "url", "currentPage",
				"currnetPageUrl", "nextPageUrl");
		try {
			ExcelUtils.writeExcel(excelFilePath, "页面内容", title, list, rowIndex);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public static void exportInfo(String url, int startPage, int maxPage, int excelStartLine) {
		ParentPageVO parentPageVO = new ParentPageVO();
		parentPageVO.setCurrentUrl(url);
		parentPageVO.setCurrentPage(startPage);
		
		ParentPageVO nextPageVO = exportPageView(parentPageVO, maxPage, excelStartLine);

		while (nextPageVO.getCurrentPage() < maxPage) {
			excelStartLine = excelStartLine + nextPageVO.getPageSize() - 1;
			// System.out.println("nextPageUrl = " +
			// nextPageVO.getNextPage().getCurrentUrl());
			nextPageVO = exportPageView(nextPageVO.getNextPage(), maxPage, excelStartLine);
		}
	}
	
	public static void exportEnInfo(String url, int startPage, int maxPage, int excelStartLine) {
		ParentPageVO parentPageVO = new ParentPageVO();
		parentPageVO.setCurrentUrl(url);
		parentPageVO.setCurrentPage(startPage);
		
		ParentPageVO nextPageVO =  CaptureEnInfo.exportPageView(parentPageVO, maxPage, excelStartLine);

		while (nextPageVO.getCurrentPage() < maxPage) {
			excelStartLine = excelStartLine + nextPageVO.getPageSize() - 1;
			// System.out.println("nextPageUrl = " +
			// nextPageVO.getNextPage().getCurrentUrl());
			nextPageVO = CaptureEnInfo.exportPageView(nextPageVO.getNextPage(), maxPage, excelStartLine);
		}
	}

	public static void startCapture() {
		String url = PropertieUtil.getValue("url");
		int maxPage = Integer.parseInt(PropertieUtil.getValue("maxPage"));
		int startPage = Integer.parseInt(PropertieUtil.getValue("startPage"));
		int excelStartLine = Integer.parseInt(PropertieUtil.getValue("excelStartLine"));
		exportInfo(url, startPage, maxPage, excelStartLine);

	}
	
	public static void startCaptureEn() {
		String url = PropertieUtil.getValue("url");
		int maxPage = Integer.parseInt(PropertieUtil.getValue("maxPage"));
		int startPage = Integer.parseInt(PropertieUtil.getValue("startPage"));
		int excelStartLine = Integer.parseInt(PropertieUtil.getValue("excelStartLine"));
		exportEnInfo(url, startPage, maxPage, excelStartLine);

	}

	public static void main(String[] args) throws MalformedURLException, IOException {
		/*String avage = "https://www.amazon.com/Google-Pixel-Memory-Phone-Unlocked/dp/B07R7DY911/ref=sr_1_1_sspa?keywords=phone&qid=1564847580&s=gateway&sr=8-1-spons&psc=1&spLa=ZW5jcnlwdGVkUXVhbGlmaWVyPUEyQVBQV0g1STVOUk1RJmVuY3J5cHRlZElkPUEwOTI0NjE1WjBNWjRNRUc4OEY4JmVuY3J5cHRlZEFkSWQ9QTA1OTgyMjcxV1RNV1AyWTZFVEEmd2lkZ2V0TmFtZT1zcF9hdGYmYWN0aW9uPWNsaWNrUmVkaXJlY3QmZG9Ob3RMb2dDbGljaz10cnVl";
		Set<String> list = new HashSet<>();
		list.add(avage);
		getSecPage(list);*/
		Thread.currentThread().setName("aaaa");
		//startCapture();
		startCaptureEn();
	}
}
