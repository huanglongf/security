package com.gome.plus;

import java.util.List;

import com.gome.Controller.model.AppHotMapQueryParam;
import com.gome.dao.model.PageResource;
import com.gome.dao.model.PageView;

public interface IHotMapAppDao {

	/**
	 * 查询全站的uv,pv,visits,bounces指标
	 * @param param
	 * @return
	 */
	public PageView queryAllStation(AppHotMapQueryParam param);
	
	/**
	 * 查询页面的汇总信息中的sum类指标值，不用按day分组
	 * @param param
	 * @return
	 */
	public PageView querySummaryOfSy(AppHotMapQueryParam param);
	
	/**
	 * 查询某个页面的点击量
	 * @param param
	 * @return
	 */
	public Long queryClickForSy(AppHotMapQueryParam param);
	
	/**
	 * 查询某个页面下所有资源位的sum类指标值
	 * @param param
	 * @return
	 */
	public List<PageResource> queryResourcesOfSy(AppHotMapQueryParam param);
	
	/**
	 * 查询某个资源位的sum指标总计
	 * @param param
	 * @return
	 */
	public PageResource queryTotalItemOfIntcmp(AppHotMapQueryParam param);
	
	/**
	 * 查询某个资源位的历史记录，sum指标
	 * @param param
	 * @return
	 */
	public List<PageResource> queryHistoryOfIntcmp(AppHotMapQueryParam param);
	
	//按小时的图表数据查询
	public List<PageView> querySyFullItemOfHour(AppHotMapQueryParam param);
	
	public List<PageView> querySyClickOfHour(AppHotMapQueryParam param);
	
	//按天的图表数据查询
	public List<PageView> querySyFullItemOfDay(AppHotMapQueryParam param);
	
	public List<PageView> querySyClickOfDay(AppHotMapQueryParam param);
}
