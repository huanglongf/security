package com.gome.Controller.model;

import java.text.DecimalFormat;

/**
 * 资源位对象
 * 
 * @author chixiaoyong
 *
 */
public class ChannelResponse {

	private String code = "";

	private double uv;

	private double uvRate;

	private double pv;

	private double pvRate;

	private double vistor;

	private double vistorRate;

	private double click;

	private double bounce;
	private double bounceRate;

	private double exitCount;

	private double exitRate;

	private double viewTime;

	// 着陆页的数量
	private double entriesCount;

	private double takeOrderRate;

	private double orderTotalAmount;
	private double takeOrderCount;

	private transient DecimalFormat decimalFormat;

	public ChannelResponse() {

		super();
		decimalFormat = new DecimalFormat("#.00");
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

	public void setBounce(double bounce, double entriesCount) {
		double bounceRate = 0.0;
		if (entriesCount != 0) {
			bounceRate = (bounce / (entriesCount * 1.0)) * 100;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public double getViewTime() {
		return viewTime;
	}

	public void setViewTime(double viewTime) {
		this.viewTime = viewTime / 1000;
	}

	public double getEntriesCount() {
		return entriesCount;
	}

	public void setEntriesCount(double entriesCount) {
		this.entriesCount = entriesCount;
	}

	public double getTakeOrderRate() {
		return takeOrderRate;
	}

	public void setTakeOrderRate(double orderCount, double uv) {
		double takeOrderRate = 0.0;
		if (uv != 0) {
			takeOrderRate = (orderCount / (uv * 1.0)) * 100;
		}
		this.takeOrderRate = Double.parseDouble(decimalFormat.format(takeOrderRate));

	}

	public double getOrderTotalAmount() {
		return orderTotalAmount;
	}

	public void setOrderTotalAmount(double orderTotalAmount) {
		
		
		this.orderTotalAmount =Double.parseDouble(decimalFormat.format(orderTotalAmount)) ;
	}

	public double getTakeOrderCount() {
		return takeOrderCount;
	}

	public void setTakeOrderCount(double takeOrderCount) {
		this.takeOrderCount = takeOrderCount;
	}



}
