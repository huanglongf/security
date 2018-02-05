package com.gome.Controller.model;

/**
 * 页面路径分析查询controller端的model
 * @author wangshubao
 *
 */
public class PathAnalyseControllerQueryParam {

	private String pageSite;
	private String pageChannel;
	private String pageType;
	private String prefixurl;
	
	private String startTime;
	private String endTime;
	
	private String channel;
	
	private String datasource;
	
	private String pageIndex;
	private String pageSize;
	
	private String groupType;	//根据什么类型分组，取值范围：pageSite, pageChannel, pageType, prefixUrl
	
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
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(String pageIndex) {
		this.pageIndex = pageIndex;
	}
	public String getPageSize() {
		return pageSize;
	}
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}
	public String getDatasource() {
		return datasource;
	}
	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}
	public String getGroupType() {
		return groupType;
	}
	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}
	public String getPrefixurl() {
		return prefixurl;
	}
	public void setPrefixurl(String prefixurl) {
		this.prefixurl = prefixurl;
	}
	
	
}
