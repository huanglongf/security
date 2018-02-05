package com.gome.pathanalyse.model;

/**
 * 页面路径分析的查询条件
 * @author wangshubao
 *
 */
public class PathAnalyseQueryParam {

	private String pageSite;
	private String pageChannel;
	private String pageType;
	private String prefixUrl;
	
	private String visitStartDay;
	private String visitEndDay;
	private String datasource;
	
	private String channel;
	
	private int pageIndex;
	private int pageSize;
	
	private int pageStart;
	
	private String topNGroupColumn;		//topN查询的时候，用于group的字段,内部使用。entry用referrer，follow用非referrer
	private String topNGroupColumnValue;	//查询uv，visits时的topNGroupColumn的具体值，内部用。entry用referrer，follow用非referrer

	public String getPageSite() {
		return pageSite;
	}

	public void setPageSite(String pageSite) {
		this.pageSite = pageSite;
	}

	public String getPageChannel() {
		return pageChannel;
	}

	public void setPageChannel(String pageChannel) {
		this.pageChannel = pageChannel;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}


	public String getVisitStartDay() {
		return visitStartDay;
	}

	public void setVisitStartDay(String visitStartDay) {
		this.visitStartDay = visitStartDay;
	}

	public String getVisitEndDay() {
		return visitEndDay;
	}

	public void setVisitEndDay(String visitEndDay) {
		this.visitEndDay = visitEndDay;
	}

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getTopNGroupColumn() {
		return topNGroupColumn;
	}

	public void setTopNGroupColumn(String topNGroupColumn) {
		this.topNGroupColumn = topNGroupColumn;
	}

	public int getPageStart() {
		return pageStart;
	}

	public void setPageStart(int pageStart) {
		this.pageStart = pageStart;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getTopNGroupColumnValue() {
		return topNGroupColumnValue;
	}

	public void setTopNGroupColumnValue(String topNGroupColumnValue) {
		this.topNGroupColumnValue = topNGroupColumnValue;
	}

	public String getPrefixUrl() {
		return prefixUrl;
	}

	public void setPrefixUrl(String prefixUrl) {
		this.prefixUrl = prefixUrl;
	}
	
	
}
