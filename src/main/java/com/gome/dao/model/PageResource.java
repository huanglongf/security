package com.gome.dao.model;

import java.text.DecimalFormat;

public class PageResource {
	private String code = "";

	private long uv;

	// pv 点击量
	private long click;

	// 后续浏览量
	private double pv;

	private long vistor;

	// 网页标题
	private String title;
	// 参与订单量
	private double orderCount;
	// 参与销售金额
	private double saleAmount;
	// 全站订单量
	private long orderALLCount;

	private String date;

	//活动页资源位的url
	private String url = "";

	// 数据更新时间
	private String updateTime;
	
	private String referUrl;
	
	private transient DecimalFormat decimalFormat;

	public PageResource() {
		super();
		decimalFormat = new DecimalFormat("#.00");
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public long getUv() {
		return uv;
	}

	public void setUv(long uv) {
		this.uv = uv;
	}

	public long getClick() {
		return click;
	}

	public void setClick(long click) {
		this.click = click;
	}

	public double getPv() {
		return pv;
	}

	public void setPv(double pv) {
		this.pv = pv;
	}

	public long getVistor() {
		return vistor;
	}

	public void setVistor(long vistor) {
		this.vistor = vistor;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(double orderCount) {
		this.orderCount = orderCount;
	}

	public double getSaleAmount() {
		return saleAmount;
	}

	public void setSaleAmount(double saleAmount) {
		this.saleAmount = Double.parseDouble(decimalFormat.format(saleAmount));
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getOrderALLCount() {
		return orderALLCount;
	}

	public void setOrderALLCount(long orderALLCount) {
		this.orderALLCount = orderALLCount;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public DecimalFormat getDecimalFormat() {
		return decimalFormat;
	}

	public void setDecimalFormat(DecimalFormat decimalFormat) {
		this.decimalFormat = decimalFormat;
	}

	public String getReferUrl() {
		return referUrl;
	}

	public void setReferUrl(String referUrl) {
		this.referUrl = referUrl;
	}

}
