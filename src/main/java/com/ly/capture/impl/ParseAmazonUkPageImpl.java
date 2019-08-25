package com.ly.capture.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.util.HSSFColor.GOLD;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.ly.capture.IParsePageInfo;
import com.ly.capture.ParsePageUtil;
import com.ly.excel.ExcelUtils;
import com.ly.util.GloableConstant;
import com.ly.util.HttpUtil;
import com.ly.util.PropertieUtil;
import com.ly.util.ThreadUtil;
import com.ly.vo.PageMainInfoVO;

/**
 * 亚马逊英国站
 * 
 * @author pc
 *
 */
@Service("parseAmazonUkPage")
public class ParseAmazonUkPageImpl implements IParsePageInfo {

	String hrefClass = "s-result-list s-search-results sg-row";

	String nextPageClass = "a-normal";

	static Map<String, String> cookieMap = new HashMap<>();

	@Override
	public Set<String> getPageAllUrl(Document doc) throws Exception {

		Set<String> result = new HashSet<String>();
		Element body = doc.body();
		Elements cla = body.getElementsByClass(hrefClass);

		Elements es = cla.select(GloableConstant.A);

		for (Iterator<Element> it = es.iterator(); it.hasNext();) {

			Element e = it.next();

			String href = e.attr(GloableConstant.HREF);

			if (href.indexOf(GloableConstant.HTTP) == -1 && href.indexOf(AMAZON_HOME) == -1) {
				href = GloableConstant.AMAZON_HOME_UK + href;
			}

			if (href.indexOf(GloableConstant.JAVASCRIPT) != -1) {
				continue;
			}
			result.add(href);
		}
		return result;
	}

	@Override
	public String getNextUrl(Document doc, int currentPage, String currentUrl) {
		String nextPaeUrl = "";
		if (doc == null) {
			return null;
		}
		Elements pages = doc.getElementsByClass(nextPageClass);

		for (Iterator<Element> it = pages.iterator(); it.hasNext();) {
			Element e = it.next();
			Elements a = e.select(GloableConstant.A);
			if (a.text().equals((currentPage + GloableConstant.ONE) + "")) {
				String href = a.attr(GloableConstant.HREF);
				nextPaeUrl = GloableConstant.AMAZON_HOME_UK + href;
				break;
			}
		}
		if (null == nextPaeUrl || nextPaeUrl.equals("")) {
			nextPaeUrl = currentUrl.replace(currentPage + "", (currentPage + 1) + "");
		}
		return nextPaeUrl;
	}

	@Override
	public Map<String, String> parsePageInfo(Document doc) {

		if (doc == null || ParsePageUtil.judgeRobot(doc)) {
			ThreadUtil.sleepTime(300000L);
			return null;
		}
		String secUrl = getSecEnPageUrl(doc);

		Document secDoc = HttpUtil.getPageInfo(secUrl, HttpUtil.getCookieMap(GloableConstant.UK_COOKIE_URL, cookieMap));

		Map<String, String> pageInfo = getEnPageInfo(secDoc);

		return pageInfo;
	}

	// 解析英国数据
	private static String getSecEnPageUrl(Document doc) {
		String keyId = "merchant-info";
		String url = "";

		if (null == doc) {
			return url;
		}
		Element e = doc.getElementById(keyId);
		if (null == e) {
			return url;
		}
		Elements a = e.select("a");
		if (null == a) {
			return url;
		}
		if (a.size() > 0) {
			url = GloableConstant.AMAZON_HOME_UK + a.get(0).attr("href");
		}
		return url;

	}

	// 解析详情页
	private static Map<String, String> getEnPageInfo(Document doc) {
		Map<String, String> result = new HashMap<>();
		String keyId = "a-unordered-list a-nostyle a-vertical";
		if (null == doc) {
			return null;
		}
		Elements es = doc.getElementsByClass(keyId);
		if (null == es || es.size() == 0) {
			return null;
		}
		Element e = es.get(0);
		Elements li = e.getElementsByTag("li");
		System.out.println(e.outerHtml());
		for (Element link : li) {
			String text = link.text();
			if (text.indexOf(":") != -1) {
				String[] infos = text.split(":");
				result.put(infos[0], infos[1]);
			}

		}
		System.out.println("详情页=" + result);
		return result;

	}
}
