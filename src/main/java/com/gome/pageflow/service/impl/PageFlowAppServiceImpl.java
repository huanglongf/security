package com.gome.pageflow.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gome.pageflow.dao.PageFlowAppDao;
import com.gome.pageflow.model.PageFlow;
import com.gome.pageflow.model.PageFlowQueryParams;
import com.gome.pageflow.model.PageFlowTable;
import com.gome.pageflow.service.PageFlowAppService;
import com.gome.pageflowplus.dao.PageFlowAppPlusDao;

@Service
public class PageFlowAppServiceImpl implements PageFlowAppService {

	private Logger logger = Logger.getLogger(PageFlowAppServiceImpl.class);
	@Autowired
	private PageFlowAppDao dao;
	
	@Autowired
	private PageFlowAppPlusDao plusDao;

	@Override
	public Map<String, Object> queryTopN(PageFlowQueryParams params) {

		Map<String, Object> map = new LinkedHashMap<String, Object>();

		List<String> urls = dao.queryPvTopN(params);

		if (urls != null && !urls.isEmpty()) {

			Long total = dao.querytotalCountTopN(params);

			long recordsTotal = 0;

			if (total != null && total > 0) {
				recordsTotal = total;
			}

			map.put("recordsTotal", recordsTotal);
			map.put("recordsFiltered", recordsTotal);

			Map<String, Object> param = new HashMap<String, Object>();
			param.put("urls", urls);
			param.put("startTime", params.getStartTime());
			param.put("endTime", params.getEndTime());
			param.put("dataSource", params.getDataSource());
			param.put("pageStart", params.getPageStart());
			param.put("pageSize", params.getPageSize());
			param.put("dim", params.getDim());
			param.put("orderField", params.getOrderField());

			List<PageFlow> pageAllStations = dao.queryAllStation(params);

			List<PageFlowTable> flowAllTables = new LinkedList<PageFlowTable>();

			for (PageFlow pageflow : pageAllStations) {
				pageflow.setAvgVisitTime(pageflow.getViewTime(), pageflow.getPv());
				pageflow.setClickRate(pageflow.getClick(), pageflow.getPv());
				pageflow.setExitsRate(pageflow.getExits(), pageflow.getExits());
				pageflow.setPerClick(pageflow.getClick(), pageflow.getUv());
				pageflow.setTakeRate(pageflow.getTakeOrdercount(), pageflow.getUv());
				// 全站跳出率比的是访问
				pageflow.setBouncesRate(pageflow.getBounces(), pageflow.getEntries());
				pageflow.setCheckbox(true);

				//为了防止计算是访问是近似值的误差。用登入次数替代
				pageflow.setEntriesRate(pageflow.getEntries(), pageflow.getEntries());

				PageFlowTable table = pageFlowTotal(pageflow);

				flowAllTables.add(table);
			}

			List<PageFlow> pageflows = dao.queryAllTopN(param);

			if (pageflows == null) {
				pageflows = new LinkedList<PageFlow>();
			}

			Map<String, Long> clickMap = new HashMap<String, Long>();

			for (PageFlow pageflow : pageflows) {

				long pageClick = clickMap.get(pageflow.getPageContent()) == null ? 0
						: clickMap.get(pageflow.getPageContent());

				pageflow.setClick(pageClick);
				pageflow.setAvgVisitTime(pageflow.getViewTime(), pageflow.getPv());
				pageflow.setClickRate(pageflow.getClick(), pageflow.getPv());
				pageflow.setExitsRate(pageflow.getExits(), pageflow.getVisitor());
				pageflow.setPerClick(pageflow.getClick(), pageflow.getUv());
				pageflow.setTakeRate(pageflow.getTakeOrdercount(), pageflow.getUv());
				pageflow.setBouncesRate(pageflow.getBounces(), pageflow.getEntries());
				pageflow.setEntriesRate(pageflow.getEntries(), pageflow.getVisitor());

				PageFlowTable table = pageFlowSub(pageflow, pageAllStations.get(0));
				flowAllTables.add(table);

			}

			// pageAllStations.addAll(pageflows);

			map.put("data", flowAllTables);
		} else {

			map.put("recordsTotal", 0);
			map.put("recordsFiltered", 0);
			map.put("data", new ArrayList<PageFlowTable>());

		}
		return map;

	}

	@Override
	public Map<String, Object> queryChart(PageFlowQueryParams params) {

		String[] urls = {};
		if (StringUtils.isNotBlank(params.getPageContent())) {

			urls = params.getPageContent().split(",");
		}

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("urls", urls);
		paramMap.put("startTime", params.getStartTime());
		paramMap.put("endTime", params.getEndTime());
		paramMap.put("dataSource", params.getDataSource());
		paramMap.put("pageStart", params.getPageStart());
		paramMap.put("pageSize", params.getPageSize());
		paramMap.put("dim", params.getDim());
		paramMap.put("showHour", params.getShowHour());

		String hourStartTime = params.getStartTime();
		if ("hour".equals(params.getShowHour().toLowerCase())) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			try {
				hourStartTime = df.format(dateFormat.parse(hourStartTime)) + " 00";
			} catch (ParseException e) {

				logger.error(" date format error ", e);
			}
		}

		paramMap.put("hourFilter", hourStartTime);

		params.setHourFilter(hourStartTime);

		List<PageFlow> ALlstationChart = dao.queryAllStationGroup(params);

		// 计算全站的占比
		for (PageFlow pageflow : ALlstationChart) {

			pageflow.setAvgVisitTime(pageflow.getViewTime(), pageflow.getPv());
			pageflow.setClickRate(pageflow.getClick(), pageflow.getPv());
			pageflow.setExitsRate(pageflow.getExits(), pageflow.getVisitor());
			pageflow.setPerClick(pageflow.getClick(), pageflow.getUv());
			pageflow.setTakeRate(pageflow.getTakeOrdercount(), pageflow.getUv());
			pageflow.setBouncesRate(pageflow.getBounces(), pageflow.getEntries());
			pageflow.setEntriesRate(pageflow.getEntries(), pageflow.getVisitor());

		}

		List<PageFlow> pageflows = null;

		if (urls.length > 0) {
			pageflows = dao.queryPage(paramMap);
		}

		if (pageflows == null || pageflows.isEmpty() || pageflows.get(0) == null) {
			pageflows = new LinkedList<PageFlow>();
		}

		Map<String, List<PageFlow>> pageflowMap = new HashMap<String, List<PageFlow>>();

		for (PageFlow pageflow : pageflows) {

			String key = pageflow.getPageContent();

			pageflow.setClick(0);
			pageflow.setAvgVisitTime(pageflow.getViewTime(), pageflow.getPv());
			pageflow.setClickRate(pageflow.getClick(), pageflow.getPv());
			pageflow.setExitsRate(pageflow.getExits(), pageflow.getVisitor());
			pageflow.setPerClick(pageflow.getClick(), pageflow.getUv());
			pageflow.setTakeRate(pageflow.getTakeOrdercount(), pageflow.getUv());
			pageflow.setBouncesRate(pageflow.getBounces(), pageflow.getEntries());
			pageflow.setEntriesRate(pageflow.getEntries(), pageflow.getVisitor());

			if (pageflowMap.containsKey(key)) {

				List<PageFlow> pfs = pageflowMap.get(key);

				pfs.add(pageflow);

				pageflowMap.put(key, pfs);

			} else {
				List<PageFlow> pfs = new LinkedList<PageFlow>();

				pfs.add(pageflow);

				pageflowMap.put(key, pfs);
			}

		}

		Map<String, Object> map = new LinkedHashMap<String, Object>();

		Collections.sort(ALlstationChart, new Comparator<PageFlow>() {

			@Override
			public int compare(PageFlow o1, PageFlow o2) {

				String o1Date = o1.getDate();
				String o2Date = o2.getDate();

				if (StringUtils.isNotEmpty(o1Date) && StringUtils.isNotEmpty(o2Date)) {

					int a = Integer.parseInt(o1Date.replace(" ", "").replace("-", ""));

					int b = Integer.parseInt(o2Date.replace(" ", "").replace("-", ""));

					if (a > b) {
						return 1;
					} else if (a < b) {
						return -1;
					}
				}

				return 0;
			}

		});

		map.put("合计", ALlstationChart);

		for (String key : pageflowMap.keySet()) {

			List<PageFlow> lists = pageflowMap.get(key);

			Collections.sort(lists, new Comparator<PageFlow>() {

				@Override
				public int compare(PageFlow o1, PageFlow o2) {

					String o1Date = o1.getDate();
					String o2Date = o2.getDate();

					if (StringUtils.isNotEmpty(o1Date) && StringUtils.isNotEmpty(o2Date)) {

						int a = Integer.parseInt(o1Date.replace(" ", "").replace("-", ""));

						int b = Integer.parseInt(o2Date.replace(" ", "").replace("-", ""));

						if (a > b) {
							return 1;
						} else if (a < b) {
							return -1;
						}
					}

					return 0;
				}

			});

			map.put(key, lists);
		}

		return map;
	}

	@Override
	public Map<String, Object> querySearch(PageFlowQueryParams params) {

		String[] urls = {};
		if (StringUtils.isNoneEmpty(params.getSearch())) {
			urls = params.getSearch().replace(" ", "").split(",");
		}

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("urls", urls);
		paramMap.put("startTime", params.getStartTime());
		paramMap.put("endTime", params.getEndTime());
		paramMap.put("dataSource", params.getDataSource());
		paramMap.put("pageStart", params.getPageStart());
		paramMap.put("pageSize", params.getPageSize());
		paramMap.put("dim", params.getDim());
		paramMap.put("orderField", params.getOrderField());

		Map<String, Object> map = new LinkedHashMap<String, Object>();

		if (urls != null && urls.length > 0) {

			Long total = dao.querySearchCount(paramMap);

			long recordsTotal = 0;

			if (total != null && total > 0) {
				recordsTotal = total;
			}

			map.put("recordsTotal", recordsTotal);
			map.put("recordsFiltered", recordsTotal);

			List<PageFlow> pageSearchALL = dao.querySearchPageTotal(paramMap);

			if (pageSearchALL == null || pageSearchALL.isEmpty() || pageSearchALL.get(0) == null) {
				pageSearchALL = new LinkedList<PageFlow>();
			}

			List<PageFlow> pageAllStationList = dao.queryAllStation(params);

			PageFlow allStation = null;

			if (pageAllStationList != null && pageAllStationList.size() > 0) {
				allStation = pageAllStationList.get(0);
			}

			List<PageFlowTable> flowAllTables = new LinkedList<PageFlowTable>();

			long click = 0;

			for (PageFlow pageflow : pageSearchALL) {

				pageflow.setClick(click);
				pageflow.setAvgVisitTime(pageflow.getViewTime(), pageflow.getPv());
				pageflow.setClickRate(pageflow.getClick(), pageflow.getPv());
				pageflow.setExitsRate(pageflow.getExits(), pageflow.getVisitor());
				pageflow.setPerClick(pageflow.getClick(), pageflow.getUv());
				pageflow.setTakeRate(pageflow.getTakeOrdercount(), pageflow.getUv());
				pageflow.setBouncesRate(pageflow.getBounces(), pageflow.getEntries());
				pageflow.setCheckbox(true);
				pageflow.setEntriesRate(pageflow.getEntries(), pageflow.getVisitor());

				PageFlowTable table = pageFlowSubSearchTotal(pageflow, allStation);

				flowAllTables.add(table);

			}

			List<PageFlow> pageflows = dao.queryAllTopN(paramMap);

			if (pageflows == null) {
				pageflows = new LinkedList<PageFlow>();
			}

			Map<String, Long> clickMap = new HashMap<String, Long>();

			for (PageFlow pageflow : pageflows) {
				String key = pageflow.getPageContent();

				long pageClick = clickMap.get(key) == null ? 0 : clickMap.get(key);

				pageflow.setClick(pageClick);
				pageflow.setAvgVisitTime(pageflow.getViewTime(), pageflow.getPv());
				pageflow.setClickRate(pageflow.getClick(), pageflow.getPv());
				pageflow.setExitsRate(pageflow.getExits(), pageflow.getVisitor());
				pageflow.setPerClick(pageflow.getClick(), pageflow.getUv());
				pageflow.setTakeRate(pageflow.getTakeOrdercount(), pageflow.getUv());
				pageflow.setBouncesRate(pageflow.getBounces(), pageflow.getEntries());
				pageflow.setEntriesRate(pageflow.getEntries(), pageflow.getVisitor());
				PageFlowTable table = pageFlowSub(pageflow, allStation);

				flowAllTables.add(table);
			}

			map.put("data", flowAllTables);

		} else {

			map.put("recordsTotal", 0);
			map.put("recordsFiltered", 0);
			map.put("data", new ArrayList<PageFlowTable>());

		}
		return map;

	}

	@Override
	public Map<String, Object> querySearchChart(PageFlowQueryParams params) {

		String[] urls = {};

		if (StringUtils.isNotEmpty(params.getSearch())) {
			urls = params.getSearch().replace(" ", "").split(",");

		}

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("urls", urls);
		paramMap.put("startTime", params.getStartTime());
		paramMap.put("endTime", params.getEndTime());
		paramMap.put("dataSource", params.getDataSource());
		paramMap.put("pageStart", params.getPageStart());
		paramMap.put("pageSize", params.getPageSize());
		paramMap.put("dim", params.getDim());
		paramMap.put("showHour", params.getShowHour());
		paramMap.put("pageContent", params.getPageContent());

		String hourStartTime = params.getStartTime();
		if ("hour".equals(params.getShowHour().toLowerCase())) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			try {
				hourStartTime = df.format(dateFormat.parse(hourStartTime)) + " 00";
			} catch (ParseException e) {

				logger.error(" date format error ", e);
			}
		}

		paramMap.put("hourFilter", hourStartTime);

		params.setHourFilter(hourStartTime);
		List<PageFlow> pageallStations = dao.querySearchPageChart(paramMap);

		if (StringUtils.isNotEmpty(params.getPageContent())) {
			urls = params.getPageContent().split(",");
			paramMap.put("urls", urls);
		}

		List<PageFlow> pageflows = dao.queryPage(paramMap);

		if (pageflows == null) {
			pageflows = new LinkedList<PageFlow>();
		}

		Map<String, Long> clickMap = new HashMap<String, Long>();

		for (PageFlow pageflow : pageallStations) {

			pageflow.setClick(0);
			pageflow.setAvgVisitTime(pageflow.getViewTime(), pageflow.getPv());
			pageflow.setClickRate(pageflow.getClick(), pageflow.getPv());
			pageflow.setExitsRate(pageflow.getExits(), pageflow.getVisitor());
			pageflow.setPerClick(pageflow.getClick(), pageflow.getUv());
			pageflow.setTakeRate(pageflow.getTakeOrdercount(), pageflow.getUv());
			pageflow.setBouncesRate(pageflow.getBounces(), pageflow.getEntries());
			pageflow.setEntriesRate(pageflow.getEntries(), pageflow.getVisitor());

		}

		Map<String, List<PageFlow>> pageflowMap = new HashMap<String, List<PageFlow>>();

		for (PageFlow pageflow : pageflows) {

			String keyClick = pageflow.getPageContent() + "_" + pageflow.getDate();
			String key = pageflow.getPageContent();

			long pageClick = clickMap.get(keyClick) == null ? 0 : clickMap.get(keyClick);

			pageflow.setClick(pageClick);

			pageflow.setAvgVisitTime(pageflow.getViewTime(), pageflow.getPv());
			pageflow.setClickRate(pageflow.getClick(), pageflow.getPv());
			pageflow.setExitsRate(pageflow.getExits(), pageflow.getVisitor());
			pageflow.setPerClick(pageflow.getClick(), pageflow.getUv());
			pageflow.setTakeRate(pageflow.getTakeOrdercount(), pageflow.getUv());
			pageflow.setBouncesRate(pageflow.getBounces(), pageflow.getEntries());
			pageflow.setEntriesRate(pageflow.getEntries(), pageflow.getVisitor());

			if (pageflowMap.containsKey(key)) {
				List<PageFlow> pfs = pageflowMap.get(key);
				pfs.add(pageflow);
				pageflowMap.put(key, pfs);
			} else {
				List<PageFlow> pfs = new LinkedList<PageFlow>();
				pfs.add(pageflow);
				pageflowMap.put(key, pfs);
			}

		}

		Map<String, Object> map = new LinkedHashMap<String, Object>();

		Collections.sort(pageallStations, new Comparator<PageFlow>() {

			@Override
			public int compare(PageFlow o1, PageFlow o2) {

				String o1Date = o1.getDate();
				String o2Date = o2.getDate();

				if (StringUtils.isNotEmpty(o1Date) && StringUtils.isNotEmpty(o2Date)) {

					int a = Integer.parseInt(o1Date.replace(" ", "").replace("-", ""));

					int b = Integer.parseInt(o2Date.replace(" ", "").replace("-", ""));

					if (a > b) {
						return 1;
					} else if (a < b) {
						return -1;
					}
				}

				return 0;
			}

		});

		map.put("合计", pageallStations);

		for (String key : pageflowMap.keySet()) {

			List<PageFlow> lists = pageflowMap.get(key);

			Collections.sort(lists, new Comparator<PageFlow>() {

				@Override
				public int compare(PageFlow o1, PageFlow o2) {

					String o1Date = o1.getDate();
					String o2Date = o2.getDate();

					if (StringUtils.isNotEmpty(o1Date) && StringUtils.isNotEmpty(o2Date)) {

						int a = Integer.parseInt(o1Date.replace(" ", "").replace("-", ""));

						int b = Integer.parseInt(o2Date.replace(" ", "").replace("-", ""));

						if (a > b) {
							return 1;
						} else if (a < b) {
							return -1;
						}
					}

					return 0;
				}

			});

			map.put(key, lists);
		}
		return map;

	}

	/**
	 * 
	 * 
	 * @param pageFlow
	 * @param totalPageFlow
	 * @return
	 */

	public PageFlowTable pageFlowSub(PageFlow pageFlow, PageFlow totalPageFlow) {
		PageFlowTable pageFlowTable = new PageFlowTable();
		pageFlowTable.setAvgVisitTime(pageFlow.getAvgVisitTime(), false);
		pageFlowTable.setBounces(pageFlow.getBounces(), totalPageFlow.getBounces(), false);
		pageFlowTable.setBouncesRate(pageFlow.getBouncesRate() + "", false);
		pageFlowTable.setClick(pageFlow.getClick(), false);
		pageFlowTable.setClickRate(pageFlow.getClickRate() + "", false);
		pageFlowTable.setDate(pageFlow.getDate());
		pageFlowTable.setEntries(pageFlow.getEntries(), totalPageFlow.getEntries(), false);

		pageFlowTable.setExits(pageFlow.getExits(), totalPageFlow.getExits(), false);
		pageFlowTable.setExitsRate(pageFlow.getExitsRate() + "", false);
		pageFlowTable.setPageContent(pageFlow.getPageContent(), false);
		pageFlowTable.setPerClick(pageFlow.getPerClick(), false);
		pageFlowTable.setPv(pageFlow.getPv(), totalPageFlow.getPv(), false);
		pageFlowTable.setTakeOrderAmount(pageFlow.getTakeOrderAmount(), totalPageFlow.getTakeOrderAmount(), false);
		pageFlowTable.setTakeOrdercount(pageFlow.getTakeOrdercount(), totalPageFlow.getTakeOrdercount(), false);
		pageFlowTable.setTakeRate(pageFlow.getTakeRate() + "", false);
		pageFlowTable.setUv(pageFlow.getUv(), totalPageFlow.getUv(), false);
		pageFlowTable.setViewTime(pageFlow.getViewTime() + "", false);
		pageFlowTable.setVisitor(pageFlow.getVisitor(), totalPageFlow.getVisitor(), false);
		pageFlowTable.setEntriesRate(pageFlow.getEntriesRate() + "", false);
		pageFlowTable.setCheckbox(pageFlow.getCheckbox());

		return pageFlowTable;
	}

	/**
	 * 
	 * 
	 * @param pageFlow
	 * @param totalPageFlow
	 * @return
	 */

	public PageFlowTable pageFlowSubSearchTotal(PageFlow pageFlow, PageFlow totalPageFlow) {
		PageFlowTable pageFlowTable = new PageFlowTable();

		pageFlowTable.setRateString("<br/>占全站的百分比<br/>", true);

		pageFlowTable.setAvgVisitTime(pageFlow.getAvgVisitTime(), true);
		pageFlowTable.setBounces(pageFlow.getBounces(), totalPageFlow.getBounces(), true);
		pageFlowTable.setBouncesRate(pageFlow.getBouncesRate() + "", true);
		pageFlowTable.setClick(pageFlow.getClick(), true);
		pageFlowTable.setClickRate(pageFlow.getClickRate() + "", true);
		pageFlowTable.setDate(pageFlow.getDate());
		pageFlowTable.setEntries(pageFlow.getEntries(), totalPageFlow.getEntries(), true);

		pageFlowTable.setExits(pageFlow.getExits(), totalPageFlow.getExits(), true);
		pageFlowTable.setExitsRate(pageFlow.getExitsRate() + "", true);
		pageFlowTable.setPageContent(pageFlow.getPageContent(), true);
		pageFlowTable.setPerClick(pageFlow.getPerClick(), true);
		pageFlowTable.setPv(pageFlow.getPv(), totalPageFlow.getPv(), true);
		pageFlowTable.setTakeOrderAmount(pageFlow.getTakeOrderAmount(), totalPageFlow.getTakeOrderAmount(), true);
		pageFlowTable.setTakeOrdercount(pageFlow.getTakeOrdercount(), totalPageFlow.getTakeOrdercount(), true);
		pageFlowTable.setTakeRate(pageFlow.getTakeRate() + "", true);
		pageFlowTable.setUv(pageFlow.getUv(), totalPageFlow.getUv(), true);
		pageFlowTable.setViewTime(pageFlow.getViewTime() + "", true);
		pageFlowTable.setVisitor(pageFlow.getVisitor(), totalPageFlow.getVisitor(), true);
		pageFlowTable.setEntriesRate(pageFlow.getEntriesRate() + "", true);
		pageFlowTable.setCheckbox(pageFlow.getCheckbox());

		return pageFlowTable;
	}

	/**
	 * 
	 * 
	 * @param pageFlow
	 * @param totalPageFlow
	 * @return
	 */

	public PageFlowTable pageFlowTotal(PageFlow pageFlow) {

		PageFlowTable pageFlowTable = new PageFlowTable();

		pageFlowTable.setRateString("<br/>占全站的百分比<br/>", true);
		pageFlowTable.setAvgVisitTime(pageFlow.getAvgVisitTime(), true);
		pageFlowTable.setBounces(pageFlow.getBounces(), pageFlow.getBounces(), true);
		pageFlowTable.setBouncesRate(pageFlow.getBouncesRate() + "", true);

		pageFlowTable.setClick(pageFlow.getClick(), true);
		pageFlowTable.setClickRate(pageFlow.getClickRate() + "", true);
		pageFlowTable.setDate(pageFlow.getDate());
		pageFlowTable.setEntries(pageFlow.getEntries(), pageFlow.getEntries(), true);
		pageFlowTable.setExits(pageFlow.getExits(), pageFlow.getExits(), true);
		pageFlowTable.setExitsRate(pageFlow.getExitsRate() + "", true);
		pageFlowTable.setPageContent(pageFlow.getPageContent(), true);
		pageFlowTable.setPerClick(pageFlow.getPerClick(), true);
		pageFlowTable.setPv(pageFlow.getPv(), pageFlow.getPv(), true);
		pageFlowTable.setTakeOrderAmount(pageFlow.getTakeOrderAmount(), pageFlow.getTakeOrderAmount(), true);
		pageFlowTable.setTakeOrdercount(pageFlow.getTakeOrdercount(), pageFlow.getTakeOrdercount(), true);
		pageFlowTable.setTakeRate(pageFlow.getTakeRate() + "", true);
		pageFlowTable.setUv(pageFlow.getUv(), pageFlow.getUv(), true);
		pageFlowTable.setViewTime(pageFlow.getViewTime() + "", true);
		pageFlowTable.setVisitor(pageFlow.getVisitor(), pageFlow.getVisitor(), true);
		pageFlowTable.setEntriesRate(pageFlow.getEntriesRate() + "", true);
		pageFlowTable.setCheckbox(pageFlow.getCheckbox());

		return pageFlowTable;
	}

	@Override
	public boolean valid(PageFlowQueryParams queryParams) {
		logger.info(" queryParams : " + queryParams.toString());

		if (StringUtils.isEmpty(queryParams.getDataSource())) {

			logger.error(" dataSource is null");
			return false;
		}

		if (StringUtils.isEmpty(queryParams.getStartTime())) {
			logger.error("s tartTime is null ");

			return false;
		}

		if (StringUtils.isEmpty(queryParams.getEndTime())) {
			logger.error(" endTime is null");

			return false;
		}

		if (StringUtils.isEmpty(queryParams.getShowHour())) {
			queryParams.setShowHour("day");
		}

		if (StringUtils.isEmpty(queryParams.getDim())) {

			queryParams.setDim("prefixurl");
		} else {
			queryParams.setDim(queryParams.getDim().toLowerCase());
		}

		return true;

	}
	

	@Override
	public Map<String, Object> queryPlusChart(PageFlowQueryParams params) {

		String[] urls = {};
		if (StringUtils.isNotBlank(params.getPageContent())) {

			urls = params.getPageContent().split(",");
		}

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("urls", urls);
		paramMap.put("startTime", params.getStartTime());
		paramMap.put("endTime", params.getEndTime());
		paramMap.put("dataSource", params.getDataSource());
		paramMap.put("pageStart", params.getPageStart());
		paramMap.put("pageSize", params.getPageSize());
		paramMap.put("dim", params.getDim());
		paramMap.put("showHour", params.getShowHour());

		String hourStartTime = params.getStartTime();
		if ("hour".equals(params.getShowHour().toLowerCase())) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			try {
				hourStartTime = df.format(dateFormat.parse(hourStartTime)) + " 00";
			} catch (ParseException e) {

				logger.error(" date format error ", e);
			}
		}

		paramMap.put("hourFilter", hourStartTime);

		params.setHourFilter(hourStartTime);

		List<PageFlow> ALlstationChart = plusDao.queryAllStationGroup(params);

		// 计算全站的占比
		for (PageFlow pageflow : ALlstationChart) {

			pageflow.setAvgVisitTime(pageflow.getViewTime(), pageflow.getPv());
			pageflow.setClickRate(pageflow.getClick(), pageflow.getPv());
			pageflow.setExitsRate(pageflow.getExits(), pageflow.getVisitor());
			pageflow.setPerClick(pageflow.getClick(), pageflow.getUv());
			pageflow.setTakeRate(pageflow.getTakeOrdercount(), pageflow.getUv());
			pageflow.setBouncesRate(pageflow.getBounces(), pageflow.getEntries());
			pageflow.setEntriesRate(pageflow.getEntries(), pageflow.getVisitor());

		}

		List<PageFlow> pageflows = null;

		if (urls.length > 0) {
			pageflows = plusDao.queryPage(paramMap);
		}

		if (pageflows == null || pageflows.isEmpty() || pageflows.get(0) == null) {
			pageflows = new LinkedList<PageFlow>();
		}

		Map<String, List<PageFlow>> pageflowMap = new HashMap<String, List<PageFlow>>();

		for (PageFlow pageflow : pageflows) {

			String key = pageflow.getPageContent();

			pageflow.setClick(0);
			pageflow.setAvgVisitTime(pageflow.getViewTime(), pageflow.getPv());
			pageflow.setClickRate(pageflow.getClick(), pageflow.getPv());
			pageflow.setExitsRate(pageflow.getExits(), pageflow.getVisitor());
			pageflow.setPerClick(pageflow.getClick(), pageflow.getUv());
			pageflow.setTakeRate(pageflow.getTakeOrdercount(), pageflow.getUv());
			pageflow.setBouncesRate(pageflow.getBounces(), pageflow.getEntries());
			pageflow.setEntriesRate(pageflow.getEntries(), pageflow.getVisitor());

			if (pageflowMap.containsKey(key)) {

				List<PageFlow> pfs = pageflowMap.get(key);

				pfs.add(pageflow);

				pageflowMap.put(key, pfs);

			} else {
				List<PageFlow> pfs = new LinkedList<PageFlow>();

				pfs.add(pageflow);

				pageflowMap.put(key, pfs);
			}

		}

		Map<String, Object> map = new LinkedHashMap<String, Object>();

		Collections.sort(ALlstationChart, new Comparator<PageFlow>() {

			@Override
			public int compare(PageFlow o1, PageFlow o2) {

				String o1Date = o1.getDate();
				String o2Date = o2.getDate();

				if (StringUtils.isNotEmpty(o1Date) && StringUtils.isNotEmpty(o2Date)) {

					int a = Integer.parseInt(o1Date.replace(" ", "").replace("-", ""));

					int b = Integer.parseInt(o2Date.replace(" ", "").replace("-", ""));

					if (a > b) {
						return 1;
					} else if (a < b) {
						return -1;
					}
				}

				return 0;
			}

		});

		map.put("合计", ALlstationChart);

		for (String key : pageflowMap.keySet()) {

			List<PageFlow> lists = pageflowMap.get(key);

			Collections.sort(lists, new Comparator<PageFlow>() {

				@Override
				public int compare(PageFlow o1, PageFlow o2) {

					String o1Date = o1.getDate();
					String o2Date = o2.getDate();

					if (StringUtils.isNotEmpty(o1Date) && StringUtils.isNotEmpty(o2Date)) {

						int a = Integer.parseInt(o1Date.replace(" ", "").replace("-", ""));

						int b = Integer.parseInt(o2Date.replace(" ", "").replace("-", ""));

						if (a > b) {
							return 1;
						} else if (a < b) {
							return -1;
						}
					}

					return 0;
				}

			});

			map.put(key, lists);
		}

		return map;
	}

	@Override
	public Map<String, Object> queryPlusSearchChart(
			PageFlowQueryParams params) {

		String[] urls = {};

		if (StringUtils.isNotEmpty(params.getSearch())) {
			urls = params.getSearch().replace(" ", "").split(",");

		}

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("urls", urls);
		paramMap.put("startTime", params.getStartTime());
		paramMap.put("endTime", params.getEndTime());
		paramMap.put("dataSource", params.getDataSource());
		paramMap.put("pageStart", params.getPageStart());
		paramMap.put("pageSize", params.getPageSize());
		paramMap.put("dim", params.getDim());
		paramMap.put("showHour", params.getShowHour());
		paramMap.put("pageContent", params.getPageContent());

		String hourStartTime = params.getStartTime();
		if ("hour".equals(params.getShowHour().toLowerCase())) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			try {
				hourStartTime = df.format(dateFormat.parse(hourStartTime)) + " 00";
			} catch (ParseException e) {

				logger.error(" date format error ", e);
			}
		}

		paramMap.put("hourFilter", hourStartTime);

		params.setHourFilter(hourStartTime);
		List<PageFlow> pageallStations = plusDao.querySearchPageChart(paramMap);

		if (StringUtils.isNotEmpty(params.getPageContent())) {
			urls = params.getPageContent().split(",");
			paramMap.put("urls", urls);
		}

		List<PageFlow> pageflows = plusDao.queryPage(paramMap);

		if (pageflows == null) {
			pageflows = new LinkedList<PageFlow>();
		}

		Map<String, Long> clickMap = new HashMap<String, Long>();

		for (PageFlow pageflow : pageallStations) {

			pageflow.setClick(0);
			pageflow.setAvgVisitTime(pageflow.getViewTime(), pageflow.getPv());
			pageflow.setClickRate(pageflow.getClick(), pageflow.getPv());
			pageflow.setExitsRate(pageflow.getExits(), pageflow.getVisitor());
			pageflow.setPerClick(pageflow.getClick(), pageflow.getUv());
			pageflow.setTakeRate(pageflow.getTakeOrdercount(), pageflow.getUv());
			pageflow.setBouncesRate(pageflow.getBounces(), pageflow.getEntries());
			pageflow.setEntriesRate(pageflow.getEntries(), pageflow.getVisitor());

		}

		Map<String, List<PageFlow>> pageflowMap = new HashMap<String, List<PageFlow>>();

		for (PageFlow pageflow : pageflows) {

			String keyClick = pageflow.getPageContent() + "_" + pageflow.getDate();
			String key = pageflow.getPageContent();

			long pageClick = clickMap.get(keyClick) == null ? 0 : clickMap.get(keyClick);

			pageflow.setClick(pageClick);

			pageflow.setAvgVisitTime(pageflow.getViewTime(), pageflow.getPv());
			pageflow.setClickRate(pageflow.getClick(), pageflow.getPv());
			pageflow.setExitsRate(pageflow.getExits(), pageflow.getVisitor());
			pageflow.setPerClick(pageflow.getClick(), pageflow.getUv());
			pageflow.setTakeRate(pageflow.getTakeOrdercount(), pageflow.getUv());
			pageflow.setBouncesRate(pageflow.getBounces(), pageflow.getEntries());
			pageflow.setEntriesRate(pageflow.getEntries(), pageflow.getVisitor());

			if (pageflowMap.containsKey(key)) {
				List<PageFlow> pfs = pageflowMap.get(key);
				pfs.add(pageflow);
				pageflowMap.put(key, pfs);
			} else {
				List<PageFlow> pfs = new LinkedList<PageFlow>();
				pfs.add(pageflow);
				pageflowMap.put(key, pfs);
			}

		}

		Map<String, Object> map = new LinkedHashMap<String, Object>();

		Collections.sort(pageallStations, new Comparator<PageFlow>() {

			@Override
			public int compare(PageFlow o1, PageFlow o2) {

				String o1Date = o1.getDate();
				String o2Date = o2.getDate();

				if (StringUtils.isNotEmpty(o1Date) && StringUtils.isNotEmpty(o2Date)) {

					int a = Integer.parseInt(o1Date.replace(" ", "").replace("-", ""));

					int b = Integer.parseInt(o2Date.replace(" ", "").replace("-", ""));

					if (a > b) {
						return 1;
					} else if (a < b) {
						return -1;
					}
				}

				return 0;
			}

		});

		map.put("合计", pageallStations);

		for (String key : pageflowMap.keySet()) {

			List<PageFlow> lists = pageflowMap.get(key);

			Collections.sort(lists, new Comparator<PageFlow>() {

				@Override
				public int compare(PageFlow o1, PageFlow o2) {

					String o1Date = o1.getDate();
					String o2Date = o2.getDate();

					if (StringUtils.isNotEmpty(o1Date) && StringUtils.isNotEmpty(o2Date)) {

						int a = Integer.parseInt(o1Date.replace(" ", "").replace("-", ""));

						int b = Integer.parseInt(o2Date.replace(" ", "").replace("-", ""));

						if (a > b) {
							return 1;
						} else if (a < b) {
							return -1;
						}
					}

					return 0;
				}

			});

			map.put(key, lists);
		}
		return map;

	}

	@Override
	public Map<String, Object> queryPlusTopN(PageFlowQueryParams params) {

		Map<String, Object> map = new LinkedHashMap<String, Object>();

		List<String> urls = plusDao.queryPvTopN(params);

		if (urls != null && !urls.isEmpty()) {

			Long total = plusDao.querytotalCountTopN(params);

			long recordsTotal = 0;

			if (total != null && total > 0) {
				recordsTotal = total;
			}

			map.put("recordsTotal", recordsTotal);
			map.put("recordsFiltered", recordsTotal);

			Map<String, Object> param = new HashMap<String, Object>();
			param.put("urls", urls);
			param.put("startTime", params.getStartTime());
			param.put("endTime", params.getEndTime());
			param.put("dataSource", params.getDataSource());
			param.put("pageStart", params.getPageStart());
			param.put("pageSize", params.getPageSize());
			param.put("dim", params.getDim());
			param.put("orderField", params.getOrderField());

			List<PageFlow> pageAllStations = plusDao.queryAllStation(params);

			List<PageFlowTable> flowAllTables = new LinkedList<PageFlowTable>();

			for (PageFlow pageflow : pageAllStations) {
				pageflow.setAvgVisitTime(pageflow.getViewTime(), pageflow.getPv());
				pageflow.setClickRate(pageflow.getClick(), pageflow.getPv());
				pageflow.setExitsRate(pageflow.getExits(), pageflow.getExits());
				pageflow.setPerClick(pageflow.getClick(), pageflow.getUv());
				pageflow.setTakeRate(pageflow.getTakeOrdercount(), pageflow.getUv());
				// 全站跳出率比的是访问
				pageflow.setBouncesRate(pageflow.getBounces(), pageflow.getEntries());
				pageflow.setCheckbox(true);

				//为了防止计算是访问是近似值的误差。用登入次数替代
				pageflow.setEntriesRate(pageflow.getEntries(), pageflow.getEntries());

				PageFlowTable table = pageFlowTotal(pageflow);

				flowAllTables.add(table);
			}

			List<PageFlow> pageflows = plusDao.queryAllTopN(param);

			if (pageflows == null) {
				pageflows = new LinkedList<PageFlow>();
			}

			Map<String, Long> clickMap = new HashMap<String, Long>();

			for (PageFlow pageflow : pageflows) {

				long pageClick = clickMap.get(pageflow.getPageContent()) == null ? 0
						: clickMap.get(pageflow.getPageContent());

				pageflow.setClick(pageClick);
				pageflow.setAvgVisitTime(pageflow.getViewTime(), pageflow.getPv());
				pageflow.setClickRate(pageflow.getClick(), pageflow.getPv());
				pageflow.setExitsRate(pageflow.getExits(), pageflow.getVisitor());
				pageflow.setPerClick(pageflow.getClick(), pageflow.getUv());
				pageflow.setTakeRate(pageflow.getTakeOrdercount(), pageflow.getUv());
				pageflow.setBouncesRate(pageflow.getBounces(), pageflow.getEntries());
				pageflow.setEntriesRate(pageflow.getEntries(), pageflow.getVisitor());

				PageFlowTable table = pageFlowSub(pageflow, pageAllStations.get(0));
				flowAllTables.add(table);

			}

			// pageAllStations.addAll(pageflows);

			map.put("data", flowAllTables);
		} else {

			map.put("recordsTotal", 0);
			map.put("recordsFiltered", 0);
			map.put("data", new ArrayList<PageFlowTable>());

		}
		return map;

	}

	@Override
	public Map<String, Object> queryPlusSearch(PageFlowQueryParams params) {

		String[] urls = {};
		if (StringUtils.isNoneEmpty(params.getSearch())) {
			urls = params.getSearch().replace(" ", "").split(",");
		}

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("urls", urls);
		paramMap.put("startTime", params.getStartTime());
		paramMap.put("endTime", params.getEndTime());
		paramMap.put("dataSource", params.getDataSource());
		paramMap.put("pageStart", params.getPageStart());
		paramMap.put("pageSize", params.getPageSize());
		paramMap.put("dim", params.getDim());
		paramMap.put("orderField", params.getOrderField());

		Map<String, Object> map = new LinkedHashMap<String, Object>();

		if (urls != null && urls.length > 0) {

			Long total = plusDao.querySearchCount(paramMap);

			long recordsTotal = 0;

			if (total != null && total > 0) {
				recordsTotal = total;
			}

			map.put("recordsTotal", recordsTotal);
			map.put("recordsFiltered", recordsTotal);

			List<PageFlow> pageSearchALL = plusDao.querySearchPageTotal(paramMap);

			if (pageSearchALL == null || pageSearchALL.isEmpty() || pageSearchALL.get(0) == null) {
				pageSearchALL = new LinkedList<PageFlow>();
			}

			List<PageFlow> pageAllStationList = plusDao.queryAllStation(params);

			PageFlow allStation = null;

			if (pageAllStationList != null && pageAllStationList.size() > 0) {
				allStation = pageAllStationList.get(0);
			}

			List<PageFlowTable> flowAllTables = new LinkedList<PageFlowTable>();

			long click = 0;

			for (PageFlow pageflow : pageSearchALL) {

				pageflow.setClick(click);
				pageflow.setAvgVisitTime(pageflow.getViewTime(), pageflow.getPv());
				pageflow.setClickRate(pageflow.getClick(), pageflow.getPv());
				pageflow.setExitsRate(pageflow.getExits(), pageflow.getVisitor());
				pageflow.setPerClick(pageflow.getClick(), pageflow.getUv());
				pageflow.setTakeRate(pageflow.getTakeOrdercount(), pageflow.getUv());
				pageflow.setBouncesRate(pageflow.getBounces(), pageflow.getEntries());
				pageflow.setCheckbox(true);
				pageflow.setEntriesRate(pageflow.getEntries(), pageflow.getVisitor());

				PageFlowTable table = pageFlowSubSearchTotal(pageflow, allStation);

				flowAllTables.add(table);

			}

			List<PageFlow> pageflows = plusDao.queryAllTopN(paramMap);

			if (pageflows == null) {
				pageflows = new LinkedList<PageFlow>();
			}

			Map<String, Long> clickMap = new HashMap<String, Long>();

			for (PageFlow pageflow : pageflows) {
				String key = pageflow.getPageContent();

				long pageClick = clickMap.get(key) == null ? 0 : clickMap.get(key);

				pageflow.setClick(pageClick);
				pageflow.setAvgVisitTime(pageflow.getViewTime(), pageflow.getPv());
				pageflow.setClickRate(pageflow.getClick(), pageflow.getPv());
				pageflow.setExitsRate(pageflow.getExits(), pageflow.getVisitor());
				pageflow.setPerClick(pageflow.getClick(), pageflow.getUv());
				pageflow.setTakeRate(pageflow.getTakeOrdercount(), pageflow.getUv());
				pageflow.setBouncesRate(pageflow.getBounces(), pageflow.getEntries());
				pageflow.setEntriesRate(pageflow.getEntries(), pageflow.getVisitor());
				PageFlowTable table = pageFlowSub(pageflow, allStation);

				flowAllTables.add(table);
			}

			map.put("data", flowAllTables);

		} else {

			map.put("recordsTotal", 0);
			map.put("recordsFiltered", 0);
			map.put("data", new ArrayList<PageFlowTable>());

		}
		return map;

	}
	
	
	

}
