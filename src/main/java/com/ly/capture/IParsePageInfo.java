package com.ly.capture;

import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Document;

import com.ly.exception.BusinessException;

public interface IParsePageInfo {

	public static final String AMAZON_HOME = "https://www.amazon";
	
	/**
	 * 获取网页下的所有链接
	 * @param doc
	 * @return
	 */
	Set<String> getPageAllUrl(Document doc,String url)throws Exception;
	/**
	 * 获取下一页链接地址
	 * @param doc
	 * @return
	 */
	public String getNextUrl(Document doc,int currentPage,String url);
	
	/**
	 * 解析页面信息
	 * @return
	 */
	public Map<String,String> parsePageInfo(Document doc,String url) throws BusinessException;
	/**
	 * 解析第三页
	 * @param doc
	 * @return
	 */
	public String getThrEnPageUrl(Document doc,String url) ;

	/**
	 * 获取品牌信息
	 * @param url
	 * @return
	 */
	public Map<String,String> getBrandInfo(String url);
	
}
