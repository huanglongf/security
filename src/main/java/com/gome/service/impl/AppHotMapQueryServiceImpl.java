package com.gome.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gome.Controller.model.AppHotMapQueryParam;
import com.gome.Controller.model.PageResponse;
import com.gome.Controller.model.ResourceResponse;
import com.gome.dao.IKylinAppHotMapDao;
import com.gome.dao.model.PageResource;
import com.gome.dao.model.PageView;
import com.gome.plus.IHotMapAppDao;
import com.gome.plus.dao.IMysqlAppHotMapDao;
import com.gome.service.IAppHotMapQueryService;

/**
 * app首页热力图服务实现类
 * @author wangshubao
 *
 */
@Service
public class AppHotMapQueryServiceImpl implements IAppHotMapQueryService {
	
	private static final Logger logger = LoggerFactory.getLogger(AppHotMapQueryServiceImpl.class);

	@Autowired
	private IKylinAppHotMapDao kylinDao;
	@Autowired
	private IMysqlAppHotMapDao mysqlDao;
	
	public IHotMapAppDao getDao(AppHotMapQueryParam param){
		return "PLUS-LOG".equals(param.getDataSource()) ? mysqlDao : kylinDao;
	}
	
	@Override
	public PageResponse querySummaryOfPage(AppHotMapQueryParam param) {
		PageView allStationInfo = getDao(param).queryAllStation(param);
		if (allStationInfo == null)
			allStationInfo = new PageView();

		PageView summaryOfPage = queryAllItemsForPage(param);

		return buildPageResponse(allStationInfo, summaryOfPage, param);
	}
	
	/**
	 * 查询某页面的所有汇总指标
	 * 
	 * @param param
	 * @return
	 */
	private PageView queryAllItemsForPage(AppHotMapQueryParam param) {
		PageView pageSummaryItem = getDao(param).querySummaryOfSy(param);
		if (pageSummaryItem == null)
			pageSummaryItem = new PageView();

		Long click = getDao(param).queryClickForSy(param);

		pageSummaryItem.setClick(click == null ? 0 : click);

		return pageSummaryItem;
	}

	@Override
	public Map<String, ResourceResponse> queryAllResourceForPage(AppHotMapQueryParam param, PageResponse pageSummary) {
		// 查询资源位的sum类指标
		List<PageResource> pageResourceOfFullItemList = getDao(param).queryResourcesOfSy(param);

		// 整合sum与非sum类指标，构建返回实体
		Map<String, ResourceResponse> returnMap = new HashMap<String, ResourceResponse>();
		for (PageResource item : pageResourceOfFullItemList) {
			String code = item.getCode();
			returnMap.put(code, buildResourceResponse(param, code, item, pageSummary));
		}
		return returnMap;
	}
	
	/**
	 * 构建页面资源位信息的返回实体
	 * 
	 * @param param
	 * @param intcmp
	 * @param fullItem
	 * @param notSumItem
	 * @param pageSummary
	 * @return
	 */
	private ResourceResponse buildResourceResponse(AppHotMapQueryParam param, String intcmp, PageResource fullItem, PageResponse pageSummary) {
		ResourceResponse rs = new ResourceResponse();
		rs.setCode(intcmp);
		rs.setUv(fullItem == null ? 0 : fullItem.getUv());
		rs.setPv(fullItem.getPv());
		rs.setVistor(fullItem == null ? 0 : fullItem.getVistor());
		rs.setClick(fullItem.getClick());

		rs.setClickRate(rs.getClick(), pageSummary.getClick());
		rs.setVistorRate(rs.getVistor(), pageSummary.getVistor());
		rs.setUvRate(rs.getUv(), pageSummary.getUv());
		rs.setAvgPv(rs.getPv(), rs.getVistor());

		return rs;
	}

	@Override
	public Map<String, Object> queryResourceHistoryInfo(AppHotMapQueryParam param) {
		
		//查询首页的汇总数据，取出uv、visits、click计算占比
		PageView pageSummaryItem = getDao(param).querySummaryOfSy(param);
		if (pageSummaryItem == null)
			pageSummaryItem = new PageView();

		Long click = getDao(param).queryClickForSy(param);

		// 查询资源位的历史信息指标汇总
		PageResource totalResourceInfo = getDao(param).queryTotalItemOfIntcmp(param);
		ResourceResponse totalResourceInfoResponse = buildResourceResponse(totalResourceInfo, pageSummaryItem.getUv(), pageSummaryItem.getVisits(), click);

		// 查询资源位的详细历史信息
		List<PageResource> historyList = getDao(param).queryHistoryOfIntcmp(param);
		List<ResourceResponse> historyResponseList = new LinkedList<ResourceResponse>();
		if (CollectionUtils.isNotEmpty(historyList)) {
			for (PageResource item : historyList)
				historyResponseList.add(buildResourceResponse(item, pageSummaryItem.getUv(), pageSummaryItem.getVisits(), click));

			// 按点击量排序
			Collections.sort(historyResponseList, new Comparator<ResourceResponse>() {

				public int compare(ResourceResponse o1, ResourceResponse o2) {

					if (o1.getClick() > o2.getClick()) {
						return -1;

					} else if (o1.getClick() < o2.getClick()) {
						return 1;
					}

					return 0;
				}

			});
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("active", historyResponseList);
		map.put("total", totalResourceInfoResponse);
		return map;
	}
	
	/**
	 * 构建资源位历史信息的返回数据
	 * 
	 * @param resourceInfo
	 * @param syUv
	 * @param syClick
	 * @param syVisitors
	 * @param allOrderCount
	 * @return
	 */
	private ResourceResponse buildResourceResponse(PageResource resourceInfo, long pageUv, long pageVisits, long pageClick) {
		ResourceResponse intCmpSummaryResp = new ResourceResponse();

		intCmpSummaryResp.setUrl(resourceInfo.getUrl());
		intCmpSummaryResp.setTitle(resourceInfo.getTitle());
		intCmpSummaryResp.setCode(resourceInfo.getCode());
		intCmpSummaryResp.setUv(resourceInfo.getUv());
		intCmpSummaryResp.setPv(resourceInfo.getPv());
		intCmpSummaryResp.setVistor(resourceInfo.getVistor());
		intCmpSummaryResp.setClick(resourceInfo.getClick());
		
		intCmpSummaryResp.setUvRate(resourceInfo.getUv(), pageUv);
		intCmpSummaryResp.setVistorRate(resourceInfo.getVistor(), pageVisits);
		intCmpSummaryResp.setClickRate(resourceInfo.getClick(), pageClick);
		intCmpSummaryResp.setAvgPv(resourceInfo.getPv(), resourceInfo.getVistor());

		return intCmpSummaryResp;
	}

	@Override
	public Map<String, PageResponse> chartQueryOfHour(AppHotMapQueryParam param) {
		List<PageView> pageSummaryOfHourList = getDao(param).querySyFullItemOfHour(param);

		List<PageView> pageClickOfHourList = getDao(param).querySyClickOfHour(param);

		// 构建map
		Map<String, PageView> map = new HashMap<String, PageView>();
		if (CollectionUtils.isNotEmpty(pageSummaryOfHourList)) {

			for (PageView item : pageSummaryOfHourList) {
				map.put(item.getCode(), item);
			}

		}

		// 设置点击量
		if (CollectionUtils.isNotEmpty(pageClickOfHourList)) {
			for (PageView item : pageClickOfHourList) {
				PageView val = map.get(item.getCode());
				if (val != null)
					val.setClick(item.getClick());
				else {
					map.put(item.getCode(), item);
				}
			}
		}

		Map<String, PageResponse> responseMap = new TreeMap<String, PageResponse>();
		for (Map.Entry<String, PageView> item : map.entrySet()) {
			responseMap.put(item.getKey(), buildPageResponse(item.getValue(), param));
		}

		Map<String, PageResponse> formatedResponseMap = new LinkedHashMap<String, PageResponse>();
		for (Map.Entry<String, PageResponse> entry : responseMap.entrySet()) {
			formatedResponseMap.put(entry.getKey() + "点", entry.getValue());
		}
		return formatedResponseMap;

	}

	@Override
	public Map<String, PageResponse> chartQueryOfDay(AppHotMapQueryParam param) {
		List<PageView> pageSummaryOfDayList = getDao(param).querySyFullItemOfDay(param);

		List<PageView> pageClickOfDayList = getDao(param).querySyClickOfDay(param);

		// 构建map
		Map<String, PageView> map = new HashMap<String, PageView>();
		if (CollectionUtils.isNotEmpty(pageSummaryOfDayList)) {

			for (PageView item : pageSummaryOfDayList) {
				map.put(item.getCode(), item);
			}

		}

		// 设置点击量
		if (CollectionUtils.isNotEmpty(pageClickOfDayList)) {
			for (PageView item : pageClickOfDayList) {
				PageView val = map.get(item.getCode());
				if (val != null)
					val.setClick(item.getClick());
				else {
					map.put(item.getCode(), item);
				}
			}
		}

		Map<String, PageResponse> responseMap = new HashMap<String, PageResponse>(map.size(), 1.0f);
		for (Map.Entry<String, PageView> item : map.entrySet()) {
			responseMap.put(formatDate(item.getKey()), buildPageResponse(item.getValue(), param));
		}
		return responseMap;
	}
	
	/**
	 * 构建页面汇总指标的返回信息
	 * 
	 * @param allStationInfo
	 * @param pageSummary
	 * @param param
	 * @return
	 */
	private PageResponse buildPageResponse(PageView allStationInfo, PageView pageSummary, AppHotMapQueryParam param) {
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
		double viewTime = pageSummary.getViewTime() == null ? 0 : pageSummary.getViewTime();
		double orderCount = pageSummary.getParticipationOrdersCount();
		double orderAmount = pageSummary.getOrderTotalAmount();

		PageResponse pageResponse = new PageResponse();

		pageResponse.setUv(pageuv);
		pageResponse.setUvRate(pageuv, alluv);

		pageResponse.setPv(pagepv);
		pageResponse.setPvRate(pagepv, allpv);

		pageResponse.setStartTime(formatDate(param.getStartTime()));
		pageResponse.setEndTime(formatDate(param.getEndTime())); // 查询当天数据里的最大时间

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

		pageResponse.setTakeOrderCount(orderCount);
		pageResponse.setOrderTotalAmount(orderAmount);
		pageResponse.setTakeOrderRate(orderCount, pageuv);

		// 设置页面描述
		pageResponse.setPageDescription(
				pageSummary.getPageSite() + ">" + pageSummary.getPageChannel() + ">" + pageSummary.getPageType());

		return pageResponse;
	}


	private String formatDate(String date) {
		try {
			DateFormat df1 = new SimpleDateFormat("yyyyMMdd");
			DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			Date d = df1.parse(date);
			return df2.format(d);
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
			return date;
		}
	}

	/**
	 * 构建pageResponse
	 * 
	 * @param value
	 * @return
	 */
	private PageResponse buildPageResponse(PageView value, AppHotMapQueryParam param) {
		PageResponse response = new PageResponse();

		response.setUv(value.getUv());

		response.setPv(value.getPv());

		response.setVistor(value.getVisits());

		response.setClick(value.getClick());
		response.setViewTime(value.getViewTime());

		response.setStartTime(formatDate(param.getStartTime()));
		response.setEndTime(formatDate(param.getEndTime()));

		return response;
	}
}
