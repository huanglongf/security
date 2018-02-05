package com.gome.promsku.model;

import java.util.List;

/**
 * 活动页商品分析查询的返回结果
 * @author wangshubao
 *
 */
public class PromSkuAnalyseResponse {

	private String pageName;		//标题
	private List<PromSkuInfoServiceResult> skuInfos;		//当前页的商品结果
	private int amount;				//总行数
	private String code="200";
	
	
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public List<PromSkuInfoServiceResult> getSkuInfos() {
		return skuInfos;
	}
	public void setSkuInfos(List<PromSkuInfoServiceResult> skuInfos) {
		this.skuInfos = skuInfos;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
}
