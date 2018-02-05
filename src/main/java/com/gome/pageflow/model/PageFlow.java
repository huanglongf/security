package com.gome.pageflow.model;

import java.io.Serializable;
import java.text.DecimalFormat;

public class PageFlow implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5961114076384976623L;

	private String pageContent = "";

	private String date;
	private long uv;

	private long pv;

	private long visitor;

	private long click;

	private long entries;

	private long bounces;

	private long exits;

	private long takeOrdercount;

	private double takeOrderAmount;

	private long viewTime;

	private long avgVisitTime;

	private long fpv;

	private String takeRate;

	private String clickRate;

	private double perClick;

	private String exitsRate;

	private String entriesRate;

	private String bouncesRate;

	private String checkbox = "<button class='checkbox_gm' onclick='checkboxChange(this)' ></button>";

	private static transient DecimalFormat decimalFormat = new DecimalFormat("#0.00");

	public String getPageContent() {
		return pageContent;
	}

	public void setPageContent(String pageContent) {
		this.pageContent = pageContent;
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

	public long getVisitor() {
		return visitor;
	}

	public void setVisitor(long visitor) {
		this.visitor = visitor;
	}

	public long getAvgVisitTime() {
		return avgVisitTime;
	}

	public void setAvgVisitTime(long viewtime, long pv) {
		long avgVisitTime = 0;
		if (pv != 0) {
			avgVisitTime = viewtime / pv;

		}

		this.avgVisitTime = avgVisitTime;

	}

	public long getClick() {
		return click;
	}

	public void setClick(long click) {
		this.click = click;
	}

	public long getEntries() {
		return entries;
	}

	public void setEntries(long entries) {
		this.entries = entries;
	}

	public long getBounces() {
		return bounces;
	}

	public void setBounces(long bounces) {
		this.bounces = bounces;
	}

	public long getExits() {
		return exits;
	}

	public void setExits(long exits) {
		this.exits = exits;
	}

	public long getTakeOrdercount() {
		return takeOrdercount;
	}

	public void setTakeOrdercount(long takeOrdercount) {
		this.takeOrdercount = takeOrdercount;
	}

	public double getTakeOrderAmount() {
		return takeOrderAmount;
	}

	public void setTakeOrderAmount(double takeOrderAmount) {
		this.takeOrderAmount = Double.parseDouble(decimalFormat.format(takeOrderAmount));
	}

	public void setTakeRate(long takeOrderCount, long uv) {
		double takeRate = 0.0;

		if (uv != 0) {
			takeRate = (takeOrderCount / (uv * 1.0)) * 100;
		}

		// takeRate = takeRate;

		this.takeRate = decimalFormat.format(takeRate) + "%";
	}

	public void setClickRate(long click, long pv) {
		double clickRate = 0.0;
		if (pv != 0) {
			clickRate = (click / (pv * 1.0)) * 100;
		}
		this.clickRate = decimalFormat.format(clickRate) + "%";
	}

	public void setPerClick(long click, long uv) {
		double perClickRate = 0.0;
		if (uv != 0) {
			perClickRate = (click / (uv * 1.0));
		}

		this.perClick = Double.parseDouble(decimalFormat.format(perClickRate));
	}

	public void setExitsRate(long exits, long visitor) {
		double exitsRate = 0.0;
		if (visitor != 0) {
			exitsRate = (exits / (visitor * 1.0)) * 100;
		}
		exitsRate = valid(exitsRate);

		this.exitsRate = decimalFormat.format(exitsRate) + "%";
	}

	public void setBouncesRate(long bounces, long entries) {
		double bouncesRate = 0.0;
		if (entries != 0) {
			bouncesRate = (bounces / (entries * 1.0)) * 100;
		}

		bouncesRate = valid(bouncesRate);

		this.bouncesRate = decimalFormat.format(bouncesRate) + "%";
	}

	public long getViewTime() {
		return viewTime;
	}

	public void setViewTime(long viewTime) {
		this.viewTime = viewTime;
	}

	public long getFpv() {
		return fpv;
	}

	public void setFpv(long fpv) {
		this.fpv = fpv;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCheckbox() {
		return checkbox;
	}

	public void setCheckbox(boolean checkbox) {
		if (checkbox) {
			this.checkbox = "<button class='checkbox_gm checked' onclick='checkboxChange(this)' disabled='disabled'></button>";

		}

	}

	public void setEntriesRate(long entries, long visitor) {

		double rate = 0.0;
		if (visitor != 0) {
			rate = (entries / (visitor * 1.0)) * 100;
		}

		rate = valid(rate);

		this.entriesRate = decimalFormat.format(rate) + "%";
	}

	@Override
	public String toString() {
		return "PageFlow [pageContent=" + pageContent + ", date=" + date + ", uv=" + uv + ", pv=" + pv + ", visitor="
				+ visitor + ", click=" + click + ", entries=" + entries + ", bounces=" + bounces + ", exits=" + exits
				+ ", takeOrdercount=" + takeOrdercount + ", takeOrderAmount=" + takeOrderAmount + ", viewTime="
				+ viewTime + ", avgVisitTime=" + avgVisitTime + ", fpv=" + fpv + ", takeRate=" + takeRate
				+ ", clickRate=" + clickRate + ", perClick=" + perClick + ", exitsRate=" + exitsRate + ", entriesRate="
				+ entriesRate + ", bouncesRate=" + bouncesRate + ", checkbox=" + checkbox + "]";
	}

	public String getTakeRate() {
		return takeRate;
	}

	public String getClickRate() {
		return clickRate;
	}

	public double getPerClick() {
		return perClick;
	}

	public String getExitsRate() {
		return exitsRate;
	}

	public String getEntriesRate() {
		return entriesRate;
	}

	public static DecimalFormat getDecimalFormat() {
		return decimalFormat;
	}

	public String getBouncesRate() {
		return bouncesRate;
	}

	/**
	 * 检验指标是否大于100
	 * 
	 * @param rate
	 * @return
	 */
	public double valid(double rate) {
		if (rate > 100.0) {

			rate = 100.0;
		}
		return rate;
	}
}
