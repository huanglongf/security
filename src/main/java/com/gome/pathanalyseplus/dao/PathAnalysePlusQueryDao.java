package com.gome.pathanalyseplus.dao;

import java.util.List;

import com.gome.Controller.model.QueryParam;
import com.gome.dao.model.PageView;
import com.gome.pathanalyse.model.PathAnalyseQueryParam;
import com.gome.pathanalyse.model.PathAnalyseQueryResult;

/**
 * 用于页面路径分析的mysql for mybatis查询
 * @author wangshubao
 *
 */
public interface PathAnalysePlusQueryDao {

	/**
	 * 查询上一页的topN
	 * @param param
	 * @return
	 */
	public List<PathAnalyseQueryResult> queryTopNEntries(PathAnalyseQueryParam param);
	
	/**
	 * 查询下一页的topN
	 */
	public List<PathAnalyseQueryResult> queryTopNFollows(PathAnalyseQueryParam param);
	
	/**
	 * 查询作为登录网站的指标查询
	 * @param param
	 * @return
	 */
	public PathAnalyseQueryResult queryDirectEntry(PathAnalyseQueryParam param);
	
	/**
	 * 查询作为退出网站的指标查询
	 * @param param
	 * @return
	 */
	public PathAnalyseQueryResult queryExit(PathAnalyseQueryParam param);
	
	/**
	 * 查询页面的汇总信息中的sum类指标值，不用按day分组
	 * @param param
	 * @return
	 */
	public PageView querySummaryOfSumItemForPagetype(PathAnalyseQueryParam param);
	
	/**
	 * 查询订单相关指标
	 * @param param
	 * @return
	 */
	public PageView querySummaryPageOfOrderItem(PathAnalyseQueryParam param);
	
	
	/**
	 * 查询全站的uv,pv,visits,bounces指标
	 * @param param
	 * @return
	 */
	public PageView queryAllStation(PathAnalyseQueryParam param);
	
	/**
	 * 查询网站类型的总pv、uv、visits
	 * @param param
	 * @return
	 */
	public PathAnalyseQueryResult queryPvUvVisitsOfReferrerPagetype(PathAnalyseQueryParam param);
	
}
