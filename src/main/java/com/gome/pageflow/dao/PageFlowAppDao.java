package com.gome.pageflow.dao;

import java.util.List;
import java.util.Map;

import com.gome.pageflow.model.PageFlow;
import com.gome.pageflow.model.PageFlowQueryParams;

/**
 * 
 * @author chixiaoyong
 *
 */
public interface PageFlowAppDao {
	/**
	 * 
	 * @param params
	 * @return
	 */
	public List<String> queryPvTopN(PageFlowQueryParams params);
	
	/**
	 * 
	 * @param params
	 * @return
	 */
	public List<String> querypageTypePvTopN(PageFlowQueryParams params);


	/**
	 * 
	 * @param params
	 * @return
	 */
	public List<PageFlow> queryAllTopN(Map<String, Object> urlMap);

	
	// 查询和小时的
	public List<PageFlow> queryPage(Map<String, Object> urlMap);

	/**
	 * 查询全站
	 * 
	 * @param flowQueryParams
	 * @return
	 */
	public List<PageFlow> queryAllStation(PageFlowQueryParams flowQueryParams);

	/**
	 * 查询TopN 的总数
	 * 
	 * @param flowQueryParams
	 * @return
	 */
	public Long querytotalCountTopN(PageFlowQueryParams flowQueryParams);

	

	public List<PageFlow> queryAllStationGroup(PageFlowQueryParams flowQueryParams);

	/**
	 * 
	 * @param urlMap
	 * @return
	 */
	public Long querySearchCount(Map<String, Object> urlMap);

	/**
	 * 搜索的总数
	 * @param urlMap
	 * @return
	 */
	public List<PageFlow> querySearchPageTotal(Map<String, Object> urlMap);

	/**
	 * 搜索的曲线图指标
	 * @param urlMap
	 * @return
	 */
	public List<PageFlow> querySearchPageChart(Map<String, Object> urlMap);

	
	
	
	

}
