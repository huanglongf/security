package com.gome.promsku.model;

/**
 * 活动页-商品分析对应的城市相关的指标
 * @author wangshubao
 *
 */
public class PromSkuAnalyseCityInfo {

	private String cityName;
	private Integer availableselling;
	private int pv;
	private int orderCount;
	private int orderQuantity;

	public PromSkuAnalyseCityInfo() {
		super();
	}
	
	public PromSkuAnalyseCityInfo(String cityName) {
		super();
		this.cityName = cityName;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public Integer getAvailableselling() {
		return availableselling;
	}
	public void setAvailableselling(Integer availableselling) {
		this.availableselling = availableselling;
	}
	public int getPv() {
		return pv;
	}
	public void setPv(int pv) {
		this.pv = pv;
	}
	public int getOrderCount() {
		return orderCount;
	}
	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}

	public int getOrderQuantity() {
		return orderQuantity;
	}

	public void setOrderQuantity(int orderQuantity) {
		this.orderQuantity = orderQuantity;
	}
	
	
}
