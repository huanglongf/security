package com.gome.clickmap.model;

/**
 * 
 * @author chixiaoyong
 *
 */
public class QueryParam {

	private String pageUrl;
	private String startTime;
	private String endTime;
	private int min;

	public QueryParam() {
		super();
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	@Override
	public String toString() {
		return "QueryParam [pageUrl=" + pageUrl + ", startTime=" + startTime + ", endTime=" + endTime + ", min=" + min
				+ "]";
	}

	
	
}
