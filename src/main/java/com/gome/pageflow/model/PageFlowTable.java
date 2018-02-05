package com.gome.pageflow.model;

import java.io.Serializable;
import java.text.DecimalFormat;

public class PageFlowTable implements Serializable {

	private static final long serialVersionUID = 1729344267167031045L;

	private String pageContent = "";

	private String date;
	private String uv;

	private String pv;

	private String visitor;

	private String click;

	private String entries;

	private String bounces;

	private String exits;

	private String takeOrdercount;

	private String takeOrderAmount;

	private String viewTime;

	private String avgVisitTime;

	private String fpv;

	private String takeRate;
	private String clickRate;
	private String perClick;
	private String exitsRate;
	private String bouncesRate;
	private String entriesRate;
	private String rateString = "";

	private String checkbox = "<button class='checkbox_gm' onclick='checkboxChange(this)' ></button>";
	private transient DecimalFormat decimalFormat = new DecimalFormat("###0.00");
	private transient DecimalFormat df = new DecimalFormat("###,##0.##");

	public String getPageContent() {
		return pageContent;
	}

	public void setPageContent(String pageContent, boolean strong) {
		if (strong) {

			this.pageContent = "<strong>" + pageContent + "</strong>";

		} else {
			this.pageContent = pageContent;
		}

	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUv() {
		return uv;
	}

	public void setUv(long uv, long totalUv, boolean strong) {

		double uvRate = 0.0;

		if (totalUv != 0) {
			uvRate = (uv / (totalUv * 1.0)) * 100;
		}

		if (strong) {
			this.uv = "<strong>" + df.format(uv) + "</strong>" + rateString + "(" + decimalFormat.format(uvRate) + "%)";

		} else {
			this.uv = df.format(uv) + rateString + "(" + decimalFormat.format(uvRate) + "%)";
		}

	}

	public String getPv() {
		return pv;
	}

	public void setPv(long pv, long totalPv, boolean strong) {

		double pvRate = 0.0;
		if (totalPv != 0) {
			pvRate = (pv / (totalPv * 1.0)) * 100;
		}

		if (strong) {
			this.pv = "<strong>" + df.format(pv) + "</strong>" + rateString + " (" + decimalFormat.format(pvRate)
					+ "%)";

		} else {

			this.pv = df.format(pv) + rateString + " (" + decimalFormat.format(pvRate) + "%)";
		}

	}

	public String getVisitor() {
		return visitor;
	}

	public void setVisitor(long visitor, long totalVisitor, boolean strong) {

		double visitorRate = 0.0;
		if (totalVisitor != 0) {
			visitorRate = (visitor / (totalVisitor * 1.0)) * 100;
		}

		if (strong) {
			this.visitor = "<strong>" + df.format(visitor) + "</strong>" + rateString + " ("
					+ decimalFormat.format(visitorRate) + "%)";

		} else {
			this.visitor = df.format(visitor) + rateString + " (" + decimalFormat.format(visitorRate) + "%)";

		}
	}

	public String getClick() {

		return click;
	}

	public void setClick(long click, boolean strong) {
		if (strong) {
			this.click = "<strong>" + df.format(click) + "</strong>";
		} else {
			this.click = df.format(click);
		}

	}

	public String getEntries() {
		return entries;
	}

	public void setEntries(long entries, long totalEntries, boolean strong) {
		double rate = 0.0;
		if (totalEntries != 0) {
			rate = (entries / (totalEntries * 1.0)) * 100;
		}

		if (strong) {
			this.entries = "<strong>" + df.format(entries) + "</strong>" + rateString + " ("
					+ decimalFormat.format(rate) + "%)";
		} else {
			this.entries = df.format(entries) + rateString + " (" + decimalFormat.format(rate) + "%)";
		}

	}

	public String getBounces() {
		return bounces;
	}

	public void setBounces(long bounces, long totalBounces, boolean strong) {
		double rate = 0.0;
		if (totalBounces != 0) {
			rate = (bounces / (totalBounces * 1.0)) * 100;
		}

		if (strong) {

			this.bounces = "<strong>" + df.format(bounces) + "</strong>" + rateString + " ("
					+ decimalFormat.format(rate) + "%)";

		} else {
			this.bounces = df.format(bounces) + rateString + " (" + decimalFormat.format(rate) + "%)";

		}

	}

	public String getExits() {
		return exits;
	}

	public void setExits(long exits, long totalExits, boolean strong) {
		double rate = 0.0;
		if (totalExits != 0) {
			rate = (exits / (totalExits * 1.0)) * 100;
		}

		if (strong) {

			this.exits = "<strong>" + df.format(exits) + "</strong>" + rateString + "(" + decimalFormat.format(rate)
					+ "%)";

		} else {
			this.exits = df.format(exits) + rateString + "(" + decimalFormat.format(rate) + "%)";

		}
	}

	public String getTakeOrdercount() {
		return takeOrdercount;
	}

	public void setTakeOrdercount(long takeOrdercount, long totalTakeOrderCount, boolean strong) {

		double rate = 0.0;
		if (totalTakeOrderCount != 0) {
			rate = (takeOrdercount / (totalTakeOrderCount * 1.0)) * 100;
		}

		if (strong) {
			this.takeOrdercount = "<strong>" + df.format(takeOrdercount) + "</strong>" + rateString + "("
					+ decimalFormat.format(rate) + "%)";

		} else {
			this.takeOrdercount = df.format(takeOrdercount) + rateString + "(" + decimalFormat.format(rate) + "%)";

		}
	}

	public String getTakeOrderAmount() {
		return takeOrderAmount;
	}

	public void setTakeOrderAmount(double takeOrderAmount, double totalTakeAmount, boolean strong) {

		double totalTakeAmountRate = 0.0;
		if (totalTakeAmount != 0.0d) {
			totalTakeAmountRate = (takeOrderAmount / (totalTakeAmount * 1.0)) * 100;
		}
		DecimalFormat dfd = new DecimalFormat("###,##0.00");

		if (strong) {
			this.takeOrderAmount = "<strong>" + dfd.format(takeOrderAmount) + "</strong>" + rateString + "("
					+ decimalFormat.format(totalTakeAmountRate) + "%)";
		} else {
			this.takeOrderAmount = dfd.format(takeOrderAmount) + rateString + "("
					+ decimalFormat.format(totalTakeAmountRate) + "%)";
		}

	}

	public String getViewTime() {
		return viewTime;
	}

	public void setViewTime(String viewTime, boolean strong) {
		if (strong) {
			this.viewTime = "<strong>" + viewTime + "</strong>";
		} else {
			this.viewTime = viewTime;
		}

	}

	public String getAvgVisitTime() {
		return avgVisitTime;
	}

	public void setAvgVisitTime(long avgVisitTime, boolean strong) {
		long seconds = (long) (avgVisitTime / 1000);

		long minutes = seconds / 60;

		long second = seconds % 60;

		long hour = minutes / 60;

		long minute = minutes % 60;

		String secondString = second + "";
		String minuteString = minute + "";
		String hourString = hour + "";
		if (secondString.length() == 1) {
			secondString = "0" + secondString;
		}

		if (minuteString.length() == 1) {
			minuteString = "0" + minute;
		}
		if (hourString.length() == 1) {
			hourString = "0" + hourString;
		}

		String avgVisitTimeString = hourString + ":" + minuteString + ":" + secondString;
		if (strong) {
			avgVisitTimeString = "<strong>" + avgVisitTimeString + "</strong>";
		}

		this.avgVisitTime = avgVisitTimeString;
	}

	public String getFpv() {
		return fpv;
	}

	public void setFpv(String fpv, boolean strong) {
		if (strong) {
			this.fpv = "<strong>" + df.format(fpv) + "</strong>";
		} else {
			this.fpv = df.format(fpv);
		}

	}

	public String getTakeRate() {
		return takeRate;
	}

	public void setTakeRate(String takeRate, boolean strong) {
		if (strong) {
			this.takeRate = "<strong>" + takeRate + "</strong>";
		} else {
			this.takeRate = takeRate;
		}

	}

	public String getClickRate() {
		return clickRate;
	}

	public void setClickRate(String clickRate, boolean strong) {
		if (strong) {
			this.clickRate = "<strong>" + clickRate + "</strong>";
		} else {
			this.clickRate = clickRate;
		}

	}

	public String getPerClick() {
		return perClick;
	}

	public void setPerClick(double perClick, boolean strong) {
		if (strong) {
			this.perClick = "<strong>" + df.format(perClick) + "</strong>";
		} else {
			this.perClick = df.format(perClick);
		}

	}

	public String getExitsRate() {
		return exitsRate;
	}

	public void setExitsRate(String exitsRate, boolean strong) {
		if (strong) {
			this.exitsRate = "<strong>" + exitsRate + "</strong>";
		} else {
			this.exitsRate = exitsRate;

		}
	}

	public String getBouncesRate() {
		return bouncesRate;
	}

	public void setBouncesRate(String bouncesRate, boolean strong) {

		if (strong) {
			this.bouncesRate = "<strong>" + bouncesRate + "</strong>";
		} else {
			this.bouncesRate = bouncesRate;
		}
	}

	public String getCheckbox() {
		return checkbox;
	}

	public void setCheckbox(String checkbox) {
		this.checkbox = checkbox;
	}

	public String getEntriesRate() {
		return entriesRate;
	}

	public void setEntriesRate(String entriesRate, boolean strong) {
		if (strong) {
			this.entriesRate = "<strong>" + entriesRate + "</strong>";
		} else {
			this.entriesRate = entriesRate;
		}
	}

	public void setRateString(String rateString, boolean strong) {
		if (strong) {
			this.rateString = "<strong>" + rateString + "</strong>";
		} else {
			this.rateString = rateString;
		}
	}

	@Override
	public String toString() {
		return "PageFlow [pageContent=" + pageContent + ", date=" + date + ", uv=" + uv + ", pv=" + pv + ", visitor="
				+ visitor + ", click=" + click + ", entries=" + entries + ", bounces=" + bounces + ", exits=" + exits
				+ ", takeOrdercount=" + takeOrdercount + ", takeOrderAmount=" + takeOrderAmount + ", viewTime="
				+ viewTime + ", avgVisitTime=" + avgVisitTime + ", fpv=" + fpv + ", takeRate=" + takeRate
				+ ", clickRate=" + clickRate + ", perClick=" + perClick + ", exitsRate=" + exitsRate + ", bouncesRate="
				+ bouncesRate + ", checkbox=" + checkbox + "]";
	}

}
