package com.gome.dao.model;

import java.text.DecimalFormat;

public class PageChannel {
	
	private String  channelCode = "";
	
	private String url;
	// uv
	private long uv;
	// pv
	private long pv;

	private long click;

	private long vistor;
	
	private long bounce;
	
	private long exitCount;
	
	private long entriesCount;
	
	private String date;

	// dataType=1,PC的channel , dataType=3   是WAP的channel
	
	private String dataType;
    
	//数据更新时间
	private String updateTime;
	
	private double viewTime;
	
	private double participationOrdersCount;
	
	private double orderTotalAmount;
	
	private transient DecimalFormat decimalFormat;

	 
	public PageChannel() {
		super();
		decimalFormat = new DecimalFormat("#.00");
	}


	public String getChannelCode() {
		return channelCode;
	}


	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public long getUv() {
		return uv;
	}


	public void setUv(long uv) {
		this.uv = uv;
	}


	public long getPv() {
		return pv;
	}


	public void setPv(long pv) {
		this.pv = pv;
	}


	public long getClick() {
		return click;
	}


	public void setClick(long click) {
		this.click = click;
	}


	public long getVistor() {
		return vistor;
	}


	public void setVistor(long vistor) {
		this.vistor = vistor;
	}


	public long getBounce() {
		return bounce;
	}


	public void setBounce(long bounce) {
		this.bounce = bounce;
	}


	public long getExitCount() {
		return exitCount;
	}


	public void setExitCount(long exitCount) {
		this.exitCount = exitCount;
	}


	public long getEntriesCount() {
		return entriesCount;
	}


	public void setEntriesCount(long entriesCount) {
		this.entriesCount = entriesCount;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}


	public String getDataType() {
		return dataType;
	}


	public void setDataType(String dataType) {
		this.dataType = dataType;
	}


	public DecimalFormat getDecimalFormat() {
		return decimalFormat;
	}


	public void setDecimalFormat(DecimalFormat decimalFormat) {
		this.decimalFormat = decimalFormat;
	}


	public String getUpdateTime() {
		return updateTime;
	}


	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}


	public double getViewTime() {
		return viewTime;
	}


	public void setViewTime(double viewTime) {
		this.viewTime = viewTime;
	}

	public double getParticipationOrdersCount() {
		return participationOrdersCount;
	}


	public void setParticipationOrdersCount(double participationOrdersCount) {
		this.participationOrdersCount = participationOrdersCount;
	}


	public double getOrderTotalAmount() {
		return orderTotalAmount;
	}


	public void setOrderTotalAmount(double orderTotalAmount) {
		this.orderTotalAmount = orderTotalAmount;
	}

	

}
