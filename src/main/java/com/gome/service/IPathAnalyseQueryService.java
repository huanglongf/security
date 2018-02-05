package com.gome.service;

import java.util.List;
import java.util.Map;

import com.gome.Controller.model.PageResponse;
import com.gome.pathanalyse.model.PathAnalyseQueryParam;
import com.gome.pathanalyse.model.PathAnalyseQueryResult;

/**
 * 页面路径分析查询服务接口
 * @author wangshubao
 *
 */
public interface IPathAnalyseQueryService {

	/**
	 * 查询直接登录的指标值
	 * @param param
	 * @return
	 */
	public PathAnalyseQueryResult queryDirectEntry(PathAnalyseQueryParam param);
	
	/**
	 * 查询直接退出的指标值
	 * @param param
	 * @return
	 */
	public PathAnalyseQueryResult queryDirectExit(PathAnalyseQueryParam param);
	
	/**
	 * 查询上一页topN
	 * @param param
	 * @return
	 */
	public List<PathAnalyseQueryResult> queryTopNEntries(PathAnalyseQueryParam param);
	
	/**
	 * 查询下一页topN
	 * @param param
	 * @return
	 */
	public List<PathAnalyseQueryResult> queryTopNFollows(PathAnalyseQueryParam param);
	
	/**
	 * 查询网站类型汇总指标信息
	 * @param param
	 * @return
	 */
	public PageResponse querySummaryOfPagetype(PathAnalyseQueryParam param);
	
	public int queryPVOfReferrerPagetype(PathAnalyseQueryParam param);
	/**
	 * 查询plus数据源页面路径信息
	 * @param queryParam
	 * @return
	 */
	public Map<String, Object> queryPathAnalyseData(
			PathAnalyseQueryParam queryParam);
	/**
	 * 上一页plus的topN
	 * @param queryParam
	 * @return
	 */
	public Map<String, Object> queryTopNEntriesData(
			PathAnalyseQueryParam queryParam);
	/**
	 * 下一页plus的topN
	 * @param queryParam
	 * @return
	 */
	public Map<String, Object> queryTopNFollowsData(
			PathAnalyseQueryParam queryParam);
	
}
