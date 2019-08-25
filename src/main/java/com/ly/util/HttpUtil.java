package com.ly.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.util.ObjectUtils;

public class HttpUtil {

	//static String user_agent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36";
	static String user_agent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.163 Safari/535.1";
	
	static String[] user_agents = {"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.163 Safari/535.1",
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36",
			"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0) Gecko/20100101 Firefox/6.0",
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
			"Opera/9.80 (Windows NT 6.1; U; zh-cn) Presto/2.9.168 Version/11.50",
			"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0; .NET CLR 2.0.50727; SLCC2; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; Tablet PC 2.0; .NET4.0E)",
			"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; InfoPath.3)",
			"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; GTB7.0)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)","Mozilla/5.0 (Windows; U; Windows NT 6.1; ) AppleWebKit/534.12 (KHTML, like Gecko) Maxthon/3.0 Safari/534.12",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; .NET4.0E)",
			 "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; .NET4.0E; SE 2.X MetaSr 1.0)",
			"Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.3 (KHTML, like Gecko) Chrome/6.0.472.33 Safari/534.3 SE 2.X MetaSr 1.0",
			"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; .NET4.0E)",
			"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.41 Safari/535.1 QQBrowser/6.9.11079.201",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; .NET4.0E) QQBrowser/6.9.11079.201",

			"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)"};
	
	private static String getUserAgent(){
		Random rand = new Random();
		int index = rand.nextInt(user_agents.length-1);
		return user_agents[index];
	}
	/**
	 * 请求网页获取网页内容
	 * 
	 * @param url
	 * @return
	 */
	public static Document getPageInfo(String url, Map<String,String> cookieMap) {
		Document doc = null;
		try {
			if (url.indexOf("javascript") != -1 || url.equals("")) {
				return doc;
			}
			doc = Jsoup.connect(url).timeout(90000).userAgent(getUserAgent()).cookies(cookieMap).get();
//			doc = Jsoup.parse(new URL(url), 90000);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("error");
		}
		if (null != doc) {
			return doc;
		} else {
			return null;
		}

	}
	
	public static Map<String, String> getCookie(String url) {
		Map<String, String> cookieMap = new HashMap<String, String>();
		try {
			if(url == null || url.equals("")){
				return null;
			}
				Connection.Response res = Jsoup.connect(url).userAgent(user_agent).data(new HashMap<String, String>())
						.method(Connection.Method.GET).execute();
				cookieMap = res.cookies();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cookieMap;
	}
	
	
	public static Map<String, String> getCookieMap(String cookieUrl,Map<String, String> cookieMap){
		if(ObjectUtils.isEmpty(cookieMap)){
			cookieMap = HttpUtil.getCookie(cookieUrl);
		}
		return cookieMap;
	}
}
