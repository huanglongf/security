package com.gome.promsku.model;

/**
 * 五大城市对应的可卖数
 * @author wangshubao
 *
 */
public class PromSkuAnalyseCityAvailableselling {
	
	public static final String DAID_BJ = "DA11010500";
	public static final String DAID_SH = "DA21010100";
	public static final String DAID_GZ = "DA31010700";
	public static final String DAID_SZ = "DA31020200";
	public static final String DAID_CD = "DA71010500";

	private String daid;
	private int selling;
	
	public String getDaid() {
		return daid;
	}
	public void setDaid(String daid) {
		this.daid = daid;
	}
	public int getSelling() {
		return selling;
	}
	public void setSelling(int selling) {
		this.selling = selling;
	}
	
	public String getCityName(){
		if(daid==null)
			return null;
		
		switch(daid){
		case DAID_BJ:
			return "北京";
		case DAID_SH:
			return "上海";
		case DAID_GZ:
			return "广州";
		case DAID_SZ:
			return "深圳";
		case DAID_CD:
			return "成都";
			default :
				return null;
		}
	}
	
}
