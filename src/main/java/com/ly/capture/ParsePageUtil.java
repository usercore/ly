package com.ly.capture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Predicate;
import com.ly.vo.PageMainInfoVO;
import com.ly.vo.ParentPageVO;

public class ParsePageUtil {

	static Pattern urlPattern = Pattern.compile("https://www.amazon.com/[^\u4e00-\u9fa5\\s]*(?=\")");

	static String amazonHom = "https://www.amazon.com";
	
	static String amazonHomUk = "https://www.amazon.co.uk";

	static Pattern starPattern = Pattern.compile("(by\\s*\\D*\\s*[0-9]*[,]?[0-9]+\\s*customer reviews)");

	static Pattern customerViewPattern = Pattern.compile("([0-9]*[,]?[0-9]+\\s*customer reviews)");

	public static List<String> starClassList = new ArrayList<>();

	static String starClass = "a-icon-alt";
	
	static {
		starClassList.add("a-icon a-icon-star a-star-5 a-spacing-none");
		starClassList.add("a-icon a-icon-star a-star-4-5");
		starClassList.add("a-icon a-icon-star a-star-3-5");
		starClassList.add("a-icon a-icon-star a-star-4");
		starClassList.add("a-icon-alt");

	}

	/// <i class="a-icon a-icon-star a-star-4"><span class="a-icon-alt">3.8 out
	/// of 5 stars</span></i>
	public static Set<String> getPageUrlInfo(String str, Predicate<String> p) {
		List<String> urlList = getParseInfo(str, p, urlPattern);
		Set<String> result = new HashSet<String>(urlList);
		return result;
	}

	@SuppressWarnings("rawtypes")
	public static Set<String> getPageUrlInfo(Document doc) {
		Set<String> result = new HashSet<String>();
		Element body = doc.body();
		Elements cla = body.getElementsByClass("s-result-list s-search-results sg-row");
		Elements es = cla.select("a");
		for (Iterator it = es.iterator(); it.hasNext();) {
			Element e = (Element) it.next();
			String href = e.attr("href");

			if (href.indexOf("http") == -1 && href.indexOf("https://www.amazon") == -1) {
				href = amazonHomUk + href;
			}

			if (href.indexOf("javascript") != -1) {
				continue;
			}
			result.add(href);
		}
		return result;
	}

	public static ParentPageVO getNextPage(ParentPageVO parentPageVO, Document doc) {

		if (doc == null) {
			return null;
		}
		// Elements pages = doc.getElementsByClass("a-pagination");
		Elements pages = doc.getElementsByClass("a-normal");

		for (Iterator it = pages.iterator(); it.hasNext();) {
			Element e = (Element) it.next();
			Elements a = e.select("a");
			if (a.text().equals((parentPageVO.getCurrentPage() + 1) + "")) {
				String href = a.attr("href");
				href = amazonHomUk + href;
				ParentPageVO parentPageVOTmp = new ParentPageVO();
				parentPageVOTmp.setCurrentPage(Integer.parseInt(a.text()));
				parentPageVOTmp.setCurrentUrl(href);
				parentPageVO.setNextPage(parentPageVOTmp);
				break;
			}
			;

		}
		ParentPageVO nextPage = parentPageVO.getNextPage();
		if (null == nextPage || null == nextPage.getCurrentUrl() || nextPage.getCurrentUrl().equals("")) {
			String nextUrl = parentPageVO.getCurrentUrl().replace(parentPageVO.getCurrentPage() + "",
					(parentPageVO.getCurrentPage() + 1) + "");
			nextPage = new ParentPageVO();
			nextPage.setCurrentPage(parentPageVO.getCurrentPage() + 1);
			nextPage.setCurrentUrl(nextUrl);
		}
		return parentPageVO;
	}

	public static PageMainInfoVO getPageMainInfo(Document doc) {
		PageMainInfoVO pageMainInfoVO = new PageMainInfoVO();
		if (null == doc) {
			return pageMainInfoVO;
		}
		pageMainInfoVO.setByInfo(getBylineInfo(doc));
		pageMainInfoVO.setStar(getAppraiseStar(doc));
		pageMainInfoVO.setCustomerViews(getCustomerView(doc.toString()));
		pageMainInfoVO.setFirstWord(getFirstWord(doc));
		return pageMainInfoVO;

	}
	//解析英国数据
	public static String getSecEnPageUrl(Document doc) {
		String keyId = "merchant-info";
		String url = "";
		
		if (null == doc) {
			return url;
		}
		Element e = doc.getElementById(keyId);
		if(null == e){
			return url;
		}
		Elements a = e.select("a");
		if(null == a){
			return url;
		}
		if(a.size() > 0){
			url = amazonHomUk + a.get(0).attr("href");
		}
		return url;

	}
	
	//解析详情页
	public static Map<String,String> getEnPageInfo(Document doc) {
		Map<String,String> result = new HashMap<>();
		String keyId = "a-unordered-list a-nostyle a-vertical";
		if (null == doc) {
			return null;
		}
		Elements es = doc.getElementsByClass(keyId);
		if(null == es || es.size() == 0){
			return null;
		}
		Element e = es.get(0);
		Elements li = e.getElementsByTag("li");
		System.out.println(e.outerHtml());
		for (Element link : li) {
			  String text = link.text();
			  if(text.indexOf(":") != -1){
				  String[] infos = text.split(":");
				  result.put(infos[0], infos[1]);
			  }
			  
		}
		System.out.println("详情页=" + result);
		return result;

	}
	
	
	/**
	 * 解析产品首词
	 * @param doc
	 * @return
	 */
	public static String getFirstWord(Document doc){
		String productFirstWord = "";
		Element content = doc.getElementById("productTitle");
		if (content == null) {
			return "";
		}
		productFirstWord = content.text().split(" ")[0];
		return productFirstWord;
	}
	public static String getAppraiseStar(Document doc) {

		/*
		 * Element content = doc.getElementById("acrPopover"); if(null ==
		 * content){ return ""; } return content.text();
		 */
		Elements contents = doc.getElementsByClass(starClass);
		if(contents.size()>0){
			return contents.get(0).ownText();
		}
		String star = "";
		/*Optional<String> haveStar = starClassList.parallelStream()
				.filter((starClass -> doc.getElementsByClass(starClass) != null)).map(starClass -> {
					if (doc.getElementsByClass(starClass).size() > 0) {
						return doc.getElementsByClass(starClass).get(0).text();
					} else {
						return "";
					}
				}).findAny();

		if (haveStar.isPresent()) {
			star = haveStar.get();
		}*/

		return star;

	}

	public static String getBylineInfo(Document doc) {
		Element content = doc.getElementById("bylineInfo");
		if (content == null) {
			content = doc.getElementById("olpProductByline");
		}
		if (content == null) {
			return "";
		}
		return "by " + content.text();
	}

	public static String getCustomerView(String text) {

		List<String> list = getParseInfo(text, (s) -> true, customerViewPattern);

		if (list.isEmpty()) {
			return "";
		} else {
			return list.get(0);
		}

	}

	private static List<String> getParseInfo(String str, Predicate<String> p, Pattern pattern) {
		List<String> urlList = new ArrayList<>();
		// 现在创建 matcher 对象

		Matcher m = pattern.matcher(str);
		while (m.find()) {
			String url = m.group(0);
			if (p.apply(url)) {
				urlList.add(url);
			}
		}
		return urlList;
	}

	/**
	 * 通过页面样式查找页面元素
	 * @param doc
	 * @param pageClass
	 * @return
	 */
	public static String getFirstTextByClass(Document doc,String pageClass){
		String result = "";
		if(doc == null){
			return result;
		}
		Elements pages = doc.getElementsByClass(pageClass);
		if(pages.size()>0){
			result = pages.get(0).text();
		}
		return result;
	}
	
	/**
	 * 判断页面是否有机器人字样
	 * @param pageDom
	 * @return
	 */
	public static boolean judgeRobot(Document pageDom){
		boolean result = false;
		String robot = ParsePageUtil.getFirstTextByClass(pageDom,"a-last");
		if(robot.indexOf("not a robot") != -1){
			result = true;
		}
		return result;
	}
}
