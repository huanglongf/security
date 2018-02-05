package com.gome.service;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gome.Controller.model.ChannelResponse;
import com.gome.Controller.model.PageResponse;
import com.gome.Controller.model.QueryParam;
import com.gome.Controller.model.ResourceResponse;
import com.gome.elasticsearch.ESAggService;

/**
 * 实时数据
 * 
 * @author chixiaoyong
 *
 */
@Service
public class ActualtimeHotMapService {

	private Logger logger = Logger.getLogger(ActualtimeHotMapService.class);

	@Autowired
	private ESAggService service;

	
	/**
	 * 查询页面的信息
	 * 
	 * @param params
	 * @param client
	 * @return
	 */
	public String queryPage(QueryParam params) {

		Map<String, Object> map = new LinkedHashMap<String, Object>();

		Map<String, Object> res = query(params);
		map.put("datas", res);
		String json = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue,
				SerializerFeature.WriteNullStringAsEmpty);

		return json;
	}

	/**
	 * 查询PC首页的信息
	 * 
	 * @param param
	 * @return
	 */
	public Map<String, Object> query(QueryParam param) {

		logger.info(param.toString());

		if (param.getStartTime() == null || param.getEndTime() == null) {
			logger.error("QueryParam startTime endTime is null");
			return new HashMap<String, Object>();
		}

		String index = getIndex();

		logger.info(" index = " + index);

		PageResponse pageResponse = service.queryPageOneDay(param,  index);

		pageResponse.setStartTime(param.getStartTime() + " 00:00");

		Map<String, ResourceResponse> resources = queryALLResource(param, pageResponse.getUv(),
				pageResponse.getVistor(), pageResponse.getClick());

		Map<String, Object> map = new LinkedHashMap<String, Object>();

		map.put("page", pageResponse);

		map.put("resource", resources);

		return map;
	}

	/**
	 * 查询PC所有的资源位
	 * 
	 * @param param
	 * @param esService
	 * @param pageuv
	 * @param pagevistors
	 * @param click
	 * @return
	 */
	public Map<String, ResourceResponse> queryALLResource(QueryParam param, double pageuv, double pagevistors,
			double pageclick) {

		logger.info(param.toString());

		String index = getIndex();

		logger.info(" index = " + index);

		Map<String, ResourceResponse> resMap = service.groupByInitcmp(param, index, pageuv, pagevistors,
				pageclick);

		return resMap;
	}

	/**
	 * 查询活动页的
	 * 
	 * @param pageUrl
	 * @param startTime
	 * @param endTime
	 * @param datapage
	 * @return
	 */

	public String chart(QueryParam param) {

		if (param.getStartTime() == null || param.getEndTime() == null) {
			logger.error("QueryParam startTime endTime is null");
			// return new HashMap<String, Object>();
		}

		// String index=getIndex()+dateFormat.format(new Date());

		String index = getIndex();

		logger.info(" index = " + index);
		// 查询startTime到endTime全站的数据

		Map<String, Object> map = new LinkedHashMap<String, Object>();

		Map<String, PageResponse> pageDatasMap = activePageHour(param, index);

		Map<String, ChannelResponse> responseChanelMap = service.queryChanelByPageMerageAll(param, index);

		map.put("pageDatas", pageDatasMap);
		map.put("channels", responseChanelMap);

		String json = JSONObject.toJSONString(map, SerializerFeature.WriteMapNullValue,
				SerializerFeature.WriteNullStringAsEmpty);

		return json;
	}

	/**
	 * 活动页按小时分组的数据
	 * 
	 * @param client
	 * @param param
	 * @param index
	 * @param dataSource
	 * @return
	 */
	public Map<String, PageResponse> activePageHour( QueryParam param, String index) {

		// 查询页面小时数据
		Map<String, PageResponse> pageDatasMap = service.queryByPageHour(param,  index);

		return pageDatasMap;
	}

	/**
	 * 根据资源位code查询历史信息
	 * 
	 * @param pageUrl
	 * @param startTime
	 * @param endTime
	 * @param initCmp
	 * @return
	 */

	/**
	 * 查询资源位的接口
	 * 
	 * @param pageUrl
	 * @param startTime
	 * @param endTime
	 * @param initCmp
	 * @return
	 */
	public String queryResourceHistory(QueryParam param) {

		Map<String, Object> map = queryhistory(param);

		String json = JSON.toJSONString(map, SerializerFeature.WriteNullListAsEmpty,
				SerializerFeature.WriteNullStringAsEmpty);

		return json;
	}

	/**
	 * 查询页面的PV，UV
	 * 
	 * @param param
	 * @return
	 */
	public Map<String, Object> queryhistory(QueryParam param) {

		logger.info(param.toString());

		Map<String, Object> map = new HashMap<String, Object>();

		String index = getIndex();

		ResourceResponse rrs = service.queryResourceByCode(param, index);

		LinkedList<ResourceResponse> resources = service.queryActiveByCode(param, index);

		// 对资源为排序
		Collections.sort(resources, new Comparator<ResourceResponse>() {

			public int compare(ResourceResponse o1, ResourceResponse o2) {
				if (o1.getClick() > o2.getClick()) {
					return -1;
				} else if (o1.getClick() < o2.getClick()) {
					return 1;
				}
				return 0;
			}

		});
		map.put("total", rrs);

		map.put("active", resources);
		return map;
	}

	public String getIndex() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String index = "current-session" + dateFormat.format(new Date());

		return index;
	}

	public List<String> queryMaxUvPage(int size,QueryParam param,String index) {
		

		return service.queryMaxUv(size, index, param);
	}
	
}
