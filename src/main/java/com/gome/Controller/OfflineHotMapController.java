package com.gome.Controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gome.Controller.model.AppHotMapQueryParam;
import com.gome.Controller.model.ChannelResponse;
import com.gome.Controller.model.PageResponse;
import com.gome.Controller.model.QueryParam;
import com.gome.Controller.model.ResourceResponse;
import com.gome.service.ActualtimeHotMapService;
import com.gome.service.IOfflineHotMapQueryService;

import redis.GcacheClient;
import redis.gcache.KeysExecutor;

/**
 * 离线热力图controller
 *
 * @author wangshubao
 *
 */

@Controller
@RequestMapping("/home")
public class OfflineHotMapController {

	private static final Logger logger = LoggerFactory.getLogger(OfflineHotMapController.class);

	private static final String APP_DATASOURCE = "APP-LOG";
	private static final String PLUS_APP_DATASOURCE = "PLUS-LOG";

	@Resource
	private GcacheClient gcacheClient;

	@Autowired
	private IOfflineHotMapQueryService queryService;

	@Autowired
	private ActualtimeHotMapService actualService;

	@Autowired
	private AppHotMapController appHotMapController;

	@Value("${gcache.expire}")
	private int expire;
	@Value("${gcache.flag}")
	private int cacheFlag;

	/**
	 * 查询首页汇总信息
	 * http://10.58.72.33:8080/homepage/home/chart?pageUrl=http://m.gome.com.cn/&startTime=2018-02-04&endTime=2018-02-04图表
	   http://10.58.72.33:8080/homepage/home/queryPage?pageUrl=http://m.gome.com.cn/&startTime=2018-02-04&endTime=2018-02-04 资源位

	 *
	 if (prefixUrl.contains("m.gomeplus.com")) {
	 param.setDataSource("PLUS-WAP");
	 } else if (prefixUrl.contains("m.gome.com.cn")) {
	 param.setDataSource("WAP");
	 } else if(prefixUrl.contains("gomeplus.com")){
	 param.setDataSource("PLUS-PC");
	 }else if(prefixUrl.contains("gome.com.cn")){
	 param.setDataSource("PC");
	 }else{
	 param.setDataSource("PC");
	 }
	 }else if(param.getDataSource().equals("null")){
	 param.setDataSource("PC");
	 }
	 * @param param
	 */
	@RequestMapping(value = "/queryPage", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String queryPage(QueryParam param,
			@RequestParam(value = "compareStartTime", required = false) String compareStartTime,
			@RequestParam(value = "compareEndTime", required = false) String compareEndTime) {

		logger.info("{}参数：{}, compareStartTime={}, compareEndTime={}", "/queryPage", JSON.toJSONString(param),
				compareStartTime, compareEndTime);
		long start = System.currentTimeMillis();

		// 校验请求参数
		try {
			validateParam(param, false);

			String json = "[]";

			String key = "hotmap_home_queryPage" + param.toString();

			json = FromCache(key);


          // 缓存 redis 数据缓存:
			if (StringUtils.isNotEmpty(json) && param.getIsCache() == 0) {

				logger.info(" read data from gcache! ");
               //测试打印json 的读取数据
				System.out.println(json);
			}


			//app首页实时热力图
			if(APP_DATASOURCE.equals(param.getDataSource())
					|| PLUS_APP_DATASOURCE.equals(param.getDataSource())){
				AppHotMapQueryParam appParam = new AppHotMapQueryParam(param.getStartTime(), param.getEndTime(), param.getIntcmp());
				appParam.setDataSource(param.getDataSource());
				json = appHotMapController.queryPage(appParam, compareStartTime, compareEndTime);
				setCache(key, json);
				return json;
			}


			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			if (dateFormat.format(new Date()).equals(param.getStartTime())) {
				json = actualService.queryPage(param);
				setCache(key, json);
				return json;
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

			json = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty);
			setCache(key, json);
			return json;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return JSON.toJSONString(new HashMap<String, Object>(), SerializerFeature.WriteMapNullValue,
					SerializerFeature.WriteNullStringAsEmpty);
		}
	}

	private Map<String, Object> queryPageInfo(QueryParam param) {

		// 查询页面的汇总指标信息
		PageResponse pageResponse = queryService.querySummaryOfPage(param);

		Map<String, Object> map = new HashMap<String, Object>(2, 1.0f);

		map.put("page", pageResponse);

		if(param.getQueryResource()!=null && param.getQueryResource()){
			// 查询页面的各资源位指标信息
			Map<String, ResourceResponse> resources = queryService.queryAllResourceForPage(param, pageResponse);
			map.put("resource", resources);
		}
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
	public String chart(QueryParam param) {

		logger.info("{}参数：{}", "/chart", JSON.toJSONString(param));
		long start = System.currentTimeMillis();
		// 校验请求参数
		try {

			validateParam(param, false);

			String json = "[]";

			String key = "hotmap_home_chart" + param.toString();

			json = FromCache(key);

			if (StringUtils.isNotEmpty(json) && param.getIsCache() == 0) {

				logger.info(" read data from gcache! ");

				return json;
			}


			//app首页实时热力图
			if(APP_DATASOURCE.equals(param.getDataSource())
					|| PLUS_APP_DATASOURCE.equals(param.getDataSource())){
				AppHotMapQueryParam appParam = new AppHotMapQueryParam(param.getStartTime(), param.getEndTime(), param.getIntcmp());
				appParam.setDataSource(param.getDataSource());
				json = appHotMapController.chart(appParam);
				setCache(key, json);
				return json;
			}

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			if (dateFormat.format(new Date()).equals(param.getStartTime())) {

				json = actualService.chart(param);
				setCache(key, json);
				return json;
			}

			// 按时间分组的图表查询
			Map<String, PageResponse> chartQueryByTime = null;
			if (param.getStartTime().equals(param.getEndTime())) {
				chartQueryByTime = queryService.chartQueryOfHour(param);
			} else {
				chartQueryByTime = queryService.chartQueryOfDay(param);
			}

			// 按渠道分组的图表查询
			Map<String, ChannelResponse> chartQueryByChannel = queryService.chartQueryOfChannel(param);

			Map<String, Object> map = new HashMap<String, Object>(2, 1.0f);
			map.put("pageDatas", chartQueryByTime);
			map.put("channels", chartQueryByChannel);

			logger.info("/chart，耗时{}毫秒", System.currentTimeMillis() - start);

			json = JSONObject.toJSONString(map, SerializerFeature.WriteMapNullValue,
					SerializerFeature.WriteNullStringAsEmpty);
			setCache(key, json);
			return json;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return JSON.toJSONString(new HashMap<String, Object>(), SerializerFeature.WriteMapNullValue,
					SerializerFeature.WriteNullStringAsEmpty);
		}
	}

	/**
	 * 查询资源位历史信息
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/queryResource", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String queryResourceHistory(QueryParam param) {

		logger.info("{}参数：{}", "/queryResource", JSON.toJSONString(param));

		long start = System.currentTimeMillis();

		// 校验请求参数
		try {

			//根据url来分析set datasource;
			validateParam(param, true);


			String json = "[]";

			String key = "hotmap_home_queryResource" + param.toString();

			json = FromCache(key);

			if (StringUtils.isNotEmpty(json) && param.getIsCache() == 0) {

				logger.info(" read data from gcache! ");

				return json;
			}


			//app首页实时热力图
			/**
			 * private static final String APP_DATASOURCE = "APP-LOG";
			   private static final String PLUS_APP_DATASOURCE = "PLUS-LOG";
			 */
			if(APP_DATASOURCE.equals(param.getDataSource())
					|| PLUS_APP_DATASOURCE.equals(param.getDataSource())){
				AppHotMapQueryParam appParam = new AppHotMapQueryParam(param.getStartTime(), param.getEndTime(), param.getIntcmp());
				appParam.setDataSource(param.getDataSource());
				json = appHotMapController.queryResourceHistory(appParam);
				setCache(key, json);
				return json;
			}


			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			if (dateFormat.format(new Date()).equals(param.getStartTime())) {

				json = actualService.queryResourceHistory(param);
				setCache(key, json);
				return json;
			}

			Map<String, Object> map = queryService.queryResourceHistoryInfo(param);

			logger.info("/queryResource，耗时{}毫秒", System.currentTimeMillis() - start);

			json = JSONObject.toJSONString(map, SerializerFeature.WriteMapNullValue,
					SerializerFeature.WriteNullStringAsEmpty);
			setCache(key, json);
			return json;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return JSON.toJSONString(new HashMap<String, Object>(), SerializerFeature.WriteMapNullValue,
					SerializerFeature.WriteNullStringAsEmpty);
		}
	}

	private void validateParam(QueryParam param, boolean validateIntcmp) throws Exception {

		logger.info(" param valid  before :" + param.toString());
		if (StringUtils.isEmpty(param.getStartTime()))
			throw new Exception("查询参数的开始时间为空");

		if (StringUtils.isEmpty(param.getEndTime()))
			throw new Exception("查询参数的结束时间为空");

		// if (StringUtils.isEmpty(param.getDataSource()))
		// throw new Exception("查询参数的来源站点为空");

		if (StringUtils.isEmpty(param.getPageUrl()))
			throw new Exception("查询参数的页面url为空");

		if (validateIntcmp && StringUtils.isEmpty(param.getIntcmp())) {
			throw new Exception("查询参数的资源位id为空");
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		// 如果为非实时，则去掉-
		if (!(dateFormat.format(new Date()).equals(param.getStartTime()))) {
			param.setStartTime(param.getStartTime().replaceAll("-", ""));
			param.setEndTime(param.getEndTime().replace("-", ""));
		}

		if (param.getDataSource() == null) {
			//取出去参url
			String prefixUrl = param.getPageUrl().toLowerCase();
			int index = param.getPageUrl().indexOf('?');
			if(index>=0){
				prefixUrl = param.getPageUrl().substring(0, index).toLowerCase();
			}

			if (prefixUrl.contains("m.gomeplus.com")) {
				param.setDataSource("PLUS-WAP");
			} else if (prefixUrl.contains("m.gome.com.cn")) {
				param.setDataSource("WAP");
			} else if(prefixUrl.contains("gomeplus.com")){
				param.setDataSource("PLUS-PC");
			}else if(prefixUrl.contains("gome.com.cn")){
				param.setDataSource("PC");
			}else{
				param.setDataSource("PC");
			}
		}else if(param.getDataSource().equals("null")){
			param.setDataSource("PC");
		}

		//融合项目修改，查询点击量和资源位信息时，需要同时查询在线和plus两个datasource
		if(param.getDataSource().equals("WAP")){
			param.setDataSourceLike("WAP");
		}else if(param.getDataSource().equals("PC")){
			param.setDataSourceLike("PC");
		}


		if (param.getPageUrl().endsWith("/")) {

			String url = param.getPageUrl().substring(0, param.getPageUrl().length() - 1);
			param.setPageUrl(url);
		}

		if (param.getDataSource().equals("PLUS-WAP")
				|| param.getDataSource().equals("PLUS-LOG")){
			String startTime = param.getStartTime().replaceAll("-", "");
			String endTime = param.getEndTime().replaceAll("-", "");
			param.setStartTime(startTime);
			param.setEndTime(endTime);
			param.setPageUrl(param.getPageUrl().replace("http://", "").replaceAll("https://", ""));
		}

		logger.info(" param valid  after :" + param.toString());
	}

	/**
	 * 根据活动页的URL。请求活动页的开始时间和结束时间
	 *
	 * @param pageUrl
	 * @return
	 */
	@RequestMapping(value = "/queryTime", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String queryTime(@RequestParam("pageUrl") String pageUrl) {

		pageUrl = pageUrl.substring("http://prom.gome.com.cn/html/prodhtml/topics/".length());

		String key = "activePage_" + pageUrl.trim();
		String value = gcacheClient.get(key);

		if (value != null) {
			return value;
		} else {

			Map<String, String> map = new LinkedHashMap<String, String>();

			String json = JSONObject.toJSONString(map, SerializerFeature.WriteMapNullValue);
			return json;
		}
	}

	/**
	 * 新增活动页接口
	 *
	 * @return
	 */
	@RequestMapping(value = "/newActivePage", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String newActivePage() {

		String json = gcacheClient.get("activePage_new");

		logger.info("newActivePage json  " + json);
		return json;
	}

	/**
	 * 查询资源位所有的ModelId
	 *
	 * @return
	 */

	@RequestMapping(value = "/queryModelId", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String queryModelId() {

		String key = gcacheClient.get("hompage_modelId");

		return key;

	}

	@RequestMapping(value = "/queryMaxUvPage", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String queryMaxUvPage(@RequestParam(value= "size",required=false,defaultValue="10") String sizeString,
			@RequestParam(value = "dataSource", required = false, defaultValue = "PC") String dataSource) {
		int size = 1;
		if (sizeString != null) {
			size = Integer.parseInt(sizeString);
		}

		Calendar cal = Calendar.getInstance();

		cal.add(Calendar.DAY_OF_MONTH, -1);

		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
		QueryParam param = new QueryParam();

		param.setStartTime(sFormat.format(cal.getTime()));
		param.setEndTime(sFormat.format(cal.getTime()));

		if ("WAP".equals(dataSource)) {
			param.setDataSource(dataSource);
			param.setPageUrl("http://prom.m.gome.com.cn/html/");
		} else if ("PLUS-WAP".equals(dataSource)){
			param.setDataSource(dataSource);
			param.setPageUrl("http://prom.m.gomeplus.com/html/");
		} else {
			param.setDataSource("PC");
			param.setPageUrl("http://prom.gome.com.cn/html");
		}

		String index = "current-session" + param.getStartTime().replace("-", "");

		logger.info("index= " + index);

		List<String> urls = actualService.queryMaxUvPage(size, param, index);

		String json = JSON.toJSONString(urls, SerializerFeature.WriteNullListAsEmpty);
		logger.info("queryMaxUvPage >> " + json);

		return json;
	}

	public String FromCache(String key) {

		if (cacheFlag == 1) {

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

		if (cacheFlag == 1) {

			gcacheClient.setex(key, expire, value);

			logger.debug(" inser gcache key " + key);
			logger.debug(" inser gcache value " + value);

		}
	}

	@RequestMapping(value = "/clearCache", produces = "application/json; charset=utf-8")
	public String clearCache(){
		gcacheClient.scanCluster("hotmap_home_*", 1024, new KeysExecutor(){

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
