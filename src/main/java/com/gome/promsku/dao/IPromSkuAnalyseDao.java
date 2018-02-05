package com.gome.promsku.dao;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.gome.promsku.dao.model.PromSkuAnalyseDaoResult;
import com.gome.promsku.model.PromSkuAnalyseCityAvailableselling;
import com.gome.promsku.model.PromSkuAnalyseCityInfo;
import com.gome.promsku.model.PromSkuAnalyseQueryParam;

/**
 * 活动页商品分析kylin查询dao层
 * @author wangshubao
 *
 */
public interface IPromSkuAnalyseDao {

	/**
	 * 查询某活动页对应的商品指标
	 * @return
	 */
	public List<PromSkuAnalyseDaoResult> queryPromSkuBaseInfo(PromSkuAnalyseQueryParam param);
	
	/**
	 * 查询某个活动页对应的skuId的流量指标
	 * @param param
	 * @return
	 */
	public PromSkuAnalyseDaoResult queryPromSkuSessionInfo(PromSkuAnalyseQueryParam param);
	
	/**
	 * 查询商品对应的全站指标
	 * @return
	 */
	public List<PromSkuAnalyseDaoResult> queryAllStationSkuInfo(@Param("param") PromSkuAnalyseQueryParam param, @Param("skuIds") List<String> skuIds);
	
	/**
	 * 查询活动页的标题、活动起始时间
	 * @param param
	 * @return
	 */
	public PromSkuAnalyseDaoResult queryPromInfo(PromSkuAnalyseQueryParam param);
	
	/**
	 * 查询活动页-商品产生的订单相关指标
	 * @param param
	 * @return
	 */
	public PromSkuAnalyseDaoResult queryPromSkuOrderCount(PromSkuAnalyseQueryParam param);
	
	/**
	 * 查询活动页-商品产生的订单相关指标
	 * @param param
	 * @return
	 */
	public PromSkuAnalyseDaoResult queryPromSkuOrderAmountQuantity(PromSkuAnalyseQueryParam param);
	
	/**
	 * 查询五大城市对应的可卖数
	 * @param param
	 * @return
	 */
	public List<PromSkuAnalyseCityAvailableselling> queryCityAvailableselling(@Param("param") PromSkuAnalyseQueryParam param, @Param("daids") String[] daids);
	
	public List<PromSkuAnalyseCityInfo> queryPromSkuCityOrderCount(@Param("param") PromSkuAnalyseQueryParam param, @Param("cityNames") String[] cityNames);
	
	public List<PromSkuAnalyseCityInfo> queryPromSkuCityOrderQuantity(@Param("param") PromSkuAnalyseQueryParam param, @Param("cityNames") String[] cityNames);
	
	public List<PromSkuAnalyseCityInfo> queryPromSkuCityPv(@Param("param") PromSkuAnalyseQueryParam param, @Param("cityNames") String[] cityNames);
	
	/**
	 * 查询可卖数需要预警的sku
	 * @param param
	 * @param daids
	 * @param skuIds
	 * @return
	 */
	public List<String> queryAvailableSellingWarnSku(@Param("param") PromSkuAnalyseQueryParam param, @Param("daids") String[] daids, @Param("skuIds")List<String> skuIds, @Param("warnThreshold")int warnThreshold);
	
	public List<String> queryAvailableSellingWarnSkuWithZero(@Param("param") PromSkuAnalyseQueryParam param, @Param("daids") String[] daids, @Param("skuIds")List<String> skuIds);
	
	/**
	 * 查询某活动页中某skuid对应的modeId，productId
	 * @param param
	 * @return
	 */
	public List<PromSkuAnalyseDaoResult> queryModelIdProductId(PromSkuAnalyseQueryParam param);
	
	/**
	 * 查询活动页-商品产生的订单相关指标,批量查询方法
	 * @param param
	 * @return
	 */
	public List<PromSkuAnalyseDaoResult> queryPromSkuOrderCountBatch(@Param("param") PromSkuAnalyseQueryParam param ,@Param("skuIds") List<String> skuIds);
	
	/**
	 * 查询活动页-商品产生的订单相关指标,批量查询方法
	 * @param param
	 * @return
	 */
	public List<PromSkuAnalyseDaoResult> queryPromSkuOrderAmountQuantityBatch(@Param("param") PromSkuAnalyseQueryParam param ,@Param("skuIds") List<String> skuIds);
}
