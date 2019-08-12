package com.ly.util;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.util.ObjectUtils;

public class HttpUtil {

	static String user_agent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36";
	
	
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
			doc = Jsoup.connect(url).timeout(90000).userAgent(user_agent).cookies(cookieMap).get();
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
	
	public static Map<String, String> getCookie(String url,Map<String,String> cookieMap) {

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
}
