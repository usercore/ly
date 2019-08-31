package com.ly.capture;

import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Document;

import com.ly.exception.BusinessException;

public interface IParsePageInfo {

	public static final String AMAZON_HOME = "https://www.amazon";
	
	/**
	 * 获取网页下的所有链接
	 * @param url
	 * @return
	 */
	public Set<String> getPageAllUrl(Document doc)throws Exception;
	/**
	 * 获取下一页链接地址
	 * @param doc
	 * @return
	 */
	public String getNextUrl(Document doc,int currentPage);
	
	/**
	 * 解析页面信息
	 * @return
	 */
	public Map<String,String> parsePageInfo(Document doc) throws BusinessException;
	/**
	 * 接卸第三页
	 * @param doc
	 * @return
	 */
	public String getThrEnPageUrl(Document doc) ;
	
}
