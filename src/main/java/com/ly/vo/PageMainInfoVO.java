package com.ly.vo;

public class PageMainInfoVO {

	//by product
	private String byInfo;
	//星级
	private String star;
	//客户浏览量
	private String customerViews;
	//当前网页
	private String url;
	//产品首词
	private String firstWord;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getByInfo() {
		return byInfo;
	}
	public void setByInfo(String byInfo) {
		this.byInfo = byInfo;
	}
	public String getStar() {
		return star;
	}
	public void setStar(String star) {
		this.star = star;
	}
	public String getCustomerViews() {
		return customerViews;
	}
	public void setCustomerViews(String customerViews) {
		this.customerViews = customerViews;
	}
	
	public String toString(){
		return byInfo + " " + star + " " + customerViews;
	}
	public String getFirstWord() {
		return firstWord;
	}
	public void setFirstWord(String firstWord) {
		this.firstWord = firstWord;
	}
	
}
