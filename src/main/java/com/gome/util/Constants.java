package com.gome.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 一些常用的常量
 * 
 * @author chixiaoyong
 *
 */
public class Constants {

	public final static String DATASOURCE_PC = "PC";
	public final static String DATASOURCE_WAP = "WAP";
	// 实时索引
	public final static String ACTUALTIME_INDEX = getIndex();

	// 聚合索引的名称
	public final static String OFFLINE_INDEX = "bigdata-session_test";

	// 热力图查询用到的指标
	public static final String ITEM_UV = "uv";
	public static final String ITEM_PV = "pv";
	public static final String ITEM_VISITOR = "visitor";
	public static final String ITEM_ORDER_COUNT = "orderCount";
	public static final String ITEM_CLICK = "click";
	public static final String ITEM_SALE_AMOUNT = "saleAmount";

	public static String getIndex() {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

		Calendar calendar = Calendar.getInstance();

		String index = null;
		try {

			index = "current-session" + dateFormat.format(calendar.getTime());
		} catch (Exception e) {

			e.printStackTrace();
		}

		return index;
    
	}

}
