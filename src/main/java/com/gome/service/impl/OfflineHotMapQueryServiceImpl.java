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
import java.util.Objects;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gome.Controller.model.ChannelResponse;
import com.gome.Controller.model.PageResponse;
import com.gome.Controller.model.QueryParam;
import com.gome.Controller.model.ResourceResponse;
import com.gome.dao.IKylinQueryDao;
import com.gome.dao.model.PageChannel;
import com.gome.dao.model.PageResource;
import com.gome.dao.model.PageView;
import com.gome.plus.IHotMapBaseDao;
import com.gome.plus.dao.IMysqlQueryDao;
import com.gome.service.IOfflineHotMapQueryService;

/**
 * 离线热力图查询服务实现类
 * 
 * @author wangshubao
 *
 */
@Service
public class OfflineHotMapQueryServiceImpl implements IOfflineHotMapQueryService {

	@Autowired
	private IKylinQueryDao kylinDao;
	
	@Autowired
	private IMysqlQueryDao mysqlDao;

	private static final Logger logger = LoggerFactory.getLogger(OfflineHotMapQueryServiceImpl.class);
	
	private IHotMapBaseDao getDao(QueryParam param){
		Objects.requireNonNull(param.getDataSource());
		String dataSource = param.getDataSource().toLowerCase();
		return dataSource.equals("plus-log") || dataSource.equals("plus-wap") ? mysqlDao : kylinDao;
	}

	/**
	 * 查询某个页面的汇总指标值
	 */
	@Override
	public PageResponse querySummaryOfPage(QueryParam param) {
		PageView allStationInfo = querryAllStationInfo(param);
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
	private PageView queryAllItemsForPage(QueryParam param) {
		PageView pageSummaryOfSumItem = getDao(param).querySummaryPageOfSumItem(param);
		if (pageSummaryOfSumItem == null)
			pageSummaryOfSumItem = new PageView();

		PageView pageSummaryOfNotSumItem = querySummaryPageOfNotSumItem(param);

		Long click = getDao(param).queryClickForPage(param);

		PageView pageSummaryOfOrderItem = getDao(param).querySummaryPageOfOrderItem(param);

		pageSummaryOfSumItem.setUv(pageSummaryOfNotSumItem == null ? 0 : pageSummaryOfNotSumItem.getUv());
		pageSummaryOfSumItem.setVisits(pageSummaryOfNotSumItem == null ? 0 : pageSummaryOfNotSumItem.getVisits());
		pageSummaryOfSumItem.setClick(click == null ? 0 : click);
		pageSummaryOfSumItem.setParticipationOrdersCount(
				pageSummaryOfOrderItem == null ? 0 : pageSummaryOfOrderItem.getParticipationOrdersCount());
		pageSummaryOfSumItem
				.setOrderTotalAmount(pageSummaryOfOrderItem == null ? 0 : pageSummaryOfOrderItem.getOrderTotalAmount());

		return pageSummaryOfSumItem;
	}

	/**
	 * 构建页面汇总指标的返回信息
	 * 
	 * @param allStationInfo
	 * @param pageSummary
	 * @param param
	 * @return
	 */
	private PageResponse buildPageResponse(PageView allStationInfo, PageView pageSummary, QueryParam param) {
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
	 * 查询并汇总全站数据
	 * 
	 * @param param
	 * @return
	 */
	private PageView querryAllStationInfo(QueryParam param) {
		PageView allStationPageView = getDao(param).queryAllStation(param);

		return allStationPageView;
	}

	/**
	 * 查询并sum页面汇总信息中的非sum类指标值
	 * 
	 * @param param
	 * @return
	 */
	private PageView querySummaryPageOfNotSumItem(QueryParam param) {
		PageView pageView = getDao(param).querySummaryPageOfNotSumItem(param);

		return pageView;
	}

	/**
	 * 查询某个页面的资源位信息
	 */
	@Override
	public Map<String, ResourceResponse> queryAllResourceForPage(QueryParam param, PageResponse pageSummary) {
		// 查询资源位的sum类指标
		List<PageResource> pageResourceOfSumItemList = getDao(param).queryResourcesOfSumItem(param);

		// 查询资源位的非sum类指标，需要把每天的指标值加起来
		List<PageResource> pageResourceOfNotSumItemList = getDao(param).queryResourcesOfNotSumItem(param);
		Map<String, PageResource> map = new HashMap<String, PageResource>(pageResourceOfSumItemList.size(), 1.0f);

		for (PageResource item : pageResourceOfNotSumItemList) {
			map.put(item.getCode(), item);
		}

		// 整合sum与非sum类指标，构建返回实体
		Map<String, ResourceResponse> returnMap = new HashMap<String, ResourceResponse>();
		for (PageResource sumItem : pageResourceOfSumItemList) {
			String code = sumItem.getCode();
			PageResource notSumItem = map.get(code);
			returnMap.put(code, buildResourceResponse(param, code, sumItem, notSumItem, pageSummary));
		}
		return returnMap;
	}

	/**
	 * 构建页面资源位信息的返回实体
	 * 
	 * @param param
	 * @param intcmp
	 * @param sumItem
	 * @param notSumItem
	 * @param pageSummary
	 * @return
	 */
	private ResourceResponse buildResourceResponse(QueryParam param, String intcmp, PageResource sumItem,
			PageResource notSumItem, PageResponse pageSummary) {
		ResourceResponse rs = new ResourceResponse();
		rs.setCode(intcmp);
		rs.setUv(notSumItem == null ? 0 : notSumItem.getUv());
		rs.setPv(sumItem.getPv());
		rs.setVistor(notSumItem == null ? 0 : notSumItem.getVistor());
		rs.setOrderCount(sumItem.getOrderCount());
		rs.setClick(sumItem.getClick());
		rs.setSaleAmount(sumItem.getSaleAmount());

		rs.setClickRate(rs.getClick(), pageSummary.getClick());
		rs.setVistorRate(rs.getVistor(), pageSummary.getVistor());
		rs.setUvRate(rs.getUv(), pageSummary.getUv());
		rs.setAvgPv(rs.getPv(), rs.getVistor());
		rs.setTakeOrderRate(rs.getOrderCount(), rs.getUv());
		rs.setUrl(param.getPageUrl());
		rs.setOrderAllRate(rs.getOrderCount(), notSumItem == null ? 0 : notSumItem.getOrderALLCount());

		return rs;
	}

	/**
	 * @see com.gome.service.IOfflineHotMapQueryService.queryResourceHistoryInfo
	 *      (QueryParam )
	 */
	@Override
	public Map<String, Object> queryResourceHistoryInfo(QueryParam param) {

		// 查询全站的订单量
		Long allOrderCount = getDao(param).queryAllOrderCountOfAllStation(param);
		if (allOrderCount == null)
			allOrderCount = 0L;

		// 查询页面的总uv、visits、click
		PageView pageSummaryOfNotSumItem = querySummaryPageOfNotSumItem(param);
		if (pageSummaryOfNotSumItem == null)
			pageSummaryOfNotSumItem = new PageView();

		Long pageClick = getDao(param).queryClickForPage(param);
		if (pageClick == null)
			pageClick = 0L;
		long pageUv = pageSummaryOfNotSumItem.getUv();
		long pageVisits = pageSummaryOfNotSumItem.getVisits();

		// 查询资源位的历史信息指标汇总
		PageResource totalResourceInfo = queryTotalResource(param);
		ResourceResponse totalResourceInfoResponse = buildResourceResponse(totalResourceInfo, pageUv, pageClick,
				pageVisits, allOrderCount);

		// 查询资源位的详细历史信息
		List<PageResource> historyList = queryHistoryResource(param);
		List<ResourceResponse> historyResponseList = new LinkedList<ResourceResponse>();
		if (CollectionUtils.isNotEmpty(historyList)) {
			for (PageResource item : historyList)
				historyResponseList.add(buildResourceResponse(item, pageUv, pageClick, pageVisits, allOrderCount));

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
	 * 查询资源位的历史信息指标汇总
	 * 
	 * @param param
	 * @return
	 */
	private PageResource queryTotalResource(QueryParam param) {
		// sum类指标
		PageResource totalResourceInfoOfSumItme = getDao(param).queryTotalSumItemOfIntcmp(param);
		if (totalResourceInfoOfSumItme == null)
			totalResourceInfoOfSumItme = new PageResource();

		// 非sum类指标
		PageResource totalResourceInfoOfNotSumItem = getDao(param).queryTotalNotSumItemOfIntcmp(param);
		if (totalResourceInfoOfNotSumItem != null) {
			totalResourceInfoOfSumItme.setVistor(totalResourceInfoOfNotSumItem.getVistor());
			totalResourceInfoOfSumItme.setUv(totalResourceInfoOfNotSumItem.getUv());
		}
		return totalResourceInfoOfSumItme;
	}

	/**
	 * 查询资源位的历史信息详情指标
	 * 
	 * @param param
	 * @return
	 */
	private List<PageResource> queryHistoryResource(QueryParam param) {

		final String SPLIT = String.valueOf('\001');

		// 查询非sum类指标
		List<PageResource> historyOfNotSumItemList = getDao(param).queryHistoryOfNotSumItemForIntcmp(param);
		Map<String, PageResource> map = new HashMap<String, PageResource>();
		if (CollectionUtils.isNotEmpty(historyOfNotSumItemList)) {
			for (PageResource item : historyOfNotSumItemList) {
				String url = item.getUrl();
				String pageTitle = item.getTitle();
				String key = url + SPLIT + pageTitle;

				map.put(key, item);
			}
		}

		// 查询sum类指标
		List<PageResource> historyOfSumItemList = getDao(param).queryHistoryOfSumItemForIntcmp(param);
		// 整合
		if (CollectionUtils.isNotEmpty(historyOfSumItemList)) {
			for (PageResource item : historyOfSumItemList) {
				String url = item.getUrl();
				String pageTitle = item.getTitle();
				String key = url + SPLIT + pageTitle;

				PageResource val = map.get(key);
				if (val != null) {
					item.setUv(val.getUv());
					item.setVistor(val.getVistor());
				}
			}
		}

		return historyOfSumItemList;
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
	private ResourceResponse buildResourceResponse(PageResource resourceInfo, double syUv, double syClick,
			double syVisitors, double allOrderCount) {
		ResourceResponse intCmpSummaryResp = new ResourceResponse();

		intCmpSummaryResp.setUrl(resourceInfo.getUrl());
		intCmpSummaryResp.setTitle(resourceInfo.getTitle());
		intCmpSummaryResp.setCode(resourceInfo.getCode());
		intCmpSummaryResp.setUv(resourceInfo.getUv());
		intCmpSummaryResp.setPv(resourceInfo.getPv());
		intCmpSummaryResp.setVistor(resourceInfo.getVistor());
		intCmpSummaryResp.setOrderCount(resourceInfo.getOrderCount());
		intCmpSummaryResp.setClick(resourceInfo.getClick());
		intCmpSummaryResp.setSaleAmount(resourceInfo.getSaleAmount());
		intCmpSummaryResp.setTakeOrderRate(resourceInfo.getOrderCount(), resourceInfo.getUv());

		intCmpSummaryResp.setUvRate(resourceInfo.getUv(), syUv);
		intCmpSummaryResp.setClickRate(resourceInfo.getClick(), syClick);
		intCmpSummaryResp.setVistorRate(resourceInfo.getVistor(), syVisitors);
		intCmpSummaryResp.setAvgPv(resourceInfo.getPv(), resourceInfo.getVistor());
		intCmpSummaryResp.setOrderAllRate(resourceInfo.getOrderCount(), allOrderCount);

		return intCmpSummaryResp;
	}

	/**
	 * 按小时分组的图表查询
	 */
	@Override
	public Map<String, PageResponse> chartQueryOfHour(QueryParam param) {
		List<PageView> pageSummaryOfHourList = getDao(param).queryPageFullItemOfHour(param);

		List<PageView> pageClickOfHourList = getDao(param).queryClickOfHour(param);

		List<PageView> pageOrderItemOfHourList = getDao(param).queryPageOrderItemOfHour(param);

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

		if (CollectionUtils.isNotEmpty(pageOrderItemOfHourList)) {
			for (PageView item : pageOrderItemOfHourList) {
				PageView val = map.get(item.getCode());
				if (val != null) {
					val.setParticipationOrdersCount(item.getParticipationOrdersCount());
					val.setOrderTotalAmount(item.getOrderTotalAmount());
				} else {
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

	/**
	 * 按天查询图表数据
	 */
	@Override
	public Map<String, PageResponse> chartQueryOfDay(QueryParam param) {
		List<PageView> pageSummaryOfDayList = getDao(param).queryPageFullItemOfDay(param);

		List<PageView> pageClickOfDayList = getDao(param).queryClickOfDay(param);

		List<PageView> pageOrderItemOfDayList = getDao(param).queryPageOrderItemOfDay(param);

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

		if (CollectionUtils.isNotEmpty(pageOrderItemOfDayList)) {
			for (PageView item : pageOrderItemOfDayList) {
				PageView val = map.get(item.getCode());
				if (val != null) {
					val.setParticipationOrdersCount(item.getParticipationOrdersCount());
					val.setOrderTotalAmount(item.getOrderTotalAmount());
				} else {
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
	 * 按渠道查询图表数据
	 */
	@Override
	public Map<String, ChannelResponse> chartQueryOfChannel(QueryParam param) {
		List<PageChannel> pageSumItemOfChannelList = getDao(param).queryPageSumItemOfChannel(param);
		Map<String, PageChannel> map = new HashMap<String, PageChannel>();
		if (CollectionUtils.isNotEmpty(pageSumItemOfChannelList)) {
			for (PageChannel item : pageSumItemOfChannelList) {
				map.put(item.getChannelCode(), item);
			}
		}

		List<PageChannel> pageNotSumItemOfChannelList = getDao(param).queryPageNotSumItemOfChannel(param);
		if (CollectionUtils.isNotEmpty(pageNotSumItemOfChannelList)) {
			for (PageChannel item : pageNotSumItemOfChannelList) {
				PageChannel val = map.get(item.getChannelCode());
				if (val != null) {
					val.setVistor(item.getVistor());
					val.setUv(item.getUv());
				} else {
					map.put(item.getChannelCode(), item);
				}
			}
		}

		List<PageChannel> pageClickOfChannelList = getDao(param).queryClickOfChannel(param);
		if (CollectionUtils.isNotEmpty(pageClickOfChannelList)) {
			for (PageChannel item : pageClickOfChannelList) {
				PageChannel val = map.get(item.getChannelCode());
				if (val != null) {
					val.setClick(item.getClick());
				} else {
					map.put(item.getChannelCode(), item);
				}
			}
		}

		List<PageChannel> pageOrderItemOfChannelList = getDao(param).queryPageOrderItemOfChannel(param);
		if (CollectionUtils.isNotEmpty(pageOrderItemOfChannelList)) {
			for (PageChannel item : pageOrderItemOfChannelList) {
				PageChannel val = map.get(item.getChannelCode());
				if (val != null) {
					val.setParticipationOrdersCount(item.getParticipationOrdersCount());
					val.setOrderTotalAmount(item.getOrderTotalAmount());
				} else {
					map.put(item.getChannelCode(), item);
				}
			}
		}

		Map<String, ChannelResponse> responseMap = new HashMap<String, ChannelResponse>();
		for (Map.Entry<String, PageChannel> entry : map.entrySet()) {
			responseMap.put(entry.getKey(), buildChannelResponse(entry.getValue()));
		}
		return responseMap;
	}

	/**
	 * 构建渠道的图表查询返回数据
	 * 
	 * @param value
	 * @return
	 */
	private ChannelResponse buildChannelResponse(PageChannel pageChannel) {
		ChannelResponse channelResp = new ChannelResponse();

		channelResp.setCode(pageChannel.getChannelCode());

		channelResp.setUv(pageChannel.getUv());

		channelResp.setPv(pageChannel.getPv());

		channelResp.setVistor(pageChannel.getVistor());

		channelResp.setEntriesCount(pageChannel.getEntriesCount());
		channelResp.setBounce(pageChannel.getBounce(), pageChannel.getEntriesCount());

		channelResp.setExitCount(pageChannel.getExitCount());
		channelResp.setExitRate(pageChannel.getExitCount(), pageChannel.getVistor());

		channelResp.setClick(pageChannel.getClick());
		channelResp.setViewTime(pageChannel.getViewTime());
		channelResp.setTakeOrderRate(pageChannel.getParticipationOrdersCount(), pageChannel.getUv());

		channelResp.setTakeOrderCount(pageChannel.getParticipationOrdersCount());
		channelResp.setOrderTotalAmount(pageChannel.getOrderTotalAmount());

		return channelResp;
	}

	/**
	 * 构建pageResponse
	 * 
	 * @param value
	 * @return
	 */
	private PageResponse buildPageResponse(PageView value, QueryParam param) {
		PageResponse response = new PageResponse();

		response.setUv(value.getUv());

		response.setPv(value.getPv());

		response.setVistor(value.getVisits());

		response.setEntriesCount(value.getEntriesCount());
		response.setBounce(value.getBounce(), value.getEntriesCount());

		response.setExitCount(value.getExitCount());
		response.setExitRate(value.getExitCount(), value.getVisits());

		response.setClick(value.getClick());
		response.setViewTime(value.getViewTime());
		response.setTakeOrderRate(value.getParticipationOrdersCount(), value.getUv());

		response.setTakeOrderCount(value.getParticipationOrdersCount());
		response.setOrderTotalAmount(value.getOrderTotalAmount());

		response.setStartTime(formatDate(param.getStartTime()));
		response.setEndTime(formatDate(param.getEndTime()));

		return response;
	}

}
