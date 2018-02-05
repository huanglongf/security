package com.gome.Controller.model;

import java.io.Serializable;
import java.text.DecimalFormat;

public class PageResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9169495407233580060L;

	private String pageDescription;
	
	private String startTime;

	private String endTime;

	private double uv;

	private double uvRate;

	private double pv;

	private double pvRate;

	private double vistor;

	private double vistorRate;

	private double click;
	
	private double clickRate;
	// 某个页面的跳出率
	private double bounce;
	// 全站跳出率
	private double bounceRate;

	private double exitCount;

	private double exitRate;

	private double takeOrderRate;
	
	private double entriesCount;
	private double orderTotalAmount;
	private double takeOrderCount;
	private double viewTime;
	private double avgViewTime;
	private transient DecimalFormat decimalFormat;

	public PageResponse() {

		super();
		decimalFormat = new DecimalFormat("0.00");
	}

	public String getPageDescription() {
		return pageDescription;
	}

	public void setPageDescription(String pageDescription) {
		this.pageDescription = pageDescription;
	}

	public double getUv() {
		return uv;
	}

	public void setUv(double uv) {
		this.uv = uv;
	}

	public double getUvRate() {
		return Double.parseDouble(decimalFormat.format(uvRate));
	}

	public void setUvRate(double pageUv, double allUV) {
		double uvRate = 0.0;
		if (allUV != 0) {
			uvRate = (pageUv / (allUV * 1.0) * 100);
		}
		this.uvRate = Double.parseDouble(decimalFormat.format(uvRate));
	}

	public double getPv() {
		return pv;
	}

	public void setPv(double pv) {
		this.pv = pv;
	}

	public double getPvRate() {
		return Double.parseDouble(decimalFormat.format(pvRate));
	}

	public void setPvRate(double pagePv, double allPv) {
		double pvRate = 0.0;
		if (allPv != 0) {
			pvRate = (pagePv / (allPv * 1.0)) * 100;
		}
		this.pvRate = Double.parseDouble(decimalFormat.format(pvRate));
	}

	public double getVistor() {
		return vistor;
	}

	public void setVistor(double vistor) {
		this.vistor = vistor;
	}

	public double getVistorRate() {
		return Double.parseDouble(decimalFormat.format(vistorRate));
	}

	public void setVistorRate(double pagevistor, double allVistor) {
		double vistorRate = 0.0;
		if (allVistor != 0) {
			vistorRate = (pagevistor / (allVistor * 1.0)) * 100;
		}
		this.vistorRate = Double.parseDouble(decimalFormat.format(vistorRate));
	}

	public double getClick() {
		return click;
	}

	public void setClick(double click) {
		this.click = click;
	}

	public double getBounce() {
		return bounce;
	}

	public void setBounce(double bounce, double visitor) {
		double bounceRate = 0.0;
		if (visitor != 0) {
			bounceRate = (bounce / (visitor * 1.0)) * 100;
		}
		this.bounce = Double.parseDouble(decimalFormat.format(bounceRate));
	}

	public double getBounceRate() {

		return Double.parseDouble(decimalFormat.format(bounceRate));
	}

	public void setBounceRate(double bounce, double allbounce) {
		double bounceRate = 0.0;
		if (allbounce != 0) {
			bounceRate = (bounce / (allbounce * 1.0)) * 100;
		}

		this.bounceRate = Double.parseDouble(decimalFormat.format(bounceRate));
	}

	public double getExitCount() {
		return exitCount;
	}

	public void setExitCount(double exitCount) {
		this.exitCount = exitCount;
	}

	public double getExitRate() {
		return Double.parseDouble(decimalFormat.format(exitRate));
	}

	public void setExitRate(double exit, double vistor) {
		double exitRate = 0.0;
		if (vistor != 0) {
			exitRate = (exit / (vistor * 1.0)) * 100;
		}
		this.exitRate = Double.parseDouble(decimalFormat.format(exitRate));
	}

	public DecimalFormat getDecimalFormat() {
		return decimalFormat;
	}

	public void setDecimalFormat(DecimalFormat decimalFormat) {
		this.decimalFormat = decimalFormat;
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

	public double getTakeOrderRate() {
		return takeOrderRate;
	}

	public void setTakeOrderRate(double orderCount,double uv) {
		double takeOrderRate=0.0;
		if(uv !=0){
			takeOrderRate=(orderCount / (uv * 1.0)) * 100;
		}
		this.takeOrderRate = Double.parseDouble(decimalFormat.format(takeOrderRate));

	}

	public double getEntriesCount() {
		return entriesCount;
	}

	public void setEntriesCount(double entriesCount) {
		this.entriesCount = entriesCount;
	}

	public double getViewTime() {
		return viewTime;
	}

	public void setViewTime(double viewTime) {
		this.viewTime = viewTime/1000;
	}

	public double getOrderTotalAmount() {
		return orderTotalAmount;
	}

	public void setOrderTotalAmount(double orderTotalAmount) {
		this.orderTotalAmount =  Double.parseDouble(decimalFormat.format(orderTotalAmount));
	}

	public double getTakeOrderCount() {
		return takeOrderCount;
	}

	public void setTakeOrderCount(double takeOrderCount) {
		this.takeOrderCount = takeOrderCount;
	}

	public double getClickRate() {
		return clickRate;
	}

	public void setClickRate(double click, double pv ) {
		double clickRate=0.0;
		if(pv !=0){
			clickRate=(click / (pv * 1.0)) * 100;
		}
		this.clickRate = Double.parseDouble(decimalFormat.format(clickRate));
	}

	public double getAvgViewTime() {
		return avgViewTime;
	}

	public void setAvgViewTime(double avgViewTime) {
		this.avgViewTime = avgViewTime;
	}

	public void setAvgViewTime(long pagepv, double viewTime2) {
		this.avgViewTime = pagepv==0 ? 0 : viewTime2/pagepv;
	}

	
	

}
