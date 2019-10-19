package com.ly.capture.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.ly.capture.IParsePageInfo;
import com.ly.capture.ParsePageUtil;
import com.ly.exception.BusinessException;
import com.ly.util.GloableConstant;
import com.ly.util.ThreadUtil;

/**
 * 亚马逊英国站
 * 
 * @author pc
 *
 */
@Service("parseAmazonUkPage")
public class ParseAmazonUkPageImpl implements IParsePageInfo {

	private String hrefClass = "s-result-list s-search-results sg-row";

	private String nextPageClass = "a-normal";
	
	private String productsId = "products-link";

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
	public String getNextUrl(Document doc, int currentPage) {
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
		return nextPaeUrl;
	}

	@Override
	public Map<String, String> parsePageInfo(Document doc) throws BusinessException{

		if (doc == null || ParsePageUtil.judgeRobot(doc)) {
			ThreadUtil.sleepTime(300000L);
			throw new BusinessException("网络异常");
		}

		Map<String, String> pageInfo = getEnPageInfo(doc);

		return pageInfo;
	}

	// 解析英国数据
	@Override
	public String getThrEnPageUrl(Document doc) {
		String keyId = "merchant-info";
		return getUrlById(doc, keyId);

	}
	
	private String getUrlById(Document doc,String keyId){
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
	private Map<String, String> getEnPageInfo(Document doc) {
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
		result.put(GloableConstant.PRODUCT_URL, getUrlById(doc, productsId));
		
		System.out.println("详情页=" + result);
		return result;
	}
}
