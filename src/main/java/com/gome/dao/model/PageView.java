package com.gome.dao.model;

public class PageView {

	private String pageSite;
	private String pageChannel;
	private String pageType;
	
	// uv
	private long uv;
	// pv
	private long pv;

	private long click;

	private long visits;
	// 页面跳出数
	private long bounce;
	// 页面退出数
	private long exitCount;
	// 着陆页的数量
	private long entriesCount;

	// session日期
	private String date;
	
	// 参与订单量
	private double participationOrdersCount;
	
	private double viewTime;
	
	//参与订单的总金额
	private double orderTotalAmount;
	
	private int allOrderCount;
	
	private String code;	//用来表示小时数、渠道

	public PageView() {
		super();
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public long getEntriesCount() {
		return entriesCount;
	}

	public void setEntriesCount(long entriesCount) {
		this.entriesCount = entriesCount;
	}

	public double getParticipationOrdersCount() {
		return participationOrdersCount;
	}

	public void setParticipationOrdersCount(double participationOrdersCount) {
		this.participationOrdersCount = participationOrdersCount;
	}

	public Double getViewTime() {
		return viewTime;
	}

	public void setViewTime(double viewTime) {
		this.viewTime = viewTime;
	}

	public double getOrderTotalAmount() {
		return orderTotalAmount;
	}

	public void setOrderTotalAmount(double orderTotalAmount) {
		this.orderTotalAmount = orderTotalAmount;
	}

	public long getVisits() {
		return visits;
	}

	public void setVisits(long visits) {
		this.visits = visits;
	}

	public int getAllOrderCount() {
		return allOrderCount;
	}

	public void setAllOrderCount(int allOrderCount) {
		this.allOrderCount = allOrderCount;
	}

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



}
