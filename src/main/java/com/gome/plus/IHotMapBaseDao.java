package com.gome.plus;

import java.util.List;

import com.gome.Controller.model.QueryParam;
import com.gome.dao.model.PageChannel;
import com.gome.dao.model.PageResource;
import com.gome.dao.model.PageView;

public interface IHotMapBaseDao {

	/**
	 * 查询全站的uv,pv,visits,bounces指标
	 * @param param
	 * @return
	 */
	public PageView queryAllStation(QueryParam param);
	
	/**
	 * 查询页面的汇总信息中的sum类指标值，不用按day分组
	 * @param param
	 * @return
	 */
	public PageView querySummaryPageOfSumItem(QueryParam param);
	
	/**
	 * 查询页面的汇总信息中的非sum类指标值，需要按day分组，然后sum
	 * @param param
	 * @return
	 */
	public PageView querySummaryPageOfNotSumItem(QueryParam param);
	
	/**
	 * 查询页面的参与订单量、参与销售额
	 * @param param
	 * @return
	 */
	public PageView querySummaryPageOfOrderItem(QueryParam param);
	
	/**
	 * 查询某个页面的点击量
	 * @param param
	 * @return
	 */
	public Long queryClickForPage(QueryParam param);
	
	/**
	 * 查询某个页面下所有资源位的sum类指标值
	 * @param param
	 * @return
	 */
	public List<PageResource> queryResourcesOfSumItem(QueryParam param);
	
	/**
	 * 查询某个页面下所有资源位的非sum类指标值
	 * @param parm
	 * @return
	 */
	public List<PageResource> queryResourcesOfNotSumItem(QueryParam parm);
	
	/**
	 * 查询全站订单量
	 * @param param
	 * @return
	 */
	public Long queryAllOrderCountOfAllStation(QueryParam param);
	
	/**
	 * 查询某个资源位的sum指标总计
	 * @param param
	 * @return
	 */
	public PageResource queryTotalSumItemOfIntcmp(QueryParam param);
	
	/**
	 * 查询某个资源位的非sum类指标，按天分组
	 * @param param
	 * @return
	 */
	public PageResource queryTotalNotSumItemOfIntcmp(QueryParam param);
	
	/**
	 * 查询某个资源位的历史记录，sum指标
	 * @param param
	 * @return
	 */
	public List<PageResource> queryHistoryOfSumItemForIntcmp(QueryParam param);
	
	/**
	 * 查询某个资源位的历史记录，非sum类指标，按天分组
	 * @param param
	 * @return
	 */
	public List<PageResource> queryHistoryOfNotSumItemForIntcmp(QueryParam param);
	
	//按小时的图表数据查询
	public List<PageView> queryPageFullItemOfHour(QueryParam param);
	
	public List<PageView> queryPageOrderItemOfHour(QueryParam param);
	
	public List<PageView> queryClickOfHour(QueryParam param);
	
	//按天的图表数据查询
	public List<PageView> queryPageFullItemOfDay(QueryParam param);
	
	public List<PageView> queryPageOrderItemOfDay(QueryParam param);
	
	public List<PageView> queryClickOfDay(QueryParam param);
	
	//按渠道的图表数据查询
	public List<PageChannel> queryPageSumItemOfChannel(QueryParam param);
	
	public List<PageChannel> queryPageNotSumItemOfChannel(QueryParam param);
	
	public List<PageChannel> queryPageOrderItemOfChannel(QueryParam param);
	
	public List<PageChannel> queryClickOfChannel(QueryParam param);
}
