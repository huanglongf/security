package com.gome.pathanalyse.model;

import java.text.DecimalFormat;

/**
 * 页面路径分析查询的结果
 * @author wangshubao
 *
 */
public class PathAnalyseQueryResult {
	
	private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

	private String code;
	private int pv;
	private int uv;
	private int visits;
	
	private String pvRate;
	private String uvRate;
	private String visitsRate;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getPv() {
		return pv;
	}
	public void setPv(int pv) {
		this.pv = pv;
	}
	public int getUv() {
		return uv;
	}
	public void setUv(int uv) {
		this.uv = uv;
	}
	public int getVisits() {
		return visits;
	}
	public void setVisits(int visits) {
		this.visits = visits;
	}
	
	public void setPvRate(int pv, int allPv){
		double pvRate = 0.0;
		if(allPv!=0){
			pvRate = ((double)pv/(double)allPv)*100;
		}
		this.pvRate = decimalFormat.format(pvRate);
	}
	
	public void setUvRate(int uv, int allUv){
		double uvRate = 0.0;
		if(allUv!=0){
			uvRate = ((double)uv/(double)allUv)*100;
		}
		this.uvRate = decimalFormat.format(uvRate);
	}
	
	public void setVisitsRate(int visits, int allVisits){
		double visitsRate = 0.0;
		if(allVisits!=0){
			visitsRate = ((double)visits/ (double)allVisits)*100;
		}
		this.visitsRate = decimalFormat.format(visitsRate);
	}
	public String getPvRate() {
		return pvRate;
	}
	public void setPvRate(String pvRate) {
		this.pvRate = pvRate;
	}
	public String getUvRate() {
		return uvRate;
	}
	public void setUvRate(String uvRate) {
		this.uvRate = uvRate;
	}
	public String getVisitsRate() {
		return visitsRate;
	}
	public void setVisitsRate(String visitsRate) {
		this.visitsRate = visitsRate;
	}
}
