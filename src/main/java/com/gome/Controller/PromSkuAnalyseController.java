package com.gome.Controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gome.promsku.model.PromSkuAnalyseCityInfo;
import com.gome.promsku.model.PromSkuAnalyseQueryParam;
import com.gome.promsku.model.PromSkuAnalyseResponse;
import com.gome.promsku.model.PromSkuInfoServiceResult;
import com.gome.service.IPromSkuAnalyseQueryService;

/**
 * 活动页商品分析的controller
 * @author wangshubao
 *
 */
@Controller
@RequestMapping("/prom_sku_analyse")
public class PromSkuAnalyseController {
	
	@Autowired
	private IPromSkuAnalyseQueryService service;

	/**
	 * 查询活动页对应的商品指标
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/query_list", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String query(PromSkuAnalyseQueryParam param){
		String errorMsg = validParamForList(param);
		
		if(StringUtils.isNotEmpty(errorMsg)){
			Map<String, String> errorMsgMap = new HashMap<String, String>();
			errorMsgMap.put("errorMsg", errorMsg);
			errorMsgMap.put("code", "400");
			
			return JSON.toJSONString(errorMsgMap, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty);
		}
		
		PromSkuAnalyseResponse response = service.queryPromSkuInfo(param);
		if(response.getSkuInfos()==null){
			response.setSkuInfos(new LinkedList<PromSkuInfoServiceResult>());
		}
		return JSON.toJSONString(response, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty);
	}
	
	private String validParamForList(PromSkuAnalyseQueryParam param) {
		String promUrl = param.getPromUrl();
		if(StringUtils.isEmpty(promUrl)){
			return "活动页url不能为空";
		}else{
			if(promUrl.endsWith("/") || promUrl.endsWith("\\"))
				param.setPromUrl(promUrl.substring(0, promUrl.length()-1));
			
			param.setPromUrl(param.getPromUrl().trim());
		}
		
		if(StringUtils.isEmpty(param.getDatasource())){
			return "站点类型不能为空";
		}
		
		if(StringUtils.isEmpty(param.getStartDate())){
			return "查询起始时间不能为空";
		}else{
			param.setStartDate(param.getStartDate().replace("-", ""));
		}
		
		if(StringUtils.isEmpty(param.getEndDate())){
			return "查询结束时间不能为空";
		}else{
			param.setEndDate(param.getEndDate().replace("-", ""));
		}
		
		return null;
	}

	/**
	 * 查询活动页对应的某个sku的城市可卖数
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/query_city", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String queryCityInfo(PromSkuAnalyseQueryParam param){
		String errorMsg = validParamForCity(param);
		if(StringUtils.isNotEmpty(errorMsg)){
			Map<String, String> errorMsgMap = new HashMap<String, String>();
			errorMsgMap.put("errorMsg", errorMsg);
			errorMsgMap.put("code", "400");
			
			return JSON.toJSONString(errorMsgMap, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty);
		}
		List<PromSkuAnalyseCityInfo> list = service.queryPromSkuCityInfo(param);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", "200");
		map.put("data", list);
		return JSON.toJSONString(map, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty);
	}

	private String validParamForCity(PromSkuAnalyseQueryParam param) {
		String promUrl = param.getPromUrl();
		if(StringUtils.isEmpty(promUrl)){
			return "活动页url不能为空";
		}else{
			if(promUrl.endsWith("/") || promUrl.endsWith("\\"))
				param.setPromUrl(promUrl.substring(0, promUrl.length()-1));
			
			param.setPromUrl(param.getPromUrl().trim());
		}
		
		if(StringUtils.isEmpty(param.getDatasource())){
			return "站点类型不能为空";
		}
		
		if(StringUtils.isEmpty(param.getStartDate())){
			return "查询起始时间不能为空";
		}
		
		if(StringUtils.isEmpty(param.getEndDate())){
			return "查询结束时间不能为空";
		}
		
		if(StringUtils.isEmpty(param.getSkuId())){
			return "skuId不能为空";
		}
		
		if(StringUtils.isNotEmpty(param.getSkuStartDate())){
			if(param.getSkuStartDate().compareTo(param.getStartDate()) > 0)
				param.setStartDate(param.getSkuStartDate());
		}
		
		if(StringUtils.isNotEmpty(param.getSkuEndDate())){
			if(param.getSkuEndDate().compareTo(param.getEndDate()) < 0)
				param.setEndDate(param.getSkuEndDate());
		}
		
		param.setStartDate(param.getStartDate().replace("-", ""));
		param.setEndDate(param.getEndDate().replace("-", ""));
		
		return null;
	}
	
	/**
	 * 设置缓存策略
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/use_cache", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String setCache(@RequestParam("useCache") String useCache){
		String msg;
		boolean isUseCache = false;
		if (useCache != null && useCache.equals("1"))
			isUseCache = true;

		service.setUseCache(isUseCache);

		if (isUseCache)
			msg = "启用缓存成功";
		else
			msg = "关闭缓存成功";
		
		return msg;
	}
	
	
	/**
	 * 清理缓存
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/clear_cache", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String clearCache(){
		service.clearCache();
		return "清除缓存成功";
	}
}
