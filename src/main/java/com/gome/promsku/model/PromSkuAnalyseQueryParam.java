package com.gome.promsku.model;

/**
 * 活动页-商品分析查询参数
 * @author wangshubao
 *
 */
public class PromSkuAnalyseQueryParam {

	private String promUrl;			//活动页url
	private String datasource;		//站点
	private String startDate;		//起始时间
	private String endDate;			//结束时间
	
	private String skuStartDate;	//sku的起始时间
	private String skuEndDate;		//sku的结束时间
	
	private int start = 0;			//从0开始
	private int length = 20;			//每页的行数
	
	private String orderColumn;		//排序字段
	private String orderType;		//排序方式
	
	private String skuId;			//skuId模糊匹配或查询可卖数的精确skuid
	
	
	public String getPromUrl() {
		return promUrl;
	}
	public void setPromUrl(String promUrl) {
		this.promUrl = promUrl;
	}
	public String getDatasource() {
		return datasource;
	}
	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public String getOrderColumn() {
		return orderColumn;
	}
	public void setOrderColumn(String orderColumn) {
		this.orderColumn = orderColumn;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getSkuId() {
		return skuId;
	}
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	public String getSkuStartDate() {
		return skuStartDate;
	}
	public void setSkuStartDate(String skuStartDate) {
		this.skuStartDate = skuStartDate;
	}
	public String getSkuEndDate() {
		return skuEndDate;
	}
	public void setSkuEndDate(String skuEndDate) {
		this.skuEndDate = skuEndDate;
	}
	
	
	
}
