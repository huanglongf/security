package com.gome.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gome.Controller.model.PageResponse;
import com.gome.Controller.model.PathAnalyseControllerQueryParam;
import com.gome.Controller.model.QueryParam;
import com.gome.pathanalyse.model.PathAnalyseQueryParam;
import com.gome.pathanalyse.model.PathAnalyseQueryResult;
import com.gome.service.IOfflineHotMapQueryService;
import com.gome.service.IPathAnalyseQueryService;

/**
 * 页面路径分析查询的controller
 * @author wangshubao
 *
 */
@Controller
@RequestMapping("/pathanalyse")
public class PathAnalyseController {
	
	private static final int DEFAULT_PAGE_SIZE = 5;
	private static final Logger logger = LoggerFactory.getLogger(PathAnalyseController.class);	
	
	@Autowired
	IPathAnalyseQueryService queryService;
	@Autowired
	IOfflineHotMapQueryService hotmapService;

	/**
	 * home页查询数据
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/home", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String home(PathAnalyseControllerQueryParam param){
		
		try {
			validateParam(param);
		
		
			PathAnalyseQueryParam queryParam = buildQueryParam(param);
			
			Map<String, Object> map = new HashMap<String, Object>();
			//增加datasource【plus】查询逻辑
			if("Plus-PC".equalsIgnoreCase(queryParam.getDatasource().trim().toLowerCase())
					|| "Plus-WAP".equalsIgnoreCase(queryParam.getDatasource().trim().toLowerCase())
					|| "Plus-LOG".equalsIgnoreCase(queryParam.getDatasource().trim().toLowerCase())){
				map = queryService.queryPathAnalyseData(queryParam);
			}else{
				//汇总
				PageResponse summary = null;
				if(StringUtils.isEmpty(param.getPrefixurl())){
					summary = queryService.querySummaryOfPagetype(queryParam);
					String description = buildDescription(queryParam);
					summary.setPageDescription(description);
				}else{
					QueryParam hotmapParam = buildQueryParamForHotmap(queryParam);
					summary = hotmapService.querySummaryOfPage(hotmapParam);
				}
				
				//登录页
				PathAnalyseQueryResult directEntry = queryService.queryDirectEntry(queryParam);
				//上一页topN
				List<PathAnalyseQueryResult> topNEntry = queryService.queryTopNEntries(queryParam);
	
				buildOtherResultForEntry(topNEntry, directEntry, summary, queryParam);
	
				//退出页
				PathAnalyseQueryResult directExit = queryService.queryDirectExit(queryParam);
				//下一页topN
				List<PathAnalyseQueryResult> topNFollow = queryService.queryTopNFollows(queryParam);
				
				int referrerPagetypePv = queryService.queryPVOfReferrerPagetype(queryParam);
				
				buildOtherResultForFollow(topNFollow, directExit, referrerPagetypePv, queryParam);
				
				
				map.put("summary", summary);
				map.put("directEntry", directEntry);
				map.put("directExit", directExit);
				map.put("topNEntry", topNEntry);
				map.put("topNFollow", topNFollow);
			}
			
			return JSON.toJSONString(map, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty);
		
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return JSON.toJSONString(new HashMap(), SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty);
		}
	}
	
	/**
	 * 构建热力图查询参数
	 * @param queryParam
	 * @return
	 */
	private QueryParam buildQueryParamForHotmap(PathAnalyseQueryParam queryParam) {
		QueryParam param = new QueryParam();
		param.setPageUrl(queryParam.getPrefixUrl());
		param.setDataSource(queryParam.getDatasource());
		param.setStartTime(queryParam.getVisitStartDay());
		param.setEndTime(queryParam.getVisitEndDay());
		param.setQueryResource(false);
		
		return param;
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

	/**
	 * 上一页topN的翻页查询
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/topNEntry", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String queryTopNEntry(PathAnalyseControllerQueryParam param){
		
		try {
			validateParam(param);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return JSON.toJSONString(new HashMap(), SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty);
		}
		
		PathAnalyseQueryParam queryParam = buildQueryParam(param);
		
		Map<String, Object> map = new HashMap<String, Object>();
		//增加datasource【plus】查询逻辑
		if("Plus-PC".equalsIgnoreCase(queryParam.getDatasource().trim().toLowerCase())
				|| "Plus-WAP".equalsIgnoreCase(queryParam.getDatasource().trim().toLowerCase())
				|| "Plus-LOG".equalsIgnoreCase(queryParam.getDatasource().trim().toLowerCase())){
			map = queryService.queryTopNEntriesData(queryParam);
		}else{
			//上一页topN
			List<PathAnalyseQueryResult> topNEntry = queryService.queryTopNEntries(queryParam);
			
			map.put("topNEntry", topNEntry);
		}
		
		return JSON.toJSONString(map, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty);
	}
	
	/**
	 * 下一页TopN的翻页查询
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/topNFollow", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String queryTopNFollow(PathAnalyseControllerQueryParam param){
		
		try {
			validateParam(param);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return JSON.toJSONString(new HashMap(), SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty);
		}
		
		PathAnalyseQueryParam queryParam = buildQueryParam(param);
		
		Map<String, Object> map = new HashMap<String, Object>();
		//增加datasource【plus】查询逻辑
		if("Plus-PC".equalsIgnoreCase(queryParam.getDatasource().trim().toLowerCase())
				|| "Plus-WAP".equalsIgnoreCase(queryParam.getDatasource().trim().toLowerCase())
				|| "Plus-LOG".equalsIgnoreCase(queryParam.getDatasource().trim().toLowerCase())){
			map = queryService.queryTopNFollowsData(queryParam);
		}else{
			//下一页topN
			List<PathAnalyseQueryResult> topNFollow = queryService.queryTopNFollows(queryParam);
			
			map.put("topNFollow", topNFollow);
		}
		return JSON.toJSONString(map, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty);
	}
	
	/**
	 * 校验查询参数
	 * @param param
	 * @throws Exception
	 */
	private void validateParam(PathAnalyseControllerQueryParam param)throws Exception{
		if(StringUtils.isEmpty(param.getPageSite()) && StringUtils.isEmpty(param.getPageChannel()) && StringUtils.isEmpty(param.getPageType()) && StringUtils.isEmpty(param.getPrefixurl()))
			throw new Exception("网站类型和url都为空");
		
		if(StringUtils.isEmpty(param.getStartTime()))
			throw new Exception("查询的起始时间为空");
		
		if(StringUtils.isEmpty(param.getEndTime()))
			throw new Exception("查询的结束时间为空");
		
		if(StringUtils.isEmpty(param.getDatasource()))
			throw new Exception("查询datasource为空");
	}

	/**
	 * 构建服务层查询param
	 * @param param
	 * @return
	 */
	private PathAnalyseQueryParam buildQueryParam(PathAnalyseControllerQueryParam param) {
		PathAnalyseQueryParam queryParam = new PathAnalyseQueryParam();
		
		queryParam.setPageSite(param.getPageSite());
		queryParam.setPageChannel(param.getPageChannel());
		queryParam.setPageType(param.getPageType());
		queryParam.setPrefixUrl(param.getPrefixurl());
		queryParam.setDatasource(param.getDatasource());
		queryParam.setChannel(param.getChannel());
		
		queryParam.setVisitStartDay(param.getStartTime().replace("-", ""));
		queryParam.setVisitEndDay(param.getEndTime().replace("-", ""));
		
		//pageIndex
		if(param.getPageIndex()==null)
			queryParam.setPageIndex(1);
		else
			queryParam.setPageIndex(Integer.parseInt(param.getPageIndex()));
		
		//pageStart
		queryParam.setPageStart((queryParam.getPageIndex()-1) * queryParam.getPageSize());
		
		//pageSize
		if(param.getPageSize()==null)
			queryParam.setPageSize(DEFAULT_PAGE_SIZE-1);
		else
			queryParam.setPageSize(Integer.parseInt(param.getPageSize())-1);
		
		//topNGroupColumn
		if(StringUtils.isNotEmpty(param.getGroupType())){
			queryParam.setTopNGroupColumn(param.getGroupType().toLowerCase());
		}else if(StringUtils.isNotEmpty(queryParam.getPageType())){
			queryParam.setTopNGroupColumn("pagetype");
		}else if(StringUtils.isNotEmpty(queryParam.getPageChannel())){
			queryParam.setTopNGroupColumn("pagechannel");
		}else if(StringUtils.isNotEmpty(queryParam.getPageSite())){
			queryParam.setTopNGroupColumn("pagesite");
		}else if(StringUtils.isNotEmpty(queryParam.getPrefixUrl())){
			queryParam.setTopNGroupColumn("prefixurl");
		}
		
		return queryParam;
	}
}
