package com.ly.capture.impl;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Predicate;
import com.ly.util.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.ly.capture.IParsePageInfo;
import com.ly.capture.ParsePageUtil;
import com.ly.exception.BusinessException;

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
    static String starClass = "a-icon-alt";
    static Pattern customerViewPattern = Pattern.compile("([0-9]*[,]?[0-9]+\\s*customer reviews)");
	static Map<String, String> cookieMap = new HashMap<>();

	@Override
	public Set<String> getPageAllUrl(Document doc,String url) throws Exception {

		Set<String> result = new HashSet<>();
		Element body = doc.body();
		Elements cla = body.getElementsByClass(hrefClass);

		Elements es = cla.select(GloableConstant.A);

		for (Iterator<Element> it = es.iterator(); it.hasNext();) {

			Element e = it.next();

			String href = e.attr(GloableConstant.HREF);

			if (href.indexOf(GloableConstant.HTTP) == -1 && href.indexOf(AMAZON_HOME) == -1) {
				href = UrlUtil.parseHostUrl(url) + href;
			}

			if (href.indexOf(GloableConstant.JAVASCRIPT) != -1) {
				continue;
			}
			result.add(href);
		}
		return result;
	}

	@Override
	public String getNextUrl(Document doc, int currentPage,String url) {
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
				nextPaeUrl = UrlUtil.parseHostUrl(url) + href;
				break;
			}
		}
		return nextPaeUrl;
	}

	@Override
	public Map<String, String> parsePageInfo(Document doc,String url) throws BusinessException{

		if (doc == null || ParsePageUtil.judgeRobot(doc)) {
			ThreadUtil.sleepTime(300000L);
			throw new BusinessException("网络异常");
		}
		Map<String, String> pageInfo = getEnPageInfo(doc);
		return pageInfo;
	}

	// 解析英国数据
	@Override
	public String getThrEnPageUrl(Document doc,String url) {
		String keyId = "merchant-info";
		return getUrlById(doc, keyId);

	}
	/**
	 * 获取品牌信息
	 * @param url
	 * @return
	 */
	public Map<String,String> getBrandInfo(String url){
		Map<String,String> result = new HashMap<>();
		Document pageDom = HttpUtil.getPageInfo(url,
				HttpUtil.getCookieMap(UrlUtil.parseHostUrl(url), cookieMap));
        result.put("finalUrl",getThrEnPageUrl(pageDom,url));
		//详情页地址
		result.put("detailUrl",url);
		//获取title
		result.put("title",getTitle(pageDom));
		//获取brand
        result.put("brand",getBylineInfo(pageDom));
        //获取星级
        result.put("start",getAppraiseStar(pageDom));
        //获取客户浏览量
        result.put("customView",getCustomerView(pageDom.toString()));
        getAdditionalInfo(pageDom,result);
		return result;
	}

    /**
     * 获取附加信息
     * @param pageDom
     * @param map
     */
	private void getAdditionalInfo(Document pageDom,Map<String,String> map){
        Element e = getElementById(pageDom,"productDetails_detailBullets_sections1");
        if(e != null){
            Elements es = e.getElementsByTag("tr");
            for (Element element : es) {
                Elements ths = element.getElementsByTag("th");
                Elements tds = element.getElementsByTag("td");
                String key = "";
                String value = "";
                if(ths != null){
                    Element th = ths.get(0);
                    key = th.text();
                }
                if(tds != null){
                    Element td = ths.get(0);
                    value = td.text();
                }
                map.put(key,value);
            }
        }
    }
    private static String getCustomerView(String text) {
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
    private static String getAppraiseStar(Document doc) {
        Elements contents = doc.getElementsByClass(starClass);
        if(contents.size()>0){
            return contents.get(0).ownText();
        }
        String star = "";
        return star;

    }

    private String getBylineInfo(Document doc) {
        Element content = doc.getElementById("bylineInfo");
        if (content == null) {
            content = doc.getElementById("olpProductByline");
        }
        if (content == null) {
            return "";
        }
        return "by " + content.text();
    }
	/**
	 * 获取title
	 * @param pageDom
	 * @return
	 */
	private String getTitle(Document pageDom){
		String title = "";
		String titleId = "productTitle";
		title = getStrById(pageDom,titleId);
		return title;
	}
	private String getStrById(Document doc,String keyId){
		String result = "";
		Element e = getElementById(doc,keyId);
		if (null == e) {
			return result;
		}
		return e.text();
	}

	private Element getElementById(Document doc,String keyId){
        if (null == doc) {
            return null;
        }
        Element e = doc.getElementById(keyId);
        return e;
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
			url = UrlUtil.parseHostUrl(url) + a.get(0).attr("href");
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
