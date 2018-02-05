package com.gome.Controller.model;

/**
 * app首页热力图查询条件
 * @author wangshubao
 *
 */
public class AppHotMapQueryParam {

	private String startTime;	//开始时间，格式yyyy-MM-dd
	private String endTime;		//结束时间，格式yyyy-MM-dd
	private String intcmp;		//资源位id
	private String dataSource;
	
	
	public AppHotMapQueryParam() {
		super();
	}
	
	
	public AppHotMapQueryParam(String startTime, String endTime, String intcmp) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.intcmp = intcmp;
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
	public String getIntcmp() {
		return intcmp;
	}
	public void setIntcmp(String intcmp) {
		this.intcmp = intcmp;
	}


	public String getDataSource() {
		return dataSource;
	}


	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	
	
}
