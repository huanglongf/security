package com.gome.service;

import java.util.Map;

import com.gome.Controller.model.AppHotMapQueryParam;
import com.gome.Controller.model.PageResponse;
import com.gome.Controller.model.ResourceResponse;

public interface IAppHotMapQueryService {

	/**
	 * 查询某个页面的汇总指标
	 * @param param
	 * @return
	 */
	public PageResponse querySummaryOfPage(AppHotMapQueryParam param);
	
	/**
	 * 查询某个页面的所有资源位指标
	 * @param param
	 * @return
	 */
	public Map<String, ResourceResponse> queryAllResourceForPage(AppHotMapQueryParam param, PageResponse pageSummary);
	
	/**
	 * 查询某个资源位的历史信息，返回map中包括：total总计和active明细
	 * @param param
	 * @return
	 */
	public Map<String, Object> queryResourceHistoryInfo(AppHotMapQueryParam param);
	
	/**
	 * 为图表查询每小时的汇总指标值
	 * @param param
	 * @return
	 */
	public Map<String, PageResponse> chartQueryOfHour(AppHotMapQueryParam param);
	
	/**
	 * 为图表查询每天的汇总指标值
	 * @param param
	 * @return
	 */
	public Map<String, PageResponse> chartQueryOfDay(AppHotMapQueryParam param);
	
}
