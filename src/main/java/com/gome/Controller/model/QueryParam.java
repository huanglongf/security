package com.gome.Controller.model;

/**
 * 查询参数值类
 * @author wangshubao
 *
 */
public class QueryParam {

	private String pageUrl;		//页面的url
	private String channel;		//渠道，可为空
	private String startTime;	//开始时间，格式yyyy-MM-dd
	private String endTime;		//结束时间，格式yyyy-MM-dd
	private String dataSource;	//来源
	private String intcmp;		//资源位id
	
	private String dataSourceLike;	//融合项目后，需要查询多个datasource
	
	private Boolean queryResource = true;	//是否查询资源位
	
	private int isCache;

	
	/**
	 * @return the isCache
	 */
	public int getIsCache() {
		return isCache;
	}
	/**
	 * @param isCache the isCache to set
	 */
	public void setIsCache(int isCache) {
		this.isCache = isCache;
	}
	public String getPageUrl() {
		return pageUrl;
	}
	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
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
	public String getDataSource() {
		return dataSource;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getIntcmp() {
		return intcmp;
	}
	public void setIntcmp(String intcmp) {
		this.intcmp = intcmp;
	}
	public Boolean getQueryResource() {
		return queryResource;
	}
	public void setQueryResource(Boolean queryResource) {
		this.queryResource = queryResource;
	}
	public String getDataSourceLike() {
		return dataSourceLike;
	}
	public void setDataSourceLike(String dataSourceLike) {
		this.dataSourceLike = dataSourceLike;
	}
	@Override
	public String toString() {
		return "QueryParam [pageUrl=" + pageUrl + ", channel=" + channel + ", startTime=" + startTime + ", endTime="
				+ endTime + ", dataSource=" + dataSource + ", intcmp=" + intcmp + ", dataSourceLike=" + dataSourceLike
				+ ", queryResource=" + queryResource + "]";
	}
	
}
