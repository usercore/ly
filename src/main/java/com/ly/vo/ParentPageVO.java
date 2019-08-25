package com.ly.vo;

public class ParentPageVO {

	private String currentUrl ;
	
	private int currentPage;
	
	private ParentPageVO nextPage;
	
	private int pageSize;

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getCurrentUrl() {
		return currentUrl;
	}

	public void setCurrentUrl(String currentUrl) {
		this.currentUrl = currentUrl;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public ParentPageVO getNextPage() {
		return nextPage;
	}

	public void setNextPage(ParentPageVO nextPage) {
		this.nextPage = nextPage;
	}
	
	@Override
	public String toString() {
		return "currentUrl=" + currentUrl + "  currentPage=" + currentPage + " nextPage=" + nextPage;
	}
	
	
	
}
