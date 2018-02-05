package com.gome.service;

import java.util.Map;

import com.gome.Controller.model.ChannelResponse;
import com.gome.Controller.model.PageResponse;
import com.gome.Controller.model.QueryParam;
import com.gome.Controller.model.ResourceResponse;

/**
 * 离线热力图查询服务接口
 * @author wangshubao
 *
 */
public interface IOfflineHotMapQueryService {
	
	
	/**
	 * 查询某个页面的汇总指标
	 * @param param
	 * @return
	 */
	public PageResponse querySummaryOfPage(QueryParam param);
	
	/**
	 * 查询某个页面的所有资源位指标
	 * @param param
	 * @return
	 */
	public Map<String, ResourceResponse> queryAllResourceForPage(QueryParam param, PageResponse pageSummary);
	
	/**
	 * 查询某个资源位的历史信息，返回map中包括：total总计和active明细
	 * @param param
	 * @return
	 */
	public Map<String, Object> queryResourceHistoryInfo(QueryParam param);
	
	/**
	 * 为图表查询每小时的汇总指标值
	 * @param param
	 * @return
	 */
	public Map<String, PageResponse> chartQueryOfHour(QueryParam param);
	
	/**
	 * 为图表查询每天的汇总指标值
	 * @param param
	 * @return
	 */
	public Map<String, PageResponse> chartQueryOfDay(QueryParam param);
	
	/**
	 * 为图表查询每种渠道的汇总指标值
	 * @param param
	 * @return
	 */
	public Map<String, ChannelResponse> chartQueryOfChannel(QueryParam param);

}
