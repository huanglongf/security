package com.gome.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gome.Controller.model.PageResponse;
import com.gome.dao.model.PageView;
import com.gome.pathanalyse.dao.IKylinPathAnalyseQueryDao;
import com.gome.pathanalyse.model.PathAnalyseQueryParam;
import com.gome.pathanalyse.model.PathAnalyseQueryResult;
import com.gome.pathanalyseplus.dao.PathAnalysePlusQueryDao;
import com.gome.service.IPathAnalyseQueryService;

/**
 * 页面路径分析查询服务实现类
 * @author wangshubao
 *
 */
@Service
public class PathAnalyseQueryServiceImpl implements IPathAnalyseQueryService {

	@Autowired
	private IKylinPathAnalyseQueryDao pathAnalyseDao;
	
	@Autowired
	private PathAnalysePlusQueryDao pathAnalysePlusDao;

	@Override
	public PathAnalyseQueryResult queryDirectEntry(PathAnalyseQueryParam param) {
		PathAnalyseQueryResult summary= pathAnalyseDao.queryPvUvVisitsOfPagetype(param);
		
		PathAnalyseQueryResult result = pathAnalyseDao.queryDirectEntry(param);
		
		if(summary!=null && result!=null){

			result.setPvRate(result.getPv(), summary.getPv());
			result.setUvRate(result.getUv(), summary.getUv());
			result.setVisitsRate(result.getVisits(), summary.getVisits());
		}
		
		return result;
	}

	@Override
	public PathAnalyseQueryResult queryDirectExit(PathAnalyseQueryParam param) {
		PathAnalyseQueryResult summary= pathAnalyseDao.queryPvUvVisitsOfPagetype(param);
		
		PathAnalyseQueryResult result =  pathAnalyseDao.queryExit(param);
		
		if(summary!=null && result!=null){
			
			//由于访问在计算时是不精确distinct，但是pv应该和访问相等，所以这里重新赋值
			result.setVisits(result.getPv());
			
			result.setPvRate(result.getPv(), summary.getPv());		//注意此处的pvRate有问题，在controller里会重新计算
			result.setUvRate(result.getUv(), summary.getUv());
			result.setVisitsRate(result.getVisits(), summary.getVisits());
		}
		
		return result;
	}

	@Override
	public List<PathAnalyseQueryResult> queryTopNEntries(PathAnalyseQueryParam param) {
		PathAnalyseQueryResult summary= pathAnalyseDao.queryPvUvVisitsOfPagetype(param);
		
		List<PathAnalyseQueryResult> list = pathAnalyseDao.queryTopNEntries(param);
		if(CollectionUtils.isNotEmpty(list)){
			for(PathAnalyseQueryResult item : list){
				param.setTopNGroupColumnValue(item.getCode());
				
				PathAnalyseQueryResult result = pathAnalyseDao.queryUvVisitsOfEntry(param);
 				if(result!=null){
					item.setUv(result.getUv());
					item.setVisits(result.getVisits());
					
					if(summary!=null){
						item.setPvRate(item.getPv(), summary.getPv());
						item.setUvRate(item.getUv(), summary.getUv());
						item.setVisitsRate(item.getVisits(), summary.getVisits());
					}
				}
 				
 				if(StringUtils.isEmpty(item.getCode())){
 					item.setCode("(未获取)");
 				}
			}
		}
		
		return list;
	}

	@Override
	public List<PathAnalyseQueryResult> queryTopNFollows(PathAnalyseQueryParam param) {
		PathAnalyseQueryResult summary= pathAnalyseDao.queryPvUvVisitsOfPagetype(param);
		
		List<PathAnalyseQueryResult> list = pathAnalyseDao.queryTopNFollows(param);
		if(CollectionUtils.isNotEmpty(list)){
			for(PathAnalyseQueryResult item : list){
				param.setTopNGroupColumnValue(item.getCode());
				
				PathAnalyseQueryResult result = pathAnalyseDao.queryUvVisitsOfFollow(param);
				if(result != null){
					item.setUv(result.getUv());
					item.setVisits(result.getVisits());
					
					if(summary!=null){
						item.setPvRate(item.getPv(), summary.getPv());		//注意此处的pvRate有问题，在controller里会重新计算
						item.setUvRate(item.getUv(), summary.getUv());
						item.setVisitsRate(item.getVisits(), summary.getVisits());
					}
				}
 				
 				if(StringUtils.isEmpty(item.getCode())){
 					item.setCode("(未获取)");
 				}
			}
		}
		
		return list;
	}

	@Override
	public PageResponse querySummaryOfPagetype(PathAnalyseQueryParam param) {
		PageView allStationInfo = querryAllStationInfo(param);
		if(allStationInfo==null)
			allStationInfo = new PageView();
		
		PageView summaryOfPage = queryAllItemsForPage(param);
		
		return buildPageResponse(allStationInfo, summaryOfPage);
	}
	
	/**
	 * 查询并汇总全站数据
	 * @param param
	 * @return
	 */
	private PageView querryAllStationInfo(PathAnalyseQueryParam param){
		PageView allStationPageView= pathAnalyseDao.queryAllStation(param);
		
		return allStationPageView;
	}
	
	/**
	 * 查询某页面的所有汇总指标
	 * @param param
	 * @return
	 */
	private PageView queryAllItemsForPage(PathAnalyseQueryParam param){
		PageView pageSummaryOfSumItem = pathAnalyseDao.querySummaryOfSumItemForPagetype(param);
		if(pageSummaryOfSumItem==null)
			pageSummaryOfSumItem = new PageView();
		
		PageView pageSummaryOfNotSumItem = pathAnalyseDao.querySummaryOfNotSumItemForPagetype(param);
		
		PageView pageSummaryOfOrderItem = pathAnalyseDao.querySummaryPageOfOrderItem(param);
		
		//Long click = pathAnalyseDao.queryClickOfPagetype(param);
		
		pageSummaryOfSumItem.setUv(pageSummaryOfNotSumItem==null? 0 : pageSummaryOfNotSumItem.getUv());
		pageSummaryOfSumItem.setVisits(pageSummaryOfNotSumItem==null? 0 :pageSummaryOfNotSumItem.getVisits());
		pageSummaryOfSumItem.setParticipationOrdersCount(pageSummaryOfOrderItem==null? 0 : pageSummaryOfOrderItem.getParticipationOrdersCount());
		pageSummaryOfSumItem.setOrderTotalAmount(pageSummaryOfOrderItem==null? 0 : pageSummaryOfOrderItem.getOrderTotalAmount());
		//pageSummaryOfSumItem.setClick(click==null? 0 : click);
		pageSummaryOfSumItem.setClick(0);
		
		return pageSummaryOfSumItem;
	}
	
	/**
	 * 构建页面汇总指标的返回信息
	 * @param allStationInfo
	 * @param pageSummary
	 * @param param
	 * @return
	 */
	private PageResponse buildPageResponse(PageView allStationInfo, PageView pageSummary){
		long alluv = allStationInfo.getUv();
		long allpv = allStationInfo.getPv();
		long allbounce = allStationInfo.getBounce();
		long allvistors = allStationInfo.getVisits();
		
		long pageuv = pageSummary.getUv();
		long pagepv = pageSummary.getPv();
		long pagebounce = pageSummary.getBounce();
		long entriesCount = pageSummary.getEntriesCount();
		long pagevistors = pageSummary.getVisits();
		long click = pageSummary.getClick();
		long pageexitCount = pageSummary.getExitCount();
		double viewTime = pageSummary.getViewTime()==null? 0 : pageSummary.getViewTime();
		double orderCount = pageSummary.getParticipationOrdersCount();
		double orderAmount = pageSummary.getOrderTotalAmount();
		
		PageResponse pageResponse = new PageResponse();

		pageResponse.setUv(pageuv);
		pageResponse.setUvRate(pageuv, alluv);

		pageResponse.setPv(pagepv);
		pageResponse.setPvRate(pagepv, allpv);

		pageResponse.setBounce(pagebounce, entriesCount);

		pageResponse.setBounceRate(allbounce, allvistors);

		pageResponse.setVistor(pagevistors);
		pageResponse.setVistorRate(pagevistors, allvistors);

		pageResponse.setClick(click);
		pageResponse.setClickRate(click, pagepv);

		pageResponse.setExitCount(pageexitCount);
		pageResponse.setExitRate(pageexitCount, pagevistors);
		pageResponse.setEntriesCount(entriesCount);

		pageResponse.setViewTime(viewTime);
		pageResponse.setAvgViewTime(pagepv, pageResponse.getViewTime());
		
		pageResponse.setTakeOrderCount(orderCount);
		pageResponse.setOrderTotalAmount(orderAmount);
		pageResponse.setTakeOrderRate(orderCount, pageuv);
		
		//设置页面描述
		pageResponse.setPageDescription(pageSummary.getPageSite()+">"+pageSummary.getPageChannel()+">"+pageSummary.getPageType());
		
		return pageResponse;
	}

	/**
	 * 查询下一页的总pv
	 */
	@Override
	public int queryPVOfReferrerPagetype(PathAnalyseQueryParam param) {
		PathAnalyseQueryResult summary= pathAnalyseDao.queryPvUvVisitsOfReferrerPagetype(param);
		return summary==null? 0 : summary.getPv();
	}
	
	
	@Override
	public Map<String, Object> queryPathAnalyseData(
			PathAnalyseQueryParam param) {
		//查询全站的uv,pv,visits,bounces指标
		PageView allStationPageView= pathAnalysePlusDao.queryAllStation(param);
		
		//查询某一页面的指标
		PageView pageSummaryOfSumItem = pathAnalysePlusDao.querySummaryOfSumItemForPagetype(param);
		if(pageSummaryOfSumItem==null)
			pageSummaryOfSumItem = new PageView();
//		PageView pageSummaryOfNotSumItem = pathAnalysePlusDao.querySummaryOfNotSumItemForPagetype(param);
		PageView pageSummaryOfOrderItem = pathAnalysePlusDao.querySummaryPageOfOrderItem(param);
//		pageSummaryOfSumItem.setUv(pageSummaryOfNotSumItem==null? 0 : pageSummaryOfNotSumItem.getUv());
//		pageSummaryOfSumItem.setVisits(pageSummaryOfNotSumItem==null? 0 :pageSummaryOfNotSumItem.getVisits());
		pageSummaryOfSumItem.setParticipationOrdersCount(pageSummaryOfOrderItem==null? 0 : pageSummaryOfOrderItem.getParticipationOrdersCount());
		pageSummaryOfSumItem.setOrderTotalAmount(pageSummaryOfOrderItem==null? 0 : pageSummaryOfOrderItem.getOrderTotalAmount());
		pageSummaryOfSumItem.setClick(0);
		//构建页面汇总指标的返回信息
		PageResponse summary = buildPageResponse(allStationPageView, pageSummaryOfSumItem);
		
		String description = buildDescription(param);
		summary.setPageDescription(description);
		
		//登录页
//		PathAnalyseQueryResult summaryResult= pathAnalysePlusDao.queryPvUvVisitsOfPagetype(param);
		PathAnalyseQueryResult directEntry = pathAnalysePlusDao.queryDirectEntry(param);
		if(pageSummaryOfSumItem!=null && directEntry!=null){
			directEntry.setPvRate(directEntry.getPv(), Integer.parseInt(String.valueOf(pageSummaryOfSumItem.getPv())));
			directEntry.setUvRate(directEntry.getUv(), Integer.parseInt(String.valueOf(pageSummaryOfSumItem.getUv())));
			directEntry.setVisitsRate(directEntry.getVisits(), Integer.parseInt(String.valueOf(pageSummaryOfSumItem.getVisits())));
		}
		
		//上一页、
		List<PathAnalyseQueryResult> topNEntry = pathAnalysePlusDao.queryTopNEntries(param);
		if(CollectionUtils.isNotEmpty(topNEntry)){
			for(PathAnalyseQueryResult item : topNEntry){
//				param.setTopNGroupColumnValue(item.getCode());
//				
//				PathAnalyseQueryResult result = pathAnalysePlusDao.queryUvVisitsOfEntry(param);
// 				if(result!=null){
//					item.setUv(result.getUv());
//					item.setVisits(result.getVisits());
					
					if(pageSummaryOfSumItem!=null){
						item.setPvRate(item.getPv(), Integer.parseInt(String.valueOf(pageSummaryOfSumItem.getPv())));
						item.setUvRate(item.getUv(), Integer.parseInt(String.valueOf(pageSummaryOfSumItem.getUv())));
						item.setVisitsRate(item.getVisits(), Integer.parseInt(String.valueOf(pageSummaryOfSumItem.getVisits())));
					}
//				}
 				
 				if(StringUtils.isEmpty(item.getCode())){
 					item.setCode("(未获取)");
 				}
			}
		}
		
		buildOtherResultForEntry(topNEntry, directEntry, summary, param);
		
		//退出页
		PathAnalyseQueryResult directExit =  pathAnalysePlusDao.queryExit(param);
		if(pageSummaryOfSumItem!=null && directExit!=null){
			
			//由于访问在计算时是不精确distinct，但是pv应该和访问相等，所以这里重新赋值
			directExit.setVisits(directExit.getPv());
			directExit.setPvRate(directExit.getPv(), Integer.parseInt(String.valueOf(pageSummaryOfSumItem.getPv())));		//注意此处的pvRate有问题，在controller里会重新计算
			directExit.setUvRate(directExit.getUv(), Integer.parseInt(String.valueOf(pageSummaryOfSumItem.getUv())));
			directExit.setVisitsRate(directExit.getVisits(), Integer.parseInt(String.valueOf(pageSummaryOfSumItem.getVisits())));
		}
		
		
		//下一页
		List<PathAnalyseQueryResult> topNFollow = pathAnalysePlusDao.queryTopNFollows(param);
		if(CollectionUtils.isNotEmpty(topNFollow)){
			for(PathAnalyseQueryResult item : topNFollow){
//				param.setTopNGroupColumnValue(item.getCode());
//				
//				PathAnalyseQueryResult result = pathAnalysePlusDao.queryUvVisitsOfFollow(param);
//				if(result != null){
//					item.setUv(result.getUv());
//					item.setVisits(result.getVisits());
					
					if(pageSummaryOfSumItem!=null){
						item.setPvRate(item.getPv(), Integer.parseInt(String.valueOf(pageSummaryOfSumItem.getPv())));		//注意此处的pvRate有问题，在controller里会重新计算
						item.setUvRate(item.getUv(), Integer.parseInt(String.valueOf(pageSummaryOfSumItem.getUv())));
						item.setVisitsRate(item.getVisits(), Integer.parseInt(String.valueOf(pageSummaryOfSumItem.getVisits())));
					}
//				}
 				
 				if(StringUtils.isEmpty(item.getCode())){
 					item.setCode("(未获取)");
 				}
			}
		}
		
		PathAnalyseQueryResult summaryReferrer= pathAnalysePlusDao.queryPvUvVisitsOfReferrerPagetype(param);
		
		buildOtherResultForFollow(topNFollow, directExit, summaryReferrer==null? 0 : summaryReferrer.getPv(), param);
		
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("summary", summary);
		map.put("directEntry", directEntry);
		map.put("directExit", directExit);
		map.put("topNEntry", topNEntry);
		map.put("topNFollow", topNFollow);
		
		return map;
	}
	
	/**
	 * 为上一页构建其他项
	 * @param topN
	 * @param direct
	 * @param summary
	 * @param param
	 */
	private void buildOtherResultForEntry(List<PathAnalyseQueryResult> topN, PathAnalyseQueryResult direct, PageResponse summary, PathAnalyseQueryParam param) {
		
		int topNSize = topN==null? 0 : topN.size();
		int pageSize = param.getPageSize();
		if(topNSize < pageSize)
			return;

		topN.remove(topN.size()-1);
		
		PathAnalyseQueryResult otherResult = new PathAnalyseQueryResult();
		otherResult.setCode("所有其他");
		
		int pv = direct==null? 0 : direct.getPv();
		if(CollectionUtils.isNotEmpty(topN)){
			for(PathAnalyseQueryResult item : topN){
				pv = pv+item.getPv();
			}
		}
		
		int totalPv = summary==null?0 : (int)summary.getPv();
		int otherPv = totalPv-pv>=0? totalPv-pv : 0;
		otherResult.setPv(otherPv);
		otherResult.setPvRate(otherPv, totalPv);
		
		topN.add(otherResult);
	}
	
	/**
	 * 为下一页构建其他项
	 * @param topN
	 * @param direct
	 * @param param
	 */
	private void buildOtherResultForFollow(List<PathAnalyseQueryResult> topN, PathAnalyseQueryResult direct, int referrerPagetypePv, PathAnalyseQueryParam param) {
		
		//计算总的pv，等于退出网站pv+referrerPv
		int directPv = direct==null? 0 : direct.getPv();
		int totalPv = directPv + referrerPagetypePv;
		
		if(direct != null)
			direct.setPvRate(direct.getPv(), totalPv);
		
		if(CollectionUtils.isNotEmpty(topN)){
			for(PathAnalyseQueryResult item : topN){
				item.setPvRate(item.getPv(), totalPv);
			}
		}
		
		int topNSize = topN==null? 0 : topN.size();
		int pageSize = param.getPageSize();
		if(topNSize < pageSize)
			return;

		topN.remove(topN.size()-1);
		
		PathAnalyseQueryResult otherResult = new PathAnalyseQueryResult();
		otherResult.setCode("所有其他");
		
		int pv = direct==null? 0 : direct.getPv();
		if(CollectionUtils.isNotEmpty(topN)){
			for(PathAnalyseQueryResult item : topN){
				pv = pv+item.getPv();
			}
		}
		
		int otherPv = totalPv-pv>=0? totalPv-pv : 0;
		otherResult.setPv(otherPv);
		otherResult.setPvRate(otherPv, totalPv);
		
		topN.add(otherResult);
	}
	private String buildDescription(PathAnalyseQueryParam queryParam) {
		StringBuilder title = new StringBuilder();
		if(StringUtils.isEmpty(queryParam.getPageSite()))
			title.append("全部 - ");
		else
			title.append(queryParam.getPageSite()).append(" - ");
		
		if(StringUtils.isEmpty(queryParam.getPageChannel()))
			title.append("全部 - ");
		else
			title.append(queryParam.getPageChannel()).append(" - ");
		
		if(StringUtils.isEmpty(queryParam.getPageType()))
			title.append("全部");
		else
			title.append(queryParam.getPageType());
		
		return title.toString();
	}

	@Override
	public Map<String, Object> queryTopNEntriesData(
			PathAnalyseQueryParam param) {
		Map<String, Object> map = new HashMap<String, Object>();
		PageView pageSummaryOfSumItem = pathAnalysePlusDao.querySummaryOfSumItemForPagetype(param);
		List<PathAnalyseQueryResult> topNEntry = pathAnalysePlusDao.queryTopNEntries(param);
		if(CollectionUtils.isNotEmpty(topNEntry)){
			for(PathAnalyseQueryResult item : topNEntry){
//						param.setTopNGroupColumnValue(item.getCode());
//						
//						PathAnalyseQueryResult result = pathAnalysePlusDao.queryUvVisitsOfEntry(param);
//		 				if(result!=null){
//							item.setUv(result.getUv());
//							item.setVisits(result.getVisits());
					
					if(pageSummaryOfSumItem!=null){
						item.setPvRate(item.getPv(), Integer.parseInt(String.valueOf(pageSummaryOfSumItem.getPv())));
						item.setUvRate(item.getUv(), Integer.parseInt(String.valueOf(pageSummaryOfSumItem.getUv())));
						item.setVisitsRate(item.getVisits(), Integer.parseInt(String.valueOf(pageSummaryOfSumItem.getVisits())));
					}
//						}
 				
 				if(StringUtils.isEmpty(item.getCode())){
 					item.setCode("(未获取)");
 				}
			}
		}
		map.put("topNEntry", topNEntry);
		return map;
	}

	@Override
	public Map<String, Object> queryTopNFollowsData(
			PathAnalyseQueryParam param) {
		Map<String, Object> map = new HashMap<String, Object>();
		PageView pageSummaryOfSumItem = pathAnalysePlusDao.querySummaryOfSumItemForPagetype(param);
		List<PathAnalyseQueryResult> topNFollow = pathAnalysePlusDao.queryTopNFollows(param);
		if(CollectionUtils.isNotEmpty(topNFollow)){
			for(PathAnalyseQueryResult item : topNFollow){
//				param.setTopNGroupColumnValue(item.getCode());
//				
//				PathAnalyseQueryResult result = pathAnalysePlusDao.queryUvVisitsOfFollow(param);
//				if(result != null){
//					item.setUv(result.getUv());
//					item.setVisits(result.getVisits());
					
					if(pageSummaryOfSumItem!=null){
						item.setPvRate(item.getPv(), Integer.parseInt(String.valueOf(pageSummaryOfSumItem.getPv())));		//注意此处的pvRate有问题，在controller里会重新计算
						item.setUvRate(item.getUv(), Integer.parseInt(String.valueOf(pageSummaryOfSumItem.getUv())));
						item.setVisitsRate(item.getVisits(), Integer.parseInt(String.valueOf(pageSummaryOfSumItem.getVisits())));
					}
//				}
 				
 				if(StringUtils.isEmpty(item.getCode())){
 					item.setCode("(未获取)");
 				}
			}
		}
		map.put("topNFollow", topNFollow);
		return map;
	}
}
