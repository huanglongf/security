package com.gome.service;

import java.util.List;

import com.gome.promsku.model.PromSkuAnalyseCityInfo;
import com.gome.promsku.model.PromSkuAnalyseQueryParam;
import com.gome.promsku.model.PromSkuAnalyseResponse;

/**
 * 活动页商品分析查询服务接口
 * @author wangshubao
 *
 */
public interface IPromSkuAnalyseQueryService {

	/**
	 * 查询活动页-商品的指标
	 * @param param
	 * @return
	 */
	public PromSkuAnalyseResponse queryPromSkuInfo(PromSkuAnalyseQueryParam param);

	/**
	 * 查询活动页的某个商品的城市相关指标
	 * @param param
	 * @return
	 */
	public List<PromSkuAnalyseCityInfo> queryPromSkuCityInfo(PromSkuAnalyseQueryParam param);
	
	/**
	 * 设置是否启用缓存
	 * @param isUseCache
	 */
	public void setUseCache(boolean isUseCache);
	
	/**
	 * 清缓存
	 */
	public void clearCache();
}
