package com.gome.service.impl;

import static com.gome.promsku.model.PromSkuAnalyseCityAvailableselling.DAID_BJ;
import static com.gome.promsku.model.PromSkuAnalyseCityAvailableselling.DAID_CD;
import static com.gome.promsku.model.PromSkuAnalyseCityAvailableselling.DAID_GZ;
import static com.gome.promsku.model.PromSkuAnalyseCityAvailableselling.DAID_SH;
import static com.gome.promsku.model.PromSkuAnalyseCityAvailableselling.DAID_SZ;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.gome.dao.IKylinQueryDao;
import com.gome.dao.model.PageView;
import com.gome.promsku.dao.IPromSkuAnalyseDao;
import com.gome.promsku.dao.model.PromSkuAnalyseDaoResult;
import com.gome.promsku.model.PromSkuAnalyseCityAvailableselling;
import com.gome.promsku.model.PromSkuAnalyseCityInfo;
import com.gome.promsku.model.PromSkuAnalyseConstants;
import com.gome.promsku.model.PromSkuAnalyseQueryParam;
import com.gome.promsku.model.PromSkuAnalyseResponse;
import com.gome.promsku.model.PromSkuInfoServiceResult;
import com.gome.service.IPromSkuAnalyseQueryService;

import redis.GcacheClient;
import redis.gcache.KeysExecutor;

/**
 * 活动页商品分析查询服务实现类
 * @author wangshubao
 *
 */
@Service
public class PromSkuAnalyseQueryServiceImpl implements IPromSkuAnalyseQueryService {
	
	private static final Logger logger = LoggerFactory.getLogger(PromSkuAnalyseQueryServiceImpl.class);
	
	private static final int QUERY_COUNT_ONE_QUERY = 50;
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final DateFormat df1 = new SimpleDateFormat("yyyyMMdd");
	private static final DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
	
	private static final String[] cityNames10 = new String[]{"北京","上海","广州","深圳","成都","重庆","西安","南京","天津","武汉"};
	private static final String[] cityNames5Daids = new String[]{DAID_BJ, DAID_SH, DAID_GZ, DAID_SZ, DAID_CD};
	private static final String[] cityNames5 = new String[]{"北京","上海","广州","深圳","成都"};
	
	private static final String CACHE_KEY_PREFIX = "bigdata_hotmap_prom_sku:";
	private static final int CACHE_EXPIRE_TIME = 12*60*60;
	
	private static final String DEFAULT_ORDER_COLUMN = "skuCategory";
	private static final String DEFAULT_ORDER_TYPE = "desc";
	
	private static final int AVAILABLESELLING_WARN_THRESHOLD = 5;	//可卖数预警阈值
	private static final String SPLIT_STR = "&";
	
	@Autowired
	IPromSkuAnalyseDao dao;
	@Autowired
	GcacheClient gcache;
	@Autowired
	IKylinQueryDao hotMapQueryDao;
	
	private volatile boolean isUseCache = true;
	

	@Override
	public PromSkuAnalyseResponse queryPromSkuInfo(PromSkuAnalyseQueryParam param) {
		PromSkuAnalyseResponse fullResponse = queryPromSkuInfo0(param);
		List<PromSkuInfoServiceResult> skuInfos = fullResponse.getSkuInfos();
		
		//根据skuId模糊匹配
		if(CollectionUtils.isNotEmpty(skuInfos) && param.getSkuId()!=null){
			skuInfos = filterSku(skuInfos, param.getSkuId());
		}
		
		fullResponse.setAmount(skuInfos==null? 0 : skuInfos.size());
		
		if(CollectionUtils.isNotEmpty(skuInfos)){
			if(param.getOrderColumn() != null){
				skuInfos = orderSkuInfo(skuInfos, param.getOrderColumn(), param.getOrderType());
			}else{
				skuInfos = orderSkuInfo(skuInfos, DEFAULT_ORDER_COLUMN, DEFAULT_ORDER_TYPE);
			}
		}
		
		//查询出分页数据
		if(CollectionUtils.isNotEmpty(skuInfos)){
			skuInfos = getPageData(skuInfos, param);
		}
		
		fullResponse.setSkuInfos(skuInfos);
		
		return fullResponse;
	}

	//获取分页的数据
	private List<PromSkuInfoServiceResult> getPageData(List<PromSkuInfoServiceResult> skuInfos, PromSkuAnalyseQueryParam param) {
		int start = param.getStart();
		int length = param.getLength();
		int end = start+length;
		
		int size = skuInfos.size();
		
		if(start>=size)
			return null;
		
		List<PromSkuInfoServiceResult> list = skuInfos.subList(start, end>size? size : end);
		int rowNo = start+1;
		for(PromSkuInfoServiceResult item : list){
			item.setRowNo(rowNo);
			rowNo++;
		}
		
		return list;
	}

	/**
	 * 先从缓存里查，缓存里查不出来，从kylin里查
	 * @param param
	 * @return
	 */
	private PromSkuAnalyseResponse queryPromSkuInfo0(PromSkuAnalyseQueryParam param){
		PromSkuAnalyseResponse fullResponse = isUseCache()? queryPromSkuInfoFromCache(param) : null;
		if(fullResponse==null){
			fullResponse = queryPromSkuInfoFromKylin(param);
			if(fullResponse != null && isUseCache()){
				try {
					cachePromSkuInfo(param, fullResponse);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		
		return fullResponse;
	}


	/**
	 * 缓存查询结果
	 * @param param
	 * @param fullResponse
	 */
	private void cachePromSkuInfo(PromSkuAnalyseQueryParam param, PromSkuAnalyseResponse fullResponse) {
		String key = buildCacheKey(param);
		gcache.setex(key, CACHE_EXPIRE_TIME, JSON.toJSONString(fullResponse));
		
	}

	/**
	 * 从缓存里获取查询结果
	 * @param param
	 * @return
	 */
	private PromSkuAnalyseResponse queryPromSkuInfoFromCache(PromSkuAnalyseQueryParam param) {
		
		String key = buildCacheKey(param);
		String value = gcache.get(key);
		if(StringUtils.isEmpty(value))
			return null;
		else{
			return JSON.parseObject(value, PromSkuAnalyseResponse.class);
		}
	}
	

	private String buildCacheKey(PromSkuAnalyseQueryParam param) {
		StringBuilder builder = new StringBuilder(CACHE_KEY_PREFIX);
		builder.append(param.getDatasource()).append("&");
		builder.append(param.getStartDate()).append("&");
		builder.append(param.getEndDate()).append("&");
		builder.append(param.getPromUrl());
		
		return builder.toString();
	}

	/**
	 * 根据skuId过滤
	 * @param skuInfos
	 * @param skuIdCondition
	 * @return
	 */
	private List<PromSkuInfoServiceResult> filterSku(List<PromSkuInfoServiceResult> skuInfos, String skuIdCondition) {
		List<PromSkuInfoServiceResult> filteredList = new LinkedList<PromSkuInfoServiceResult>();
		
		String[] skuQueryIds = skuIdCondition.split(",");
		for(PromSkuInfoServiceResult item : skuInfos){
			for(String skuQueryId : skuQueryIds){
				if(item.getSkuId().contains(skuQueryId)){
					filteredList.add(item);
					break;
				}
			}
		}
		return filteredList;
	}

	
	/**
	 * 根据指标值排序
	 * @param skuInfos
	 * @param orderColumn
	 * @param orderType
	 * @return
	 */
	private List<PromSkuInfoServiceResult> orderSkuInfo(List<PromSkuInfoServiceResult> skuInfos, String orderColumn, String orderType) {
		Collections.sort(skuInfos, new PromSkuComparator(orderColumn, orderType));
		return skuInfos;
	}


	public PromSkuAnalyseResponse queryPromSkuInfoFromKylin(PromSkuAnalyseQueryParam queryParam) {
		
		PromSkuAnalyseDaoResult promInfo = dao.queryPromInfo(queryParam);
		List<PromSkuAnalyseDaoResult> promSkuBaseInfoList = dao.queryPromSkuBaseInfo(queryParam);

		//sku去重
		promSkuBaseInfoList = removeRepeatSku(promSkuBaseInfoList);
		
		//查询活动页对应的点击量和pv
		if(promInfo!=null)
			queryPromClickAndPv(queryParam, promInfo);
		
		if(CollectionUtils.isNotEmpty(promSkuBaseInfoList)){

			//构建新查询参数
			PromSkuAnalyseQueryParam newParam = new PromSkuAnalyseQueryParam();
			newParam.setPromUrl(queryParam.getPromUrl());
			newParam.setDatasource(queryParam.getDatasource());
			
			Map<String, List<String>> skuIdMap = new HashMap<String, List<String>>();
			
			for(PromSkuAnalyseDaoResult skuBaseInfo : promSkuBaseInfoList){
				
				//---------------构建查询条件-----------------------
				String skuStartDate = skuBaseInfo.getSkuStartDate();
				String skuEndDate = skuBaseInfo.getSkuEndDate();
				
					//如果sku的上架时间大于查询起始时间，则用上架时间；否则用查询起始时间
				if(skuStartDate!=null && skuStartDate.compareTo(queryParam.getStartDate())>0){
					newParam.setStartDate(skuStartDate);
				}else{
					newParam.setStartDate(queryParam.getStartDate());
				}
				
					//如果sku的下架时间小于查询结束时间，则用下架时间；否则用查询结束时间
				if(skuEndDate!=null && skuEndDate.compareTo(queryParam.getEndDate())<0){
					newParam.setEndDate(skuEndDate);
				}else{
					newParam.setEndDate(queryParam.getEndDate());
				}
				
				skuBaseInfo.setSkuStartDate(newParam.getStartDate());
				skuBaseInfo.setSkuEndDate(newParam.getEndDate());
				
				skuBaseInfo.setPromClick(promInfo.getPromClick());
				skuBaseInfo.setPromPv(promInfo.getPromPv());
				
					//设置skuid
				newParam.setSkuId(skuBaseInfo.getSkuId());
				//--------------------构建查询条件结束----------------
				
				//查询sku的流量相关的指标
				PromSkuAnalyseDaoResult skuSessionInfo = dao.queryPromSkuSessionInfo(newParam);
				if(skuSessionInfo != null){
					skuBaseInfo.setSkuUv(skuSessionInfo.getSkuUv());
					skuBaseInfo.setSkuClick(skuSessionInfo.getSkuClick());
//					skuBaseInfo.setPromClick(skuSessionInfo.getPromClick());
//					skuBaseInfo.setPromPv(skuSessionInfo.getPromPv());
				}else{
					skuBaseInfo.setSkuUv(0);
					skuBaseInfo.setSkuClick(0);
//					skuBaseInfo.setPromClick(0);
//					skuBaseInfo.setPromPv(0);
				}
				
				String key = newParam.getStartDate() + SPLIT_STR + newParam.getEndDate();
				List<String> set = skuIdMap.get(key);
				if(set==null){
					set = new LinkedList<String>();
					skuIdMap.put(key, set);
				}
				set.add(skuBaseInfo.getSkuId());

	//该段代码被注释掉，用批量查询的方式提高效率
//				//查询sku的订单相关指标
//				PromSkuAnalyseDaoResult orderCountResult = dao.queryPromSkuOrderCount(newParam);
//				PromSkuAnalyseDaoResult orderAMResult = dao.queryPromSkuOrderAmountQuantity(newParam);
//				if(orderCountResult!=null){
//					skuBaseInfo.setOrderCount(orderCountResult.getOrderCount());
//				}else{
//					skuBaseInfo.setOrderCount(0);
//				}
//				if(orderAMResult != null){
//					skuBaseInfo.setOrderAmount(orderAMResult.getOrderAmount());
//					skuBaseInfo.setOrderQuantity(orderAMResult.getOrderQuantity());
//				}else{
//					skuBaseInfo.setOrderAmount(0);
//					skuBaseInfo.setOrderQuantity(0);
//				}
				
				//查询对应的modeId，productId
				List<PromSkuAnalyseDaoResult> modeProduct = dao.queryModelIdProductId(newParam);
				if(CollectionUtils.isNotEmpty(modeProduct)){
					if(modeProduct.size()==1){
						skuBaseInfo.setIntcmpModeId(modeProduct.get(0).getIntcmpModeId());
						skuBaseInfo.setProductId(modeProduct.get(0).getProductId());
					}else{
						Set<String> modelSet = new HashSet<String>(modeProduct.size(), 1.0f);
						Set<String> productSet = new HashSet<String>(modeProduct.size(), 1.0f);
						
						for(PromSkuAnalyseDaoResult item : modeProduct){
							if(StringUtils.isNotEmpty(item.getIntcmpModeId()))
								modelSet.add(item.getIntcmpModeId());
							if(StringUtils.isNotEmpty(item.getProductId()))
								productSet.add(item.getProductId());
						}
						
						skuBaseInfo.setIntcmpModeId(StringUtils.join(modelSet, ","));
						skuBaseInfo.setProductId(StringUtils.join(productSet, ","));
					}
				}
			
			}

			//批量查询订单类指标
			Map<String, PromSkuAnalyseDaoResult> skuOrderInfoMap = queryOrderInfoForSkuBatch(skuIdMap, newParam);
			for(PromSkuAnalyseDaoResult item : promSkuBaseInfoList){
				String key = item.getSkuId() + SPLIT_STR + item.getSkuStartDate() + SPLIT_STR + item.getSkuEndDate();
				PromSkuAnalyseDaoResult orderInfo = skuOrderInfoMap.get(key);
				if(orderInfo!=null){
					item.setOrderCount(orderInfo.getOrderCount());
					item.setOrderAmount(orderInfo.getOrderAmount());
					item.setOrderQuantity(orderInfo.getOrderQuantity());
				}else{
					item.setOrderCount(0);
					item.setOrderAmount(0);
					item.setOrderQuantity(0);
				}
			}
			
			//批量查询可卖数是否预警
			availableCellingWarn(queryParam, promSkuBaseInfoList);

			//查询活动前一周sku的指标
			if(promInfo!=null && promInfo.getPageStartDate()!=null){
				
				List<String> skuIds = new ArrayList<String>(promSkuBaseInfoList.size());
				for(PromSkuAnalyseDaoResult item : promSkuBaseInfoList){
					skuIds.add(item.getSkuId());
				}
				
				String pageStartDate = promInfo.getPageStartDate();
				try{
					Date pageStartDateTime = df.parse(pageStartDate);
					Date startDate = DateUtils.addDays(pageStartDateTime, -7);
					Date endDate = DateUtils.addDays(pageStartDateTime, -1);
					
					//设置查询活动页前一周的sku指标
					newParam.setStartDate(df1.format(startDate));
					newParam.setEndDate(df1.format(endDate));
					
					Map<String, PromSkuAnalyseDaoResult> skuInfoBeforeOneWeekMap = querySkuAllStationInfos(skuIds, newParam);
					for(PromSkuAnalyseDaoResult item : promSkuBaseInfoList){
						PromSkuAnalyseDaoResult skuInfo = skuInfoBeforeOneWeekMap.get(item.getSkuId());
						
						if(skuInfo!=null){
							item.setOrderCountBeforePromOneWeek(skuInfo.getOrderCount());
							item.setOrderQuantityBeforePromOneWeek(skuInfo.getOrderQuantity());
							item.setSkuPvBeforePromOneWeek(skuInfo.getSkuAllPv());
							item.setSkuUvBeforePromOneWeek(skuInfo.getSkuAllUv());
						}
					}
				}catch(Exception ex){
					logger.error(ex.getMessage(), ex);
				}
				
			}
		}
		
		return buildResponse(promInfo, promSkuBaseInfoList, queryParam);
	}
	
	/**
	 * 增加可卖数报警
	 * @param skuInfos
	 * @return
	 */
	private void availableCellingWarn(PromSkuAnalyseQueryParam param, List<PromSkuAnalyseDaoResult> skuInfos) {
		List<String> skuIds = new LinkedList<String>();
		for(PromSkuAnalyseDaoResult item : skuInfos){
			skuIds.add(item.getSkuId());
		}
		
		Set<String> set = new HashSet<String>();
		
		//分批查询，一批查询50个skuId
		int skuIdSize = skuIds.size();
		int queryCount = skuIdSize%QUERY_COUNT_ONE_QUERY==0? skuIdSize/QUERY_COUNT_ONE_QUERY : skuIdSize/QUERY_COUNT_ONE_QUERY+1;
		
		for(int i=0;i<queryCount;i++){
			int fromIndex = i*QUERY_COUNT_ONE_QUERY;
			int toIndex = (i+1)*QUERY_COUNT_ONE_QUERY;
			toIndex = toIndex>skuIdSize? skuIdSize : toIndex;
			List<String> subSkuIds = skuIds.subList(fromIndex, toIndex);
		
			List<String> warnSkuIds = dao.queryAvailableSellingWarnSku(param, cityNames5Daids, subSkuIds, AVAILABLESELLING_WARN_THRESHOLD);
			if(CollectionUtils.isNotEmpty(warnSkuIds)){
				set.addAll(warnSkuIds);
			}
			
			List<String> warnSkuIdsWithZero = dao.queryAvailableSellingWarnSkuWithZero(param, cityNames5Daids, subSkuIds);
			if(CollectionUtils.isNotEmpty(warnSkuIdsWithZero)){
				set.addAll(warnSkuIdsWithZero);
			}
		}
		
		if(CollectionUtils.isEmpty(set))
			return;
		
		for(PromSkuAnalyseDaoResult item : skuInfos){
			if(set.contains(item.getSkuId())){
				item.setAvailableSellingWarn(true);
			}
		}
	}

	/**
	 * sku的订单指标批量查询方法
	 * @param promSkuBaseInfoList
	 * @param skuIdMap
	 * @param newParam
	 * @return
	 */
	private Map<String, PromSkuAnalyseDaoResult> queryOrderInfoForSkuBatch(Map<String, List<String>> skuIdMap, PromSkuAnalyseQueryParam newParam) {
		//key为skuId+skuStartDate+skuEndDate， value为包含orderCount，orderAmount，orderQuantity的指标
		Map<String, PromSkuAnalyseDaoResult> skuOrderInfoMap = new HashMap<String, PromSkuAnalyseDaoResult>();
		
		//相同的查询日期进行批量查询，提高查询效率；批量查询里每次查询50个sku
		for(Map.Entry<String, List<String>> entry : skuIdMap.entrySet()){
			String dateInfo = entry.getKey();
			String[] splits = dateInfo.split(SPLIT_STR);
			String startDate = splits[0];
			String endDate = splits[1];
			
			newParam.setStartDate(startDate);
			newParam.setEndDate(endDate);
			
			List<String> skuIds = entry.getValue();
			
			int skuIdSize = skuIds.size();
			int queryCount = skuIdSize%QUERY_COUNT_ONE_QUERY==0? skuIdSize/QUERY_COUNT_ONE_QUERY : skuIdSize/QUERY_COUNT_ONE_QUERY+1;
			
			for(int i=0;i<queryCount;i++){
				int fromIndex = i*QUERY_COUNT_ONE_QUERY;
				int toIndex = (i+1)*QUERY_COUNT_ONE_QUERY;
				toIndex = toIndex>skuIdSize? skuIdSize : toIndex;
				List<String> subSkuIds = skuIds.subList(fromIndex, toIndex);
				
				//查询orderCount
				List<PromSkuAnalyseDaoResult> orderCountQueryResult = dao.queryPromSkuOrderCountBatch(newParam, subSkuIds);
				if(CollectionUtils.isNotEmpty(orderCountQueryResult)){
					//将orderCount，orderAmount，orderQuantity放在一起，存在map中
					for(PromSkuAnalyseDaoResult item : orderCountQueryResult){
						String key = item.getSkuId() + SPLIT_STR + dateInfo;
						
						PromSkuAnalyseDaoResult skuInfo = skuOrderInfoMap.get(key);
						if(skuInfo==null){
							skuOrderInfoMap.put(key, item);
						}else{
							skuInfo.setOrderCount(item.getOrderCount());
						}
					}
				}
				
				//查询orderAmount，orderQuantity
				List<PromSkuAnalyseDaoResult> orderAQQueryResult = dao.queryPromSkuOrderAmountQuantityBatch(newParam, subSkuIds);
				if(CollectionUtils.isNotEmpty(orderAQQueryResult)){
					for(PromSkuAnalyseDaoResult item : orderAQQueryResult){
						String key = item.getSkuId() + SPLIT_STR + dateInfo;
						
						PromSkuAnalyseDaoResult skuInfo = skuOrderInfoMap.get(key);
						if(skuInfo==null){
							skuOrderInfoMap.put(key, item);
						}else{
							skuInfo.setOrderAmount(item.getOrderAmount());
							skuInfo.setOrderQuantity(item.getOrderQuantity());
						}
					}
				}
			}
			
		}
		
		return skuOrderInfoMap;
		
	}

	/**
	 * sku去重
	 * @param promSkuBaseInfoList
	 * @return
	 */
	private List<PromSkuAnalyseDaoResult> removeRepeatSku(List<PromSkuAnalyseDaoResult> promSkuBaseInfoList) {
		List<PromSkuAnalyseDaoResult> filteredList = new LinkedList<PromSkuAnalyseDaoResult>();
		Map<String, PromSkuAnalyseDaoResult> map = new LinkedHashMap<String, PromSkuAnalyseDaoResult>();
		
		for(PromSkuAnalyseDaoResult item : promSkuBaseInfoList){
			String key = item.getSkuId()+"$"+item.getSkuStartDate();
			
			if(map.containsKey(key)){
				PromSkuAnalyseDaoResult oldItem = map.get(key);
				if(StringUtils.isEmpty(oldItem.getSkuUrl()) && StringUtils.isNotEmpty(oldItem.getSkuUrl())){
					map.put(key, item);
				}
			}else{
				map.put(key, item);
			}
		}
		
		filteredList.addAll(map.values());
		return filteredList;
	}

	/**
	 * 查询页面的点击量和pv
	 * @param queryParam
	 * @param promInfo
	 */
	private void queryPromClickAndPv(PromSkuAnalyseQueryParam queryParam, PromSkuAnalyseDaoResult promInfo) {
		com.gome.Controller.model.QueryParam hotMapParam = new com.gome.Controller.model.QueryParam();
		hotMapParam.setPageUrl(queryParam.getPromUrl());
		hotMapParam.setDataSource(queryParam.getDatasource());
		hotMapParam.setStartTime(queryParam.getStartDate());
		hotMapParam.setEndTime(queryParam.getEndDate());
		
		Long click = hotMapQueryDao.queryClickForPage(hotMapParam);
		promInfo.setPromClick(click==null? 0 : click.intValue());
		
		PageView pageView = hotMapQueryDao.querySummaryPageOfSumItem(hotMapParam);
		promInfo.setPromPv(pageView==null? 0 : (int)pageView.getPv());
	}


	/**
	 * 构建活动页的所有商品的指标
	 * @param promInfo
	 * @param promSkuList
	 * @param param
	 * @return
	 */
	private PromSkuAnalyseResponse buildResponse(PromSkuAnalyseDaoResult promInfo, List<PromSkuAnalyseDaoResult> promSkuList, PromSkuAnalyseQueryParam param) {
		PromSkuAnalyseResponse response = new PromSkuAnalyseResponse();
		if(promInfo!=null){
			response.setPageName(promInfo.getPageTitle());
		}
		
		response.setAmount(promSkuList==null? 0 : promSkuList.size());
		if(CollectionUtils.isNotEmpty(promSkuList)){
			
			List<PromSkuInfoServiceResult> list = new ArrayList<PromSkuInfoServiceResult>(promSkuList.size());
			
			for(PromSkuAnalyseDaoResult item : promSkuList){
				list.add(buildServiceResult(item, param));
			}
			
			response.setSkuInfos(list);
		}
		return response;
	}

	/**
	 * 构建活动页对应商品的指标
	 * @param item
	 * @param param
	 * @return
	 */
	private PromSkuInfoServiceResult buildServiceResult(PromSkuAnalyseDaoResult item, PromSkuAnalyseQueryParam param) {
		PromSkuInfoServiceResult result = new PromSkuInfoServiceResult();
		
		int skuDuration = calSkuDuration(item.getSkuStartDate(), item.getSkuEndDate());
		result.setSkuDuration(skuDuration);
		
		result.setSkuStartDate(formatDate(item.getSkuStartDate()));
		result.setSkuEndDate(formatDate(item.getSkuEndDate()));
		
		result.setSkuId(item.getSkuId());
		result.setSkuName(item.getSkuName());
		result.setSkuCategory(item.getCatdName1()+" & "+item.getCatdName2()+" & "+item.getCatdName3());
		result.setSkuBrandName(item.getBrandName());
		result.setSkuUrl(item.getSkuUrl());
		
		result.setIpv(item.getSkuClick());
		result.setClick(item.getSkuClick());
		result.calClickRate(item.getSkuClick(), item.getPromClick());
		result.calClickPercent(item.getSkuClick(), item.getPromPv());
		result.calClickDaily(item.getSkuClick(), skuDuration);
		
		result.setOrderCount(item.getOrderCount());
		result.setOrderAmount(item.getOrderAmount());
		result.setOrderQuantity(item.getOrderQuantity());
		result.calOrderCountDaily(item.getOrderCount(), skuDuration);
		result.calOrderAmountDaily(item.getOrderAmount(), skuDuration);
		result.calOrderQuantityDaily(item.getOrderQuantity(), skuDuration);
		result.calOrderTransferRate(item.getOrderCount(), item.getSkuUv());
		
		result.calIpvDailyBeforeOneWeek(item.getSkuPvBeforePromOneWeek());
		result.calOrderQuantityDailyBeforeOneWeek(item.getOrderQuantityBeforePromOneWeek());
		result.calOrderTransferRateDailyBeforeOneWeek(item.getOrderCountBeforePromOneWeek(), item.getSkuUvBeforePromOneWeek());
		
		result.setIntcmpModeId(item.getIntcmpModeId());
		result.setProductId(item.getProductId());
		
		result.setSaleableQuantity(item.isAvailableSellingWarn()? "1" : "0");
		
		return result;
	}
	
	
	private String formatDate(String date) {
		if(StringUtils.isEmpty(date))
			return null;
		else{
			try {
				Date d = df1.parse(date);
				return df2.format(d);
			} catch (ParseException e) {
				logger.error(e.getMessage(), e);
				return date;
			}
		}
	}


	/**
	 * 计算sku的放置时间
	 * @param skuStartDate
	 * @param skuEndDate
	 * @param queryStartDate
	 * @param queryEndDate
	 * @return
	 */
	private int calSkuDuration(String skuStartDate, String skuEndDate){
//		String calStartDate, calEndDate;
		
//		if(skuStartDate!=null){
//			calStartDate = skuStartDate.compareTo(queryStartDate)>0? skuStartDate : queryStartDate;
//		}else
//			calStartDate = queryStartDate;
//		
//		if(skuEndDate!=null){
//			calEndDate = skuEndDate.compareTo(queryEndDate)<0? skuEndDate : queryEndDate;
//		}else{
//			calEndDate = queryEndDate;
//		}
		
		try {
			Date startDate = df1.parse(skuStartDate);
			Date endDate = df1.parse(skuEndDate);
			
			long duration = (endDate.getTime() - startDate.getTime()) /1000 / 3600 / 24;
			return duration<0? 0 : (int)duration+1;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return 0;
		}
	}
	
	private Map<String, PromSkuAnalyseDaoResult> querySkuAllStationInfos(List<String> skuIds, PromSkuAnalyseQueryParam param){
		Map<String, PromSkuAnalyseDaoResult> skuInfoMap = new HashMap<String, PromSkuAnalyseDaoResult>();
		
		int skuIdSize = skuIds.size();
		int queryCount = skuIdSize%QUERY_COUNT_ONE_QUERY==0? skuIdSize/QUERY_COUNT_ONE_QUERY : skuIdSize/QUERY_COUNT_ONE_QUERY+1;
		
		for(int i=0;i<queryCount;i++){
			int fromIndex = i*QUERY_COUNT_ONE_QUERY;
			int toIndex = (i+1)*QUERY_COUNT_ONE_QUERY;
			toIndex = toIndex>skuIdSize? skuIdSize : toIndex;
			List<String> subSkuIds = skuIds.subList(fromIndex, toIndex);
			
			List<PromSkuAnalyseDaoResult> result = dao.queryAllStationSkuInfo(param, subSkuIds);
			
			if(CollectionUtils.isNotEmpty(result)){
				for(PromSkuAnalyseDaoResult item : result){
					skuInfoMap.put(item.getSkuId(), item);
				}
			}
		}
		
		return skuInfoMap;
	}
	
	private class PromSkuComparator implements Comparator<PromSkuInfoServiceResult>{
		
		private String column;
		private int orderTypeFactor;
		
		public PromSkuComparator(String column, String orderType){
			this.column = column;
			
			if(orderType==null){
				orderTypeFactor = 1;
			}else if(orderType.equals(PromSkuAnalyseConstants.ORDER_TYPE_DESC)){
					orderTypeFactor = -1;
			}else{
				orderTypeFactor = 1;
			}
		}

		@Override
		public int compare(PromSkuInfoServiceResult o1, PromSkuInfoServiceResult o2) {
			int comparator;
			
			if(o1==null)
				comparator = -1;
			else if(o2==null){
				comparator = 1;
			}else{
				Object value1 = o1.getOrderColumnValue(column);
				Object value2 = o2.getOrderColumnValue(column);
				
				if(value1==null && value2==null)
					comparator = 0;
				else if(value1==null)
					comparator = -1;
				else if(value2==null)
					comparator = 1;
				else if(value1 instanceof String){
					comparator = ((String) value1).compareTo((String)value2);
				}else if(value1 instanceof Float){
					comparator = ((Float)value1).compareTo((Float)value2);
				}else if(value1 instanceof Double){
					comparator = ((Double)value1).compareTo((Double)value2);
				}else if(value1 instanceof Integer){
					comparator = ((Integer)value1).compareTo((Integer)value2);
				}else
					comparator = 0;
			}
			return comparator * orderTypeFactor;
		}
		
	}

	/**
	 * 查询某个sku对应的城市的可卖数、pv、点击量
	 */
	@Override
	public List<PromSkuAnalyseCityInfo> queryPromSkuCityInfo(PromSkuAnalyseQueryParam param) {
		Map<String, PromSkuAnalyseCityInfo> cityInfoMap = new LinkedHashMap<String, PromSkuAnalyseCityInfo>(10, 1.0f);
		for(String cityName : cityNames10){
			cityInfoMap.put(cityName, new PromSkuAnalyseCityInfo(cityName));
		}
		
		//城市订单量
		List<PromSkuAnalyseCityInfo> cityOrderCounts = dao.queryPromSkuCityOrderCount(param, cityNames10);
		if(CollectionUtils.isNotEmpty(cityOrderCounts)){
			for(PromSkuAnalyseCityInfo item : cityOrderCounts){
				PromSkuAnalyseCityInfo value = cityInfoMap.get(item.getCityName());
				if(value!=null)
					value.setOrderCount(item.getOrderCount());
			}
		}
		
		//城市商品件数
		List<PromSkuAnalyseCityInfo> cityOrderQuantities = dao.queryPromSkuCityOrderQuantity(param, cityNames10);
		if(CollectionUtils.isNotEmpty(cityOrderQuantities)){
			for(PromSkuAnalyseCityInfo item : cityOrderQuantities){
				PromSkuAnalyseCityInfo value = cityInfoMap.get(item.getCityName());
				if(value!=null)
					value.setOrderQuantity(item.getOrderQuantity());
			}
		}
		
		//城市pv
		List<PromSkuAnalyseCityInfo> cityPvs = dao.queryPromSkuCityPv(param, cityNames10);
		if(CollectionUtils.isNotEmpty(cityPvs)){
			for(PromSkuAnalyseCityInfo item : cityPvs){
				PromSkuAnalyseCityInfo value = cityInfoMap.get(item.getCityName());
				if(value!=null)
					value.setPv(item.getPv());
			}
		}
		
		//城市可卖数
		List<PromSkuAnalyseCityAvailableselling> citySellings = dao.queryCityAvailableselling(param, cityNames5Daids);
		if(CollectionUtils.isNotEmpty(citySellings)){
			for(PromSkuAnalyseCityAvailableselling item : citySellings){
				PromSkuAnalyseCityInfo value = cityInfoMap.get(item.getCityName());
				if(value!=null)
					value.setAvailableselling(item.getSelling());
			}
		}
		
		for(String city: cityNames5){
			PromSkuAnalyseCityInfo value = cityInfoMap.get(city);
			if(value!=null && value.getAvailableselling()==null)
				value.setAvailableselling(0);
		}
		
		return new LinkedList<PromSkuAnalyseCityInfo>(cityInfoMap.values());
	}

	public boolean isUseCache() {
		return isUseCache;
	}

	@Override
	public void setUseCache(boolean isUseCache) {
		this.isUseCache = isUseCache;
	}

	@Override
	public void clearCache() {
		gcache.scanCluster(CACHE_KEY_PREFIX+"*", 1024, new KeysExecutor(){

			@Override
			public void execute(List<String> keys) {
				if(CollectionUtils.isNotEmpty(keys)){
					for(String key: keys){
						gcache.del(key);
					}
				}
			}
			
		});
	}

}
