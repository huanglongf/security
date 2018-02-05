package com.gome.clickmap.service;

import com.gome.clickmap.model.QueryParam;

/**
 * 点击热力图接口
 * 
 * @author chixiaoyong
 *
 */  
public interface ClickMapService {

	/**
	 * 根据URL查询页面的点击量和最大的点击位置
	 * 
	 * @param url
	 * @return
	 */
	String  queryByUrl(QueryParam param);


}
