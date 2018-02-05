package com.gome.clickmap.dao;

import java.util.List;

import com.gome.clickmap.model.ClickMapModel;
import com.gome.clickmap.model.QueryParam;

/**
 * 点击热力图接口
 * 
 * @author chixiaoyong
 *
 */
public interface ClickMapDao {

	/**
	 * 根据URL查询页面的点击量
	 * 
	 * @param url
	 * @return
	 */
	List<ClickMapModel> queryByUrl(QueryParam param);

	/**
	 * 查询最大点击
	 * 
	 * @param url
	 * @return
	 */
	Double queryMaxClick(QueryParam param);
	
	
	Double queryCount(QueryParam param);
	
}
   