package com.gome.Controller.model;

import java.text.DecimalFormat;

/**
 * 资源位对象
 * 
 * @author chixiaoyong
 *
 */
public class ResourceResponse {

	private String code="";

	private double uv;

	private double uvRate;
	// pv 点击量
	private double click;

	private double clickRate;
	// 后续浏览量
	private double pv;

	private double avgPv;
	
	private double vistor;

	private double vistorRate;
	// 网页标题
	private String title;
	// 参与订单量
	private double orderCount;
	// 参与销售金额
	private double saleAmount;
	// 订单转换率
	private double takeOrderRate;

	private String url = "";
	
	private double OrderAllRate;
	
	private transient DecimalFormat decimalFormat;

	public ResourceResponse() {
		super();
		decimalFormat = new DecimalFormat("##0.00");
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public double getUv() {
		return uv;
	}

	public void setUv(double uv) {
		this.uv = uv;
	}

	public double getUvRate() {
		return uvRate;
	}

	public void setUvRate(double resUv, double pageUv) {
		double uvRate = 0.0;
		if (pageUv != 0) {
			uvRate = (resUv / (pageUv * 1.0)) * 100;
		}

		this.uvRate = Double.parseDouble(decimalFormat.format(uvRate));
	}

	public double getVistor() {
		return vistor;
	}

	public void setVistor(double vistor) {
		this.vistor = vistor;
	}

	public double getVistorRate() {
		return vistorRate;
	}

	public void setVistorRate(double resVistor, double pageVistor) {
		double vistorRate = 0.0;
		if (pageVistor != 0) {
			vistorRate = resVistor / (pageVistor * 1.0) * 100;
		}
		this.vistorRate = Double.parseDouble(decimalFormat.format(vistorRate));
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

	public double getTakeOrderRate() {
		return takeOrderRate;
	}

	public void setTakeOrderRate(double orderCount, double uv) {
		double orderConvRate = 0;
		if (uv != 0) {
			orderConvRate = (orderCount / (uv * 1.0)) * 100;
		}
		this.takeOrderRate = Double.parseDouble(decimalFormat.format(orderConvRate));
	}

	public double getClick() {
		return click;
	}

	public void setClick(double click) {
		this.click = click;
	}

	public double getClickRate() {
		return clickRate;
	}

	public void setClickRate(double click, double pagePv) {
		double clickRate = 0.0;
		if (pagePv != 0) {
			clickRate = (click / (pagePv * 1.0)) * 100;
		}

		this.clickRate = Double.parseDouble(decimalFormat.format(clickRate));
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public double getPv() {
		return pv;
	}

	public void setPv(double pv) {
		this.pv = pv;
	}

	
	public double getOrderAllRate() {
		return OrderAllRate;
	}

	public void setOrderAllRate(double orderCount,double allOrderCount) {
		double orderAllRate=0.0;
		if(allOrderCount !=0){
			orderAllRate=(orderCount/(allOrderCount*1.0))*100;
		}
		this.OrderAllRate = Double.parseDouble(decimalFormat.format(orderAllRate));
	}

	public double getAvgPv() {
		return avgPv;
	}

	public void setAvgPv(double pv,double uv) {
		double avgPv=0.0;
		if(uv !=0){
			avgPv=(pv/(uv*1.0));
		}
		this.avgPv = Double.parseDouble(decimalFormat.format(avgPv));
	}

	@Override
	public String toString() {
		return "ResourceResponse [code=" + code + ", uv=" + uv + ", uvRate=" + uvRate + ", click=" + click
				+ ", clickRate=" + clickRate + ", pv=" + pv + ", avgPv=" + avgPv + ", vistor=" + vistor
				+ ", vistorRate=" + vistorRate + ", title=" + title + ", orderCount=" + orderCount + ", saleAmount="
				+ saleAmount + ", orderConvRate=" + takeOrderRate + ", url=" + url + ", OrderAllRate=" + OrderAllRate
				+ "]";
	}

}
