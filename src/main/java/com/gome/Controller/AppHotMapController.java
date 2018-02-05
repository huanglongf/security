package com.gome.Controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gome.Controller.model.AppHotMapQueryParam;
import com.gome.Controller.model.PageResponse;
import com.gome.Controller.model.ResourceResponse;
import com.gome.service.IAppHotMapQueryService;

/**
 * app首页热力图的controller。为了统一路径，入口由OfflineHotMapContrller代替。
 * @author wangshubao
 *
 */
@Controller
@RequestMapping("/app_sy_hotmap")
public class AppHotMapController {

	private static final Logger logger = LoggerFactory.getLogger(AppHotMapController.class);
	
	@Autowired
	private IAppHotMapQueryService queryService;
	
	/**
	 * 查询首页汇总信息
	 * 
	 * @param param
	 */
	@RequestMapping(value = "/queryPage", produces = "application/json; charset=utf-8")
	@ResponseBody

	public String queryPage(AppHotMapQueryParam param,
			@RequestParam(value = "compareStartTime", required = false) String compareStartTime,
			@RequestParam(value = "compareEndTime", required = false) String compareEndTime) {

		logger.info("{}参数：{}, compareStartTime={}, compareEndTime={}", "/queryPage", JSON.toJSONString(param),
				compareStartTime, compareEndTime);
		long start = System.currentTimeMillis();

		// 校验请求参数
		try {

			validateParam(param, false);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return JSON.toJSONString(new HashMap<String, Object>(), SerializerFeature.WriteMapNullValue,
					SerializerFeature.WriteNullStringAsEmpty);
		}

		Map<String, Object> map = new LinkedHashMap<String, Object>();

		// 查询页面指标信息
		Map<String, Object> res = queryPageInfo(param);
		map.put("datas", res);

		// 查询页面对比指标信息
		if (StringUtils.isNotEmpty(compareStartTime) && StringUtils.isNotEmpty(compareEndTime)) {
			param.setStartTime(compareStartTime.replace("-", ""));
			param.setEndTime(compareEndTime.replace("-", ""));

			Map<String, Object> compareRes = queryPageInfo(param);
			map.put("compareDatas", compareRes);
		}

		logger.info("/queryPage，耗时{}毫秒", System.currentTimeMillis() - start);

		return JSON.toJSONString(map, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty);
	}

	private Map<String, Object> queryPageInfo(AppHotMapQueryParam param) {

		// 查询页面的汇总指标信息
		PageResponse pageResponse = queryService.querySummaryOfPage(param);
		// 查询页面的各资源位指标信息
		Map<String, ResourceResponse> resources = queryService.queryAllResourceForPage(param, pageResponse);

		Map<String, Object> map = new HashMap<String, Object>(2, 1.0f);

		map.put("page", pageResponse);
		map.put("resource", resources);

		return map;
	}

	/**
	 * 查询页面的图表信息
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/chart", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String chart(AppHotMapQueryParam param) {

		logger.info("{}参数：{}", "/chart", JSON.toJSONString(param));
		long start = System.currentTimeMillis();

		// 校验请求参数
		try {

			validateParam(param, false);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return JSON.toJSONString(new HashMap<String, Object>(), SerializerFeature.WriteMapNullValue,
					SerializerFeature.WriteNullStringAsEmpty);
		}
		
		// 按时间分组的图表查询
		Map<String, PageResponse> chartQueryByTime = null;
		if (param.getStartTime().equals(param.getEndTime())) {
			chartQueryByTime = queryService.chartQueryOfHour(param);
		} else {
			chartQueryByTime = queryService.chartQueryOfDay(param);
		}

		Map<String, Object> map = new HashMap<String, Object>(2, 1.0f);
		map.put("pageDatas", chartQueryByTime);

		logger.info("/chart，耗时{}毫秒", System.currentTimeMillis() - start);

		String json = JSONObject.toJSONString(map, SerializerFeature.WriteMapNullValue,
				SerializerFeature.WriteNullStringAsEmpty);

		return json;
	}

	/**
	 * 查询资源位历史信息
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/queryResource", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String queryResourceHistory(AppHotMapQueryParam param) {

		logger.info("{}参数：{}", "/queryResource", JSON.toJSONString(param));

		long start = System.currentTimeMillis();

		// 校验请求参数
		try {

			validateParam(param, true);

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return JSON.toJSONString(new HashMap<String, Object>(), SerializerFeature.WriteMapNullValue,
					SerializerFeature.WriteNullStringAsEmpty);
		}

		Map<String, Object> map = queryService.queryResourceHistoryInfo(param);

		logger.info("/queryResource，耗时{}毫秒", System.currentTimeMillis() - start);

		String json = JSONObject.toJSONString(map, SerializerFeature.WriteMapNullValue,
				SerializerFeature.WriteNullStringAsEmpty);

		return json;
	}
	
	private void validateParam(AppHotMapQueryParam param, boolean validateIntcmp) throws Exception {

		logger.info(" param valid  before :" + param.toString());
		if (StringUtils.isEmpty(param.getStartTime()))
			throw new Exception("查询参数的开始时间为空");

		if (StringUtils.isEmpty(param.getEndTime()))
			throw new Exception("查询参数的结束时间为空");

		if (validateIntcmp && StringUtils.isEmpty(param.getIntcmp())) {
			throw new Exception("查询参数的资源位id为空");
		}

		// 如果为非实时，则去掉-
		param.setStartTime(param.getStartTime().replaceAll("-", ""));
		param.setEndTime(param.getEndTime().replace("-", ""));

		logger.info(" param valid  after :" + param.toString());
	}
}
