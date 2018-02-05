package com.gome.promsku.dao.model;

/**
 * 活动页-商品分析kylin查询出的结果model
 * @author wangshubao
 *
 */
public class PromSkuAnalyseDaoResult {

	private String pageTitle;
	private String pageStartDate;
	
	private String skuId;
	private String skuName;
	private String catdName1;
	private String catdName2;
	private String catdName3;
	private String brandName;
	private String skuStartDate;
	private String skuEndDate;
	private String skuUrl;
	private int skuUv;
	private int skuClick;
	private int promClick;
	private int promPv;
	
	private int orderCount;
	private float orderAmount;
	private int orderQuantity;
	private int skuAllPv;
	private int skuAllUv;
	
	private int orderCountBeforePromOneWeek;
	private int orderQuantityBeforePromOneWeek;
	private int skuPvBeforePromOneWeek;
	private int skuUvBeforePromOneWeek;
	
	private String intcmpModeId;
	private String productId;
	
	private boolean isAvailableSellingWarn = false;
	
	public PromSkuAnalyseDaoResult() {
		super();
	}
	public String getPageTitle() {
		return pageTitle;
	}
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}
	public String getPageStartDate() {
		return pageStartDate;
	}
	public void setPageStartDate(String pageStartDate) {
		this.pageStartDate = pageStartDate;
	}
	public String getSkuId() {
		return skuId;
	}
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	public String getSkuName() {
		return skuName;
	}
	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}
	public String getCatdName1() {
		return catdName1;
	}
	public void setCatdName1(String catdName1) {
		this.catdName1 = catdName1;
	}
	public String getCatdName2() {
		return catdName2;
	}
	public void setCatdName2(String catdName2) {
		this.catdName2 = catdName2;
	}
	public String getCatdName3() {
		return catdName3;
	}
	public void setCatdName3(String catdName3) {
		this.catdName3 = catdName3;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getSkuStartDate() {
		return skuStartDate;
	}
	public void setSkuStartDate(String skuStartDate) {
		this.skuStartDate = skuStartDate;
	}
	public int getSkuUv() {
		return skuUv;
	}
	public void setSkuUv(int skuUv) {
		this.skuUv = skuUv;
	}
	public int getSkuClick() {
		return skuClick;
	}
	public void setSkuClick(int skuClick) {
		this.skuClick = skuClick;
	}
	public int getPromClick() {
		return promClick;
	}
	public void setPromClick(int promClick) {
		this.promClick = promClick;
	}
	public int getPromPv() {
		return promPv;
	}
	public void setPromPv(int promPv) {
		this.promPv = promPv;
	}
	public int getOrderCount() {
		return orderCount;
	}
	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}
	public float getOrderAmount() {
		return orderAmount;
	}
	public void setOrderAmount(float orderAmount) {
		this.orderAmount = orderAmount;
	}
	public int getOrderQuantity() {
		return orderQuantity;
	}
	public void setOrderQuantity(int orderQuantity) {
		this.orderQuantity = orderQuantity;
	}
	public int getSkuAllPv() {
		return skuAllPv;
	}
	public void setSkuAllPv(int skuAllPv) {
		this.skuAllPv = skuAllPv;
	}
	public int getSkuAllUv() {
		return skuAllUv;
	}
	public void setSkuAllUv(int skuAllUv) {
		this.skuAllUv = skuAllUv;
	}
	public String getSkuEndDate() {
		return skuEndDate;
	}
	public void setSkuEndDate(String skuEndDate) {
		this.skuEndDate = skuEndDate;
	}
	public int getOrderCountBeforePromOneWeek() {
		return orderCountBeforePromOneWeek;
	}
	public void setOrderCountBeforePromOneWeek(int orderCountBeforePromOneWeek) {
		this.orderCountBeforePromOneWeek = orderCountBeforePromOneWeek;
	}
	public int getOrderQuantityBeforePromOneWeek() {
		return orderQuantityBeforePromOneWeek;
	}
	public void setOrderQuantityBeforePromOneWeek(int orderQuantityBeforePromOneWeek) {
		this.orderQuantityBeforePromOneWeek = orderQuantityBeforePromOneWeek;
	}
	public int getSkuPvBeforePromOneWeek() {
		return skuPvBeforePromOneWeek;
	}
	public void setSkuPvBeforePromOneWeek(int skuPvBeforePromOneWeek) {
		this.skuPvBeforePromOneWeek = skuPvBeforePromOneWeek;
	}
	public int getSkuUvBeforePromOneWeek() {
		return skuUvBeforePromOneWeek;
	}
	public void setSkuUvBeforePromOneWeek(int skuUvBeforePromOneWeek) {
		this.skuUvBeforePromOneWeek = skuUvBeforePromOneWeek;
	}
	public String getSkuUrl() {
		return skuUrl;
	}
	public void setSkuUrl(String skuUrl) {
		this.skuUrl = skuUrl;
	}
	public String getIntcmpModeId() {
		return intcmpModeId;
	}
	public void setIntcmpModeId(String intcmpModeId) {
		this.intcmpModeId = intcmpModeId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public boolean isAvailableSellingWarn() {
		return isAvailableSellingWarn;
	}
	public void setAvailableSellingWarn(boolean isAvailableSellingWarn) {
		this.isAvailableSellingWarn = isAvailableSellingWarn;
	}
	
	
}
