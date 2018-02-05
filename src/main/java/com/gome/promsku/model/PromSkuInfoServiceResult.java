package com.gome.promsku.model;

import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_click;
import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_clickDaily;
import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_clickPercent;
import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_clickRate;
import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_ipv;
import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_ipvDailyBeforeOneWeek;
import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_orderAmount;
import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_orderAmountDaily;
import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_orderCount;
import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_orderCountDaily;
import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_orderQuantity;
import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_orderQuantityDaily;
import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_orderQuantityDailyBeforeOneWeek;
import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_orderTransferRate;
import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_orderTransferRateDailyBeforeOneWeek;
import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_skuBrandName;
import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_skuCategory;
import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_skuDuration;
import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_skuEndDate;
import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_skuId;
import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_skuName;
import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_skuStartDate;
import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_productId;
import static com.gome.promsku.model.PromSkuAnalyseConstants.ORDER_COLUMN_intcmpModeId;
import java.math.BigDecimal;
import java.math.RoundingMode;
/**
 * 活动页-商品分析服务端查询的某个sku的指标结果
 * @author wangshubao
 *
 */
public class PromSkuInfoServiceResult {

	private int rowNo;
	private String skuId;
	private String skuName;
	private String skuCategory;
	private String skuBrandName;
	private int ipv;
	private int skuDuration;
	private String skuStartDate;
	private String skuEndDate;
	private String skuUrl;
	private int click;
	private Float clickRate;
	private Float clickPercent;
	private Integer clickDaily;
	private Integer orderCountDaily;
	private Double orderAmountDaily;
	private Integer orderQuantityDaily;
	private int orderCount;
	private double orderAmount;
	private int orderQuantity;
	private Float orderTransferRate;
	private Integer ipvDailyBeforeOneWeek;
	private Integer orderQuantityDailyBeforeOneWeek;
	private Float orderTransferRateDailyBeforeOneWeek;
	private String saleableQuantity = "0";		//0表示不预警，默认值；1表示预警
	private String intcmpModeId;
	private String productId;
	
	
	public int getRowNo() {
		return rowNo;
	}
	public void setRowNo(int rowNo) {
		this.rowNo = rowNo;
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
	public String getSkuCategory() {
		return skuCategory;
	}
	public void setSkuCategory(String skuCategory) {
		this.skuCategory = skuCategory;
	}
	public String getSkuBrandName() {
		return skuBrandName;
	}
	public void setSkuBrandName(String skuBrandName) {
		this.skuBrandName = skuBrandName;
	}
	public int getIpv() {
		return ipv;
	}
	public void setIpv(int ipv) {
		this.ipv = ipv;
	}
	public int getSkuDuration() {
		return skuDuration;
	}
	public void setSkuDuration(int skuDuration) {
		this.skuDuration = skuDuration;
	}
	public int getClick() {
		return click;
	}
	public void setClick(int click) {
		this.click = click;
	}
	public int getClickDaily() {
		return clickDaily;
	}
	public void setClickDaily(int clickDaily) {
		this.clickDaily = clickDaily;
	}
	
	public Integer getOrderCountDaily() {
		return orderCountDaily;
	}
	public void setOrderCountDaily(Integer orderCountDaily) {
		this.orderCountDaily = orderCountDaily;
	}
	public Integer getOrderQuantityDaily() {
		return orderQuantityDaily;
	}
	public void setOrderQuantityDaily(Integer orderQuantityDaily) {
		this.orderQuantityDaily = orderQuantityDaily;
	}
	public int getOrderCount() {
		return orderCount;
	}
	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}
	public double getOrderAmount() {
		return orderAmount;
	}
	public void setOrderAmount(double orderAmount) {
		this.orderAmount = orderAmount;
	}
	public int getOrderQuantity() {
		return orderQuantity;
	}
	public void setOrderQuantity(int orderQuantity) {
		this.orderQuantity = orderQuantity;
	}
	public int getIpvDailyBeforeOneWeek() {
		return ipvDailyBeforeOneWeek;
	}
	public Integer getOrderQuantityDailyBeforeOneWeek() {
		return orderQuantityDailyBeforeOneWeek;
	}
	public void setOrderQuantityDailyBeforeOneWeek(Integer orderQuantityDailyBeforeOneWeek) {
		this.orderQuantityDailyBeforeOneWeek = orderQuantityDailyBeforeOneWeek;
	}
	public void setIpvDailyBeforeOneWeek(Integer ipvDailyBeforeOneWeek) {
		this.ipvDailyBeforeOneWeek = ipvDailyBeforeOneWeek;
	}
	public Float getOrderTransferRateDailyBeforeOneWeek() {
		return orderTransferRateDailyBeforeOneWeek;
	}
	public void setOrderTransferRateDailyBeforeOneWeek(Float orderTransferRateDailyBeforeOneWeek) {
		this.orderTransferRateDailyBeforeOneWeek = orderTransferRateDailyBeforeOneWeek;
	}
	public void setOrderAmountDaily(Double orderAmountDaily) {
		this.orderAmountDaily = orderAmountDaily;
	}
	public Double getOrderAmountDaily() {
		return orderAmountDaily;
	}
	public Float getClickRate() {
		return clickRate;
	}
	public void setClickRate(Float clickRate) {
		this.clickRate = clickRate;
	}
	public Float getClickPercent() {
		return clickPercent;
	}
	public void setClickPercent(Float clickPercent) {
		this.clickPercent = clickPercent;
	}
	public Float getOrderTransferRate() {
		return orderTransferRate;
	}
	public void setOrderTransferRate(Float orderTransferRate) {
		this.orderTransferRate = orderTransferRate;
	}
	public void setClickDaily(Integer clickDaily) {
		this.clickDaily = clickDaily;
	}
	public void calClickRate(int skuClick, int pageClick){
		if(pageClick==0)
			return;
		
		this.clickRate = ((float)skuClick)/((float)pageClick) * 100;
	}
	
	public void calClickPercent(int skuClick, int pagePv){
		if(pagePv==0)
			return;
		
		this.clickPercent = ((float)skuClick)/((float)pagePv)*100;
	}
	
	public void calOrderCountDaily(int orderCount, int day){
		if(day!=0){
			double value = (double)orderCount / (double)day;
			this.orderCountDaily = new BigDecimal(value).setScale(0, RoundingMode.HALF_UP).intValue();
		}
	}
	
	public void calOrderAmountDaily(double orderAmount, int day){
		if(day!=0)
			this.orderAmountDaily = orderAmount / day;
	}
	
	public void calOrderQuantityDaily(int orderQuantity, int day){
		if(day!=0){
			double value = (double)orderQuantity / (double)day;
			this.orderQuantityDaily = new BigDecimal(value).setScale(0, RoundingMode.HALF_UP).intValue();
		}
	}
	
	public void calOrderTransferRate(int orderCount, int skuUv){
		if(skuUv==0)
			return;
		
		this.orderTransferRate = ((float)orderCount) / ((float)skuUv) * 100;
	}
	
	public void calIpvDailyBeforeOneWeek(int ipvDailyBeforeOneWeek){
		double value = ipvDailyBeforeOneWeek / 7.0;
		this.ipvDailyBeforeOneWeek = new BigDecimal(value).setScale(0, RoundingMode.HALF_UP).intValue();
	}
	
	public void calOrderQuantityDailyBeforeOneWeek(int orderQuantityDailyBeforeOneWeek){
		double value = orderQuantityDailyBeforeOneWeek / 7.0;
		this.orderQuantityDailyBeforeOneWeek = new BigDecimal(value).setScale(0, RoundingMode.HALF_UP).intValue();
	}
	
	public void calOrderTransferRateDailyBeforeOneWeek(int orderCount, int uv){
		if(uv==0)
			return;
		
		this.orderTransferRateDailyBeforeOneWeek = ((float)orderCount)/((float)uv) * 100;
	}
	
	public void calClickDaily(int click, int days){
		if(days==0)
			return;
		
		double value = (double)click / (double)days;
		this.clickDaily = new BigDecimal(value).setScale(0, RoundingMode.HALF_UP).intValue();
	}
	
	public String getSkuStartDate() {
		return skuStartDate;
	}
	public void setSkuStartDate(String skuStartDate) {
		this.skuStartDate = skuStartDate;
	}
	public String getSkuEndDate() {
		return skuEndDate;
	}
	public void setSkuEndDate(String skuEndDate) {
		this.skuEndDate = skuEndDate;
	}
	public String getSaleableQuantity() {
		return saleableQuantity;
	}
	public void setSaleableQuantity(String saleableQuantity) {
		this.saleableQuantity = saleableQuantity;
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
	public Object getOrderColumnValue(String orderColumn){
		switch(orderColumn){
		case ORDER_COLUMN_skuId:
			return this.getSkuId();
		case ORDER_COLUMN_skuName:
			return this.getSkuName();
		case ORDER_COLUMN_skuCategory:
			return this.getSkuCategory();
		case ORDER_COLUMN_skuBrandName:
			return this.getSkuBrandName();
		case ORDER_COLUMN_ipv:
			return this.getIpv();
		case ORDER_COLUMN_skuDuration:
			return this.getSkuDuration();
		case ORDER_COLUMN_click:
			return this.getClick();
		case ORDER_COLUMN_clickRate:
			return  this.getClickRate();
		case ORDER_COLUMN_clickPercent:
			return  this.getClickPercent();
		case ORDER_COLUMN_clickDaily:
			return this.getClickDaily();
		case ORDER_COLUMN_orderCountDaily:
			return this.getOrderCountDaily();
		case ORDER_COLUMN_orderAmountDaily:
			return this.getOrderAmountDaily();
		case ORDER_COLUMN_orderQuantityDaily:
			return this.getOrderQuantityDaily();
		case ORDER_COLUMN_orderCount:
			return this.getOrderCount();
		case ORDER_COLUMN_orderAmount:
			return this.getOrderAmount();
		case ORDER_COLUMN_orderQuantity:
			return this.getOrderQuantity();
		case ORDER_COLUMN_orderTransferRate:
			return this.getOrderTransferRate();
		case ORDER_COLUMN_ipvDailyBeforeOneWeek:
			return this.getIpvDailyBeforeOneWeek();
		case ORDER_COLUMN_orderQuantityDailyBeforeOneWeek:
			return this.getOrderQuantityDailyBeforeOneWeek();
		case ORDER_COLUMN_orderTransferRateDailyBeforeOneWeek:
			return this.getOrderTransferRateDailyBeforeOneWeek();
		case ORDER_COLUMN_skuStartDate:
			return this.getSkuStartDate();
		case ORDER_COLUMN_skuEndDate:
			return this.getSkuEndDate();
		case ORDER_COLUMN_productId:
			return this.getProductId();
		case ORDER_COLUMN_intcmpModeId:
			return this.getIntcmpModeId();
		default :
			return null;
		}
	}
}
