package com.gome.pageflow.Controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.gome.pageflow.model.PageFlowQueryParams;
import com.gome.pageflow.service.PageFlowAppService;
import com.gome.pageflow.service.PageFlowService;

import redis.GcacheClient;
import redis.gcache.KeysExecutor;

@RequestMapping("/pageFlow")
@RestController
public class PageFlowController {

	private Logger logger = Logger.getLogger(PageFlowController.class);

	@Autowired
	private PageFlowService flowService;
	@Autowired
	private PageFlowAppService flowAppService;

	@Value("${gcache.expire}")
	private int expire;
	@Value("${gcache.flag}")
	private int cacheFlag = 0;

	@Resource
	private GcacheClient gcacheClient;

	@RequestMapping(value = "/queryTop", produces = "application/json; charset=utf-8")
	public String queryAll(PageFlowQueryParams params) {

		if (!valid(params)) {

			return "[{\"status\":\"query params have null!\"}]";

		}
		try {
			String json = "[]";
	
			String key = "page_flow_" + params.hashCode();
	
			json = FromCache(key);
	
	//		if (StringUtils.isNotEmpty(json)) {
	//
	//			logger.info(" read data from gcache! ");
	//
	//			return json;
	//		}
	
			logger.info(" start select kylin .......");
	
			boolean isApp = "app-log".equals(params.getDataSource().trim().toLowerCase());
	
			if (isApp) {
	
				if (StringUtils.isEmpty(params.getSearch())) {
	
					Map<String, Object> pageFlowMap = flowAppService.queryTopN(params);
	
					json = JSONObject.toJSONString(pageFlowMap);
	
				} else {
					Map<String, Object> pageFlowMap = flowAppService.querySearch(params);
	
					json = JSONObject.toJSONString(pageFlowMap);
	
				}
	
			} else if("Plus-LOG".equalsIgnoreCase(params.getDataSource().trim().toLowerCase())){
				if (StringUtils.isEmpty(params.getSearch())) {
	
					Map<String, Object> pageFlowMap = flowAppService.queryPlusTopN(params);
	
					json = JSONObject.toJSONString(pageFlowMap);
	
				} else {
					Map<String, Object> pageFlowMap = flowAppService.queryPlusSearch(params);
	
					json = JSONObject.toJSONString(pageFlowMap);
	
				}
				
			}else {
				String dataSource = params.getDataSource().trim().toLowerCase();
				
				if("PLUS-WAP".equalsIgnoreCase(dataSource)
						|| "GJ-PC".equalsIgnoreCase(dataSource)
						|| "GJ-WAP".equalsIgnoreCase(dataSource)){
					if("PLUS-WAP".equalsIgnoreCase(params.getDataSource().trim().toLowerCase())){
						params.setDataSource("PLUS-WAP");
					}else if("GJ-PC".equalsIgnoreCase(params.getDataSource().trim().toLowerCase())){
						params.setDataSource("GJ-PC");
					}else if("GJ-WAP".equalsIgnoreCase(params.getDataSource().trim().toLowerCase())){
						params.setDataSource("GJ-WAP");
					}
					if (StringUtils.isEmpty(params.getSearch())) {
	
						Map<String, Object> pageFlowMap = flowService.queryPlusTopN(params);
	
						json = JSONObject.toJSONString(pageFlowMap);
	
					} else {
						Map<String, Object> pageFlowMap = flowService.queryPlusSearch(params);
	
						json = JSONObject.toJSONString(pageFlowMap);
	
					}
				}else{
					if("pc".equals(dataSource)){
						params.setDataSourceLike("PC");
					}else if("wap".equals(dataSource)){
						params.setDataSourceLike("WAP");
					}
					
					if (StringUtils.isEmpty(params.getSearch())) {
	
						Map<String, Object> pageFlowMap = flowService.queryTopN(params);
	
						json = JSONObject.toJSONString(pageFlowMap);
	
					} else {
						Map<String, Object> pageFlowMap = flowService.querySearch(params);
	
						json = JSONObject.toJSONString(pageFlowMap);
	
					}
				}
				
			}
	//		setCache(key, json);
	
			logger.info(" after select db  params : " + params);
	
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			return "{\"recordsTotal\":0,\"recordsFiltered\":0,\"data\":[]}";
		}
	}

	@RequestMapping(value = "/pageChart", produces = "application/json; charset=utf-8")
	public String queryChart(PageFlowQueryParams queryParams) {

		if (!valid(queryParams)) {
			return "[{\"status\":\"query params have null!\"}]";

		}
		try {
			String key = "page_flow_" + queryParams.hashCode();
	
			String json = "[]";
	
			json = FromCache(key);
	
	//		if (StringUtils.isNotEmpty(json)) {
	//
	//			logger.info(" read data from gcache! ");
	//
	//			return json;
	//		}
	
			logger.info(" start select db .......");
	
			boolean isApp = "app-log".equals(queryParams.getDataSource().trim().toLowerCase());
	
			if (isApp) {
	
				if (StringUtils.isEmpty(queryParams.getSearch())) {
	
					Map<String, Object> pages = flowAppService.queryChart(queryParams);
	
					json = JSONObject.toJSONString(pages);
	
					String responseJson = "{\"result\":[" + json + "]}";
	
	//				setCache(key, responseJson);
	
					return responseJson;
				} else {
	
					Map<String, Object> pages = flowAppService.querySearchChart(queryParams);
	
					json = JSONObject.toJSONString(pages);
	
					String responseJson = "{\"result\":[" + json + "]}";
	
	//				setCache(key, responseJson);
	
					return responseJson;
				}
	
			} else if("Plus-LOG".equalsIgnoreCase(queryParams.getDataSource().trim().toLowerCase())){
				if (StringUtils.isEmpty(queryParams.getSearch())) {
	
					Map<String, Object> pages = flowAppService.queryPlusChart(queryParams);
	
					json = JSONObject.toJSONString(pages);
	
					String responseJson = "{\"result\":[" + json + "]}";
	
	//				setCache(key, responseJson);
	
					return responseJson;
				} else {
	
					Map<String, Object> pages = flowAppService.queryPlusSearchChart(queryParams);
	
					json = JSONObject.toJSONString(pages);
	
					String responseJson = "{\"result\":[" + json + "]}";
	
	//				setCache(key, responseJson);
	
					return responseJson;
				}
				
			}else {
				Map<String, Object> pages = null;
				if (StringUtils.isEmpty(queryParams.getSearch())) {
					if("PLUS-WAP".equalsIgnoreCase(queryParams.getDataSource().trim().toLowerCase())
							||"GJ-PC".equalsIgnoreCase(queryParams.getDataSource().trim().toLowerCase())
							||"GJ-WAP".equalsIgnoreCase(queryParams.getDataSource().trim().toLowerCase())){
						if("PLUS-WAP".equalsIgnoreCase(queryParams.getDataSource().trim().toLowerCase())){
							queryParams.setDataSource("PLUS-WAP");
						}else if("GJ-PC".equalsIgnoreCase(queryParams.getDataSource().trim().toLowerCase())){
							queryParams.setDataSource("GJ-PC");
						}else if("GJ-WAP".equalsIgnoreCase(queryParams.getDataSource().trim().toLowerCase())){
							queryParams.setDataSource("GJ-WAP");
						}
						pages = flowService.queryPlusChart(queryParams);
					}else{
						pages = flowService.queryChart(queryParams);
					}
					
					json = JSONObject.toJSONString(pages);
	
					String responseJson = "{\"result\":[" + json + "]}";
	
	//				setCache(key, responseJson);
	
					return responseJson;
				} else {
					if("PLUS-WAP".equalsIgnoreCase(queryParams.getDataSource().trim().toLowerCase())
							||"GJ-PC".equalsIgnoreCase(queryParams.getDataSource().trim().toLowerCase())
							||"GJ-WAP".equalsIgnoreCase(queryParams.getDataSource().trim().toLowerCase())){
						if("PLUS-WAP".equalsIgnoreCase(queryParams.getDataSource().trim().toLowerCase())){
							queryParams.setDataSource("PLUS-WAP");
						}else if("GJ-PC".equalsIgnoreCase(queryParams.getDataSource().trim().toLowerCase())){
							queryParams.setDataSource("GJ-PC");
						}else if("GJ-WAP".equalsIgnoreCase(queryParams.getDataSource().trim().toLowerCase())){
							queryParams.setDataSource("GJ-WAP");
						}
						pages = flowService.queryPlusSearchChart(queryParams);
					}else{
						pages = flowService.querySearchChart(queryParams);
					}
					
					json = JSONObject.toJSONString(pages);
	
					String responseJson = "{\"result\":[" + json + "]}";
	
	//				setCache(key, responseJson);
	
					return responseJson;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "{\"result\":[{\"合计\":[]}]}";
		}
	}

	private boolean valid(PageFlowQueryParams queryParams) {

		logger.info(" queryParams :" + queryParams.toString());

		if (StringUtils.isEmpty(queryParams.getDataSource())) {

			logger.error(" dataSource is null");
			return false;
		}

		if (StringUtils.isEmpty(queryParams.getStartTime())) {
			logger.error("startTime is null ");

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

		if (StringUtils.isEmpty(queryParams.getOrderField())) {
			queryParams.setOrderField("pv");
		}

		if ("app-log".equals(queryParams.getDataSource().trim().toLowerCase())) {
			queryParams.setDataSource(queryParams.getDataSource().trim().toUpperCase());
			queryParams.setDim("pagetype");
		}

		return true;

	}

	public String FromCache(String key) {

		if (cacheFlag == 0) {

			String json = gcacheClient.get(key);

			logger.debug(" select gcache  key :" + key);

			logger.debug(" gcache  value : " + json);

			if (StringUtils.isNotEmpty(json)) {

				return json;
			}
		}
		return null;

	}

	public void setCache(String key, String value) {

		if (cacheFlag == 0) {

			gcacheClient.setex(key, expire, value);

			logger.debug(" inser gcache key " + key);
			logger.debug(" inser gcache value " + value);

		}
	}
	
	@RequestMapping(value = "/clearCache", produces = "application/json; charset=utf-8")
	public String clearCache(){
		gcacheClient.scanCluster("page_flow_*", 1024, new KeysExecutor(){

			@Override
			public void execute(List<String> keys) {
				if(CollectionUtils.isNotEmpty(keys)){
					for(String key: keys){
						gcacheClient.del(key);
					}
				}
			}
			
		});
		
		return "清除缓存成功";
	}

}
