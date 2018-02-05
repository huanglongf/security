package com.gome.elasticsearch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.InternalFilter;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.nested.InternalNested;
import org.elasticsearch.search.aggregations.bucket.nested.InternalReverseNested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Order;
import org.elasticsearch.search.aggregations.metrics.cardinality.InternalCardinality;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.scripted.InternalScriptedMetric;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.springframework.stereotype.Repository;

import com.gome.Controller.model.ChannelResponse;
import com.gome.Controller.model.PageResponse;
import com.gome.Controller.model.QueryParam;
import com.gome.Controller.model.ResourceResponse;

/**
 * 
 * @author chixiaoyong
 *
 */
@Repository
public class ESAggService {

	private Logger logger = Logger.getLogger(ESAggService.class);

	@Resource
	ESClient esClient;

	//private Client client = esClient.getTransportClient();

	/**
	 * 
	 * 查询某个页面小时的pv,uv,等。
	 * 
	 * @param param
	 * @param client
	 * @return
	 */
	public Map<String, PageResponse> queryByPageHour(QueryParam param, String index) {

		BoolQueryBuilder boolQueryBuilder = getQuery(param);

		AggregationBuilder pageTakeOrder = aggPageTakeOrderAndAmount(param.getPageUrl());

		SearchRequestBuilder req =esClient.getTransportClient().prepareSearch(index).setTypes("sessions").setQuery(boolQueryBuilder)

				.addAggregation(
						AggregationBuilders.dateHistogram("group").field("visitStartTime").format("H点")
								.interval(DateHistogramInterval.HOUR).minDocCount(1)
								// 计算全站信息
								.subAggregation(AggregationBuilders.cardinality("uvAllStation").field("visitorId")
										.precisionThreshold(400))
								.subAggregation(AggregationBuilders.count("vistorAllstation").field("sessionId"))
								.subAggregation(AggregationBuilders.filter("bounceALLStationFilter").filter(
										QueryBuilders.termQuery("pageViewsCount", 1)).subAggregation(
												AggregationBuilders.count("bounceALLStation").field("sessionId")))
								.subAggregation(
										AggregationBuilders.nested("nestpvALlStation").path("pageviews").subAggregation(
												AggregationBuilders.count("pvALLStation").field("pageviews.url")))

								// 计算点击量
								.subAggregation(AggregationBuilders.nested("nestClick").path("pageviews")
										.subAggregation(AggregationBuilders.filter("clickFilter")
												.filter(QueryBuilders.boolQuery()
														.must(QueryBuilders.termQuery("pageviews.referrerPrefixURL",
																param.getPageUrl()))
														.mustNot(QueryBuilders.termQuery("pageviews.intcmp", "")))
												.subAggregation(
														AggregationBuilders.count("click").field("pageviews.intcmp"))))
								// 计算Page信息
								.subAggregation(AggregationBuilders.filter("filter")
										.filter(QueryBuilders.boolQuery()
												.must(QueryBuilders.nestedQuery("pageviews",
														QueryBuilders.termQuery("pageviews.prefixUrl",
																param.getPageUrl()))))
										.subAggregation(AggregationBuilders.cardinality("uv").field("visitorId")
												.precisionThreshold(400))
										.subAggregation(AggregationBuilders.count("vistor").field("sessionId")))
								.subAggregation(
										AggregationBuilders.filter("bounceRateFilter")
												.filter(QueryBuilders.boolQuery()
														.must(QueryBuilders.termQuery("pageViewsCount", 1))
														.must(QueryBuilders.termQuery("exitURLnoParam",
																param.getPageUrl())))
												.subAggregation(
														AggregationBuilders.count("bounceRate").field("sessionId")))
								.subAggregation(AggregationBuilders.nested("nestEntries").path("pageviews")
										.subAggregation(AggregationBuilders.filter("filter")
												.filter(QueryBuilders.boolQuery()
														.must(QueryBuilders.termQuery("pageviews.prefixUrl",
																param.getPageUrl()))
														.must(QueryBuilders.termQuery("pageviews.sepPv", 1)))
												.subAggregation(
														AggregationBuilders.count("entries").field("pageviews.url"))))
								.subAggregation(AggregationBuilders.nested("nestpv").path("pageviews")
										.subAggregation(AggregationBuilders.filter("filter")
												.filter(QueryBuilders.boolQuery()
														.must(QueryBuilders.termQuery("pageviews.prefixUrl",
																param.getPageUrl())))
												.subAggregation(AggregationBuilders.count("pv").field("pageviews.url"))
												.subAggregation(AggregationBuilders.sum("viewTime")
														.field("pageviews.viewTime"))))
								.subAggregation(
										AggregationBuilders.filter("exitRatefilter")
												.filter(QueryBuilders.boolQuery()
														.must(QueryBuilders.termQuery("exitURLnoParam",
																param.getPageUrl())))
												.subAggregation(
														AggregationBuilders.count("exitRate").field("sessionId")))
								.subAggregation(pageTakeOrder))
				.setSize(0);

		logger.info("queryByPageHour req > " + req);

		SearchResponse searchResponse = req.execute().actionGet();

		Aggregations aggall = searchResponse.getAggregations();

		logger.info("queryByPageHour response > " + searchResponse);

		Map<String, PageResponse> pages = new HashMap<String, PageResponse>();

		Histogram terms = aggall.get("group");

		for (Histogram.Bucket bucket : terms.getBuckets()) {

			Aggregations agg = bucket.getAggregations();

			String key = bucket.getKeyAsString();

			PageResponse pageResponse = new PageResponse();

			// uv
			InternalCardinality uvALL = agg.get("uvAllStation");

			ValueCount vistorALL = agg.get("vistorAllstation");

			ValueCount pvALL = ((InternalNested) agg.get("nestpvALlStation")).getAggregations().get("pvALLStation");

			ValueCount bounceALL = ((InternalFilter) agg.get("bounceALLStationFilter")).getAggregations()
					.get("bounceALLStation");

			InternalCardinality uv = ((InternalFilter) agg.get("filter")).getAggregations().get("uv");

			ValueCount vistor = ((InternalFilter) agg.get("filter")).getAggregations().get("vistor");

			ValueCount bounce = ((InternalFilter) agg.get("bounceRateFilter")).getAggregations().get("bounceRate");

			ValueCount pv = ((InternalFilter) ((InternalNested) agg.get("nestpv")).getAggregations().get("filter"))
					.getAggregations().get("pv");

			Sum viewTime = ((InternalFilter) ((InternalNested) agg.get("nestpv")).getAggregations().get("filter"))
					.getAggregations().get("viewTime");

			ValueCount click = ((InternalFilter) ((InternalNested) agg.get("nestClick")).getAggregations()
					.get("clickFilter")).getAggregations().get("click");

			ValueCount exitCount = ((InternalFilter) agg.get("exitRatefilter")).getAggregations().get("exitRate");

			ValueCount entries = ((InternalFilter) ((InternalNested) agg.get("nestEntries")).getAggregations()
					.get("filter")).getAggregations().get("entries");

			InternalScriptedMetric orderCount = ((InternalFilter) ((InternalNested) agg.get("nestOrderCount"))
					.getAggregations().get("filter")).getAggregations().get("orderCount");

			InternalScriptedMetric amount = ((InternalFilter) ((InternalNested) agg.get("nestOrderCount"))
					.getAggregations().get("filter")).getAggregations().get("amount");

			pageResponse.setBounce(bounce.getValue(), vistor.getValue());
			pageResponse.setBounceRate(bounce.getValue(), bounceALL.getValue());
			pageResponse.setEntriesCount(entries.getValue());
			pageResponse.setExitCount(exitCount.getValue());
			pageResponse.setExitRate(pageResponse.getExitCount(), vistor.getValue());
			pageResponse.setOrderTotalAmount(ScriptConvDouble(amount));
			pageResponse.setPv(pv.getValue());
			pageResponse.setPvRate(pageResponse.getPv(), pvALL.getValue());
			pageResponse.setTakeOrderRate(ScriptConvDouble(orderCount), uv.getValue());
			pageResponse.setUv(uv.getValue());
			pageResponse.setUvRate(pageResponse.getUv(), uvALL.getValue());
			pageResponse.setViewTime(viewTime.getValue());
			pageResponse.setVistor(vistor.getValue());
			pageResponse.setVistorRate(pageResponse.getVistor(), vistorALL.getValue());
			pageResponse.setClick(click.getValue());
			pageResponse.setTakeOrderCount(ScriptConvDouble(orderCount));
			pages.put(key, pageResponse);
		}

		return pages;
	}

	/**
	 *
	 * 查询某一天的PV，UV
	 * 
	 * @param param
	 * @param client
	 * @return
	 */
	public PageResponse queryPageOneDay(QueryParam param, String index) {

		SearchRequestBuilder req =esClient.getTransportClient().prepareSearch(index).setTypes("sessions");

		BoolQueryBuilder boolQueryBuilder = getQuery(param);

		req.setQuery(boolQueryBuilder);

		req = aggAllStation(req);

		// 查询全站的数据

		// 计算页面的
		req = aggPageInfo(param.getPageUrl(), req);

		req.addAggregation(aggPageClick(param.getPageUrl()))
				.addAggregation(aggPageTakeOrderAndAmount(param.getPageUrl())).setSize(0);

		logger.info("queryPageOneDay request:" + req);

		SearchResponse searchResponse = req.execute().actionGet();

		logger.info("queryPageOneDay response:" + searchResponse);

		Aggregations aggAll = searchResponse.getAggregations();

		PageResponse pageResponse = new PageResponse();

		InternalCardinality uvaggStation = aggAll.get("uvAllstation");

		ValueCount vistoraggStation = aggAll.get("vistorAllstation");

		ValueCount bounceAggStation = ((InternalFilter) aggAll.get("bounceStation")).getAggregations()
				.get("bounceAllStation");

		ValueCount pvaggStation = ((InternalNested) aggAll.get("pvAllStation")).getAggregations().get("pv");

		Aggregations page = ((InternalFilter) aggAll.get("page")).getAggregations();

		InternalCardinality uvPage = page.get("uv");

		ValueCount vistorPage = page.get("vistor");

		ValueCount bouncePage = ((InternalFilter) aggAll.get("bouncePage")).getAggregations().get("bouncePage");

		ValueCount entries = ((InternalFilter) ((InternalNested) aggAll.get("nestEntries")).getAggregations()
				.get("filter")).getAggregations().get("entries");

		Aggregations nestpv = ((InternalFilter) ((InternalNested) aggAll.get("nestpv")).getAggregations().get("filter"))
				.getAggregations();

		ValueCount pvPage = nestpv.get("pv");

		Max maxTime = nestpv.get("maxTime");

		Sum viewTime = nestpv.get("viewTime");

		ValueCount clickPage = ((InternalFilter) ((InternalNested) aggAll.get("nestclick")).getAggregations()
				.get("filter")).getAggregations().get("click");

		InternalScriptedMetric orderCountPage = ((InternalFilter) ((InternalNested) aggAll.get("nestOrderCount"))
				.getAggregations().get("filter")).getAggregations().get("orderCount");

		InternalScriptedMetric amount = ((InternalFilter) ((InternalNested) aggAll.get("nestOrderCount"))
				.getAggregations().get("filter")).getAggregations().get("amount");

		ValueCount exitCountPage = ((InternalFilter) aggAll.get("exitCountfilter")).getAggregations().get("exitCount");

		pageResponse.setBounce(bouncePage.getValue(), vistorPage.getValue());
		pageResponse.setBounceRate(bouncePage.getValue(), bounceAggStation.getValue());
		pageResponse.setClick(clickPage.getValue());
		pageResponse.setEntriesCount(entries.getValue());
		pageResponse.setExitCount(exitCountPage.getValue());
		pageResponse.setExitRate(exitCountPage.getValue(), vistorPage.getValue());
		pageResponse.setPv(pvPage.getValue());
		pageResponse.setPvRate(pageResponse.getPv(), pvaggStation.getValue());
		pageResponse.setTakeOrderRate(ScriptConvDouble(orderCountPage), uvPage.getValue());
		pageResponse.setUv(uvPage.getValue());
		pageResponse.setUvRate(pageResponse.getUv(), uvaggStation.getValue());
		pageResponse.setViewTime(viewTime.getValue());
		pageResponse.setVistor(vistorPage.getValue());
		pageResponse.setVistorRate(pageResponse.getVistor(), vistoraggStation.getValue());
		pageResponse.setTakeOrderCount(ScriptConvDouble(orderCountPage));
		pageResponse.setOrderTotalAmount(ScriptConvDouble(amount));

		if (Double.isInfinite(maxTime.getValue()) || Double.isNaN(maxTime.getValue())) {
			pageResponse.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));

		} else {
			pageResponse.setEndTime(maxTime.getValueAsString());
		}
		return pageResponse;
	}

	/**
	 * 用实时的ES脚本后续浏览量去重
	 * 
	 * @param param
	 * @param client
	 * @param index
	 * @param dataSource
	 * @return
	 */
	public Map<String, ResourceResponse> groupByInitcmp(QueryParam param, String index, double pageuv,
			double pagevistors, double pageclick) {

		// 计算后续平均浏览量
		Script initPvScript = new Script(" _agg['pv']=[:] ");

		Script mapPvScript = new Script(
				"  def key=doc['_uid'].value+'\\001'+doc['pageviews.intcmp'].value+'\\001'+doc['pageviews.url'].value; def flowPV=doc['pageviews.followingPV'].value;  if(!_agg.pv.containsKey(key) ) { _agg.pv.put(key,flowPV);}  else { tmp=_agg.pv.get(key); if(flowPV >tmp) {  _agg.pv.put(key,flowPV); } } ");

		Script combinePvScript = new Script(" sum=0; for( a in _agg.pv){  sum +=a.value } ;return sum  ");

		Script reducePvScript = new Script(" sum=0;for (a in _aggs) {if(a !=null){ sum += a }}; return sum ");

		// 计算参与订单金额
		Script initOrdersAmmountScript = new Script(" _agg['pv']=[:] ");

		Script mapOrdersAmmountScript = new Script(
				"  def key=doc['_uid'].value +'\\001'+doc['pageviews.intcmp'].value+'\\001'+doc['pageviews.url'].value; def participationOrdersAmmount=doc['pageviews.participationOrdersAmmount'].value;  if(!_agg.pv.containsKey(key) ) { _agg.pv.put(key,participationOrdersAmmount);} else { tmp=_agg.pv.get(key); if(participationOrdersAmmount >tmp) {  _agg.pv.put(key,participationOrdersAmmount); } }  ");

		Script combineOrdersAmmountScript = new Script(" sum=0; for( a in _agg.pv){  sum +=a.value } ;return sum  ");

		Script reduceOrdersAmmountScript = new Script(
				" sum=0;for (a in _aggs) { if(a !=null){sum += a} }; return sum ");

		// 计算参与订单量
		Script initOrdersCountScript = new Script(" _agg['pv']=[:] ");

		Script mapOrdersCountScript = new Script(
				"  def key=doc['_uid'].value+'\\001'+doc['pageviews.intcmp'].value+'\\001'+doc['pageviews.url'].value; def participationOrdersCount=doc['pageviews.participationOrdersCount'].value;  if(!_agg.pv.containsKey(key) ) { _agg.pv.put(key,participationOrdersCount);}  else { tmp=_agg.pv.get(key); if(participationOrdersCount >tmp) {  _agg.pv.put(key,participationOrdersCount); } } ");

		Script combineOrdersCountScript = new Script(" sum=0; for( a in _agg.pv){  sum +=a.value } ;return sum  ");

		Script reduceOrdersCountScript = new Script(" sum=0;for (a in _aggs) {if(a !=null){ sum += a} }; return sum ");

		BoolQueryBuilder boolQueryBuilder = getQuery(param);

		SearchRequestBuilder req =esClient.getTransportClient().prepareSearch(index).setTypes("sessions").setQuery(boolQueryBuilder)

				.addAggregation(aggTotalOrder())
				.addAggregation(AggregationBuilders.nested("netted").path("pageviews")
						.subAggregation(AggregationBuilders.filter("syFilter")
								.filter(QueryBuilders.boolQuery().must(
										QueryBuilders.termQuery("pageviews.referrerPrefixURL", param.getPageUrl()))
										.mustNot(QueryBuilders.termQuery("pageviews.intcmp", "")))
								.subAggregation(AggregationBuilders.terms("clickTerm")
										.script(new Script(
												"doc['pageviews.intcmp'].value.substring(doc['pageviews.intcmp'].value.indexOf('-')+1)"))
										.size(0)
										.subAggregation(AggregationBuilders.count("click").field("pageviews.intcmp"))
										.subAggregation(AggregationBuilders.scriptedMetric("followingPV")
												.initScript(initPvScript).mapScript(mapPvScript)
												.combineScript(combinePvScript).reduceScript(reducePvScript))
										.subAggregation(AggregationBuilders.scriptedMetric("participationOrdersCount")
												.initScript(initOrdersCountScript).mapScript(mapOrdersCountScript)
												.combineScript(combineOrdersCountScript)
												.reduceScript(reduceOrdersCountScript))
										.subAggregation(AggregationBuilders.scriptedMetric("participationOrdersAmmount")
												.initScript(initOrdersAmmountScript).mapScript(mapOrdersAmmountScript)
												.combineScript(combineOrdersAmmountScript)
												.reduceScript(reduceOrdersAmmountScript))
										.subAggregation(AggregationBuilders.reverseNested("parent")
												.subAggregation(AggregationBuilders.count("vistor").field("sessionId"))
												.subAggregation(AggregationBuilders.cardinality("uv").field("visitorId")
														.precisionThreshold(400))))))
				.setSize(0);

		logger.info(" groupByInitcmpScript > \n " + req);

		SearchResponse searchResponse = req.execute().actionGet();

		logger.info(" groupByInitcmpScript > \n " + searchResponse);

		Aggregations agg = searchResponse.getAggregations();

		Map<String, ResourceResponse> resMap = new LinkedHashMap<String, ResourceResponse>();

		ValueCount orderALlcount = ((InternalFilter) ((InternalNested) agg.get("nestorder")).getAggregations()
				.get("filter")).getAggregations().get("orderALlcount");

		Terms termsNested = ((InternalFilter) ((InternalNested) agg.get("netted")).getAggregations().get("syFilter"))
				.getAggregations().get("clickTerm");

		for (Bucket bt : termsNested.getBuckets()) {

			String key = bt.getKey().toString();

			ResourceResponse res = new ResourceResponse();

			ValueCount click = bt.getAggregations().get("click");

			InternalScriptedMetric followingPV = bt.getAggregations().get("followingPV");
			InternalScriptedMetric participationOrdersCount = bt.getAggregations().get("participationOrdersCount");

			InternalScriptedMetric participationOrdersAmmount = bt.getAggregations().get("participationOrdersAmmount");
			InternalReverseNested parentagg = (InternalReverseNested) bt.getAggregations().get("parent");
			ValueCount vistor = parentagg.getAggregations().get("vistor");

			InternalCardinality uv = parentagg.getAggregations().get("uv");

			res.setClick(click.getValue());
			res.setUv(uv.getValue());
			res.setClickRate(res.getClick(), pageclick);
			res.setPv(ScriptConvDouble(followingPV));
			res.setAvgPv(res.getPv(), uv.getValue());
			res.setCode(key);
			res.setOrderCount(ScriptConvDouble(participationOrdersCount));
			res.setOrderAllRate(res.getOrderCount(), orderALlcount.getValue());
			res.setSaleAmount(ScriptConvDouble(participationOrdersAmmount));
			res.setTakeOrderRate(res.getOrderCount(), res.getUv());
			res.setVistor(vistor.getValue());
			res.setUvRate(res.getUv(), pageuv);
			res.setVistorRate(res.getVistor(), pagevistors);
			resMap.put(res.getCode(), res);
		}

		return resMap;

	}

	/**
	 * 用ES脚本后续浏览量去重
	 * 
	 * @param param
	 * @param client
	 * @param index
	 * @param dataSource
	 * @return
	 */
	public ResourceResponse queryResourceByCode(QueryParam param, String index) {

		Script initPvScript = new Script(" _agg['pv']=[:] ");

		Script mapPvScript = new Script(
				"  def key=doc['_uid'].value+'\\001'+doc['pageviews.intcmp'].value+'\\001'+doc['pageviews.url'].value; def flowPV=doc['pageviews.followingPV'].value;  if(!_agg.pv.containsKey(key) ) { _agg.pv.put(key,flowPV);}  else { tmp=_agg.pv.get(key); if(flowPV >tmp) {  _agg.pv.put(key,flowPV); } } ");

		Script combinePvScript = new Script(" sum=0; for( a in _agg.pv){  sum +=a.value } ;return sum  ");

		Script reducePvScript = new Script(" sum=0;for (a in _aggs) {if(a !=null){ sum += a }}; return sum ");

		Script initOrdersAmmountScript = new Script(" _agg['pv']=[:] ");

		Script mapOrdersAmmountScript = new Script(
				"  def key=doc['_uid'].value +'\\001'+doc['pageviews.intcmp'].value+'\\001'+doc['pageviews.url'].value; def participationOrdersAmmount=doc['pageviews.participationOrdersAmmount'].value;  if(!_agg.pv.containsKey(key) ) { _agg.pv.put(key,participationOrdersAmmount);} else { tmp=_agg.pv.get(key); if(participationOrdersAmmount >tmp) {  _agg.pv.put(key,participationOrdersAmmount); } }  ");

		Script combineOrdersAmmountScript = new Script(" sum=0; for( a in _agg.pv){  sum +=a.value } ;return sum  ");

		Script reduceOrdersAmmountScript = new Script(
				" sum=0;for (a in _aggs) { if(a !=null){sum += a} }; return sum ");

		Script initOrdersCountScript = new Script(" _agg['pv']=[:] ");

		Script mapOrdersCountScript = new Script(
				"  def key=doc['_uid'].value+'\\001'+doc['pageviews.intcmp'].value+'\\001'+doc['pageviews.url'].value; def participationOrdersCount=doc['pageviews.participationOrdersCount'].value;  if(!_agg.pv.containsKey(key) ) { _agg.pv.put(key,participationOrdersCount);}  else { tmp=_agg.pv.get(key); if(participationOrdersCount >tmp) {  _agg.pv.put(key,participationOrdersCount); } } ");

		Script combineOrdersCountScript = new Script(" sum=0; for( a in _agg.pv){  sum +=a.value } ;return sum  ");

		Script reduceOrdersCountScript = new Script(" sum=0;for (a in _aggs) {if(a !=null){ sum += a} }; return sum ");

		BoolQueryBuilder boolQueryBuilder = getQuery(param);

		SearchRequestBuilder req =esClient.getTransportClient().prepareSearch(index).setTypes("sessions").setQuery(boolQueryBuilder)
				.addAggregation(AggregationBuilders.nested("nestorder").path("events")
						.subAggregation(AggregationBuilders.filter("filter")
								.filter(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("events.goalID", "52")))
								.subAggregation(AggregationBuilders.count("orderALlcount").field("events.goalID"))))
				// 计算页面
				.addAggregation(AggregationBuilders.filter("filter")
						.filter(QueryBuilders.boolQuery()
								.must(QueryBuilders.nestedQuery("pageviews",
										QueryBuilders.termQuery("pageviews.prefixUrl", param.getPageUrl()))))
						.subAggregation(
								AggregationBuilders.cardinality("uv").field("visitorId").precisionThreshold(400))
						.subAggregation(AggregationBuilders.count("vistor").field("sessionId")))

				.addAggregation(AggregationBuilders.nested("nestPage").path("pageviews")
						.subAggregation(AggregationBuilders.filter("filter")
								.filter(QueryBuilders.boolQuery()
										.must(QueryBuilders.termQuery("pageviews.referrerPrefixURL",
												param.getPageUrl()))
										.mustNot(QueryBuilders.termQuery("pageviews.intcmp", ""))

								).subAggregation(AggregationBuilders.count("click").field("pageviews.intcmpPage"))))

				.addAggregation(AggregationBuilders.nested("netted").path("pageviews")
						.subAggregation(AggregationBuilders.filter("syFilter").filter(QueryBuilders.boolQuery()
								.mustNot(QueryBuilders.termQuery("pageviews.intcmp", ""))
								.must(QueryBuilders.termQuery("pageviews.referrerPrefixURL", param.getPageUrl()))
								.must(QueryBuilders.scriptQuery(new Script(
										"doc['pageviews.intcmp'].value.substring(doc['pageviews.intcmp'].value.indexOf('-')+1)=='"
												+ param.getIntcmp() + "'"))))
								.subAggregation(AggregationBuilders.count("click").field("pageviews.intcmp"))
								.subAggregation(AggregationBuilders.scriptedMetric("followingPV")
										.initScript(initPvScript).mapScript(mapPvScript).combineScript(combinePvScript)
										.reduceScript(reducePvScript))
								.subAggregation(AggregationBuilders.scriptedMetric("participationOrdersCount")
										.initScript(initOrdersCountScript).mapScript(mapOrdersCountScript)
										.combineScript(combineOrdersCountScript).reduceScript(reduceOrdersCountScript))
								.subAggregation(AggregationBuilders.scriptedMetric("participationOrdersAmmount")
										.initScript(initOrdersAmmountScript).mapScript(mapOrdersAmmountScript)
										.combineScript(combineOrdersAmmountScript)
										.reduceScript(reduceOrdersAmmountScript))
								.subAggregation(AggregationBuilders.reverseNested("parent")
										.subAggregation(
												AggregationBuilders.count("vistor").field("sessionId"))
										.subAggregation(AggregationBuilders.cardinality("uv").field("visitorId")
												.precisionThreshold(400))
										.subAggregation(AggregationBuilders.sum("pvCount").field("pageViewsCount")))))
				.setSize(0);

		logger.info(" queryResourceByCode > \n " + req);

		SearchResponse searchResponse = req.execute().actionGet();

		logger.info(" queryResourceByCode > \n " + searchResponse);

		Aggregations agg = searchResponse.getAggregations();

		InternalCardinality uvPage = ((InternalFilter) agg.get("filter")).getAggregations().get("uv");

		ValueCount vistorPage = ((InternalFilter) agg.get("filter")).getAggregations().get("vistor");

		ValueCount clickPage = ((InternalFilter) ((InternalNested) agg.get("nestPage")).getAggregations().get("filter"))
				.getAggregations().get("click");

		ValueCount orderALlcount = ((InternalFilter) ((InternalNested) agg.get("nestorder")).getAggregations()
				.get("filter")).getAggregations().get("orderALlcount");

		Aggregations filterAgg = ((InternalFilter) ((InternalNested) agg.get("netted")).getAggregations()
				.get("syFilter")).getAggregations();

		ResourceResponse re = new ResourceResponse();
		re.setCode(param.getIntcmp());

		ValueCount clickValue = filterAgg.get("click");
		re.setClick(clickValue.getValue());

		InternalScriptedMetric followingPV = filterAgg.get("followingPV");
		re.setPv(ScriptConvDouble(followingPV));

		InternalScriptedMetric participationOrdersCount = filterAgg.get("participationOrdersCount");
		re.setOrderCount(ScriptConvDouble(participationOrdersCount));

		InternalScriptedMetric participationOrdersAmmount = filterAgg.get("participationOrdersAmmount");
		re.setSaleAmount(ScriptConvDouble(participationOrdersAmmount));

		InternalReverseNested parentagg = (InternalReverseNested) filterAgg.get("parent");

		ValueCount vistorValue = parentagg.getAggregations().get("vistor");

		re.setVistor(vistorValue.getValue());

		InternalCardinality uv = parentagg.getAggregations().get("uv");

		re.setUv(uv.getValue());

		re.setTakeOrderRate(re.getOrderCount(), uv.getValue());

		re.setAvgPv(re.getPv(), uv.getValue());
		re.setOrderAllRate(re.getOrderCount(), orderALlcount.getValue());
		re.setUvRate(re.getUv(), uvPage.getValue());
		re.setVistorRate(re.getVistor(), vistorPage.getValue());
		re.setAvgPv(re.getPv(), re.getVistor());
		re.setClickRate(re.getClick(), clickPage.getValue());

		return re;

	}

	/**
	 * 根据页面查询渠道
	 * 
	 * @param param
	 * @param client
	 * @return
	 */
	public Map<String, ChannelResponse> queryChanelByPageMerageAll(QueryParam param, String index) {

		Script initOrdersCountScript = new Script(" _agg['pv']=[:] ");

		Script mapOrdersCountScript = new Script(
				"  def key=doc['_uid'].value+'\\001'+doc['pageviews.prefixUrl'].value ; def participationOrdersCount=doc['pageviews.participationOrdersCount'].value;  if(!_agg.pv.containsKey(key) ) { _agg.pv.put(key,participationOrdersCount);}  else { tmp=_agg.pv.get(key); if(participationOrdersCount >tmp) {  _agg.pv.put(key,participationOrdersCount); } } ");

		Script combineOrdersCountScript = new Script(" sum=0; for( a in _agg.pv){  sum +=a.value } ;return sum  ");

		Script reduceOrdersCountScript = new Script(" sum=0;for (a in _aggs) {if(a !=null){ sum += a }}; return sum ");

		Script initOrdersAmmountScript = new Script(" _agg['pv']=[:] ");

		Script mapOrdersAmmountScript = new Script(
				"  def key=doc['_uid'].value +'\\001'+doc['pageviews.prefixUrl'].value; def participationOrdersAmmount=doc['pageviews.participationOrdersAmmount'].value;  if(!_agg.pv.containsKey(key) ) { _agg.pv.put(key,participationOrdersAmmount);} else { tmp=_agg.pv.get(key); if(participationOrdersAmmount >tmp) {  _agg.pv.put(key,participationOrdersAmmount); } }  ");

		Script combineOrdersAmmountScript = new Script(" sum=0; for( a in _agg.pv){  sum +=a.value } ;return sum  ");

		Script reduceOrdersAmmountScript = new Script(
				" sum=0;for (a in _aggs) { if(a !=null){sum += a} }; return sum ");

		BoolQueryBuilder boolQueryBuilder = getQuery(param);

		SearchRequestBuilder req =esClient.getTransportClient().prepareSearch(index).setTypes("sessions").setQuery(boolQueryBuilder)

				.addAggregation(
						AggregationBuilders.terms("group").field("channel").size(0)
								// 计算全站
								.subAggregation(AggregationBuilders.cardinality("uvAllStation").field("visitorId")
										.precisionThreshold(400))
								.subAggregation(AggregationBuilders.count("vistorAllstation").field("sessionId"))
								.subAggregation(AggregationBuilders.filter("bounceALLStationFilter").filter(
										QueryBuilders.termQuery("pageViewsCount", 1)).subAggregation(
												AggregationBuilders.count("bounceALLStation").field("sessionId")))
								.subAggregation(
										AggregationBuilders.nested("nestpvALlStation").path("pageviews").subAggregation(
												AggregationBuilders.count("pvALLStation").field("pageviews.url")))

								// 计算渠道点击量
								.subAggregation(AggregationBuilders.nested("nestClick").path("pageviews")
										.subAggregation(AggregationBuilders.filter("clickFilter")
												.filter(QueryBuilders.boolQuery()
														.must(QueryBuilders.termQuery("pageviews.referrerPrefixURL",
																param.getPageUrl()))
														.mustNot(QueryBuilders.termQuery("pageviews.intcmp", "")))
												.subAggregation(
														AggregationBuilders.count("click").field("pageviews.intcmp"))))

								// 计算页面渠道的URL
								.subAggregation(AggregationBuilders.filter("filter")
										.filter(QueryBuilders.boolQuery()
												.must(QueryBuilders.nestedQuery("pageviews",
														QueryBuilders.termQuery("pageviews.prefixUrl",
																param.getPageUrl()))))
										.subAggregation(AggregationBuilders.cardinality("uv").field("visitorId")
												.precisionThreshold(400))
										.subAggregation(AggregationBuilders.count("vistor").field("sessionId")))
								.subAggregation(
										AggregationBuilders.filter("bounceRateFilter")
												.filter(QueryBuilders.boolQuery()
														.must(QueryBuilders.termQuery("pageViewsCount", 1))
														.must(QueryBuilders.termQuery("exitURLnoParam",
																param.getPageUrl())))
												.subAggregation(
														AggregationBuilders.count("bounceRate").field("sessionId")))
								.subAggregation(AggregationBuilders.nested("nestEntries").path("pageviews")
										.subAggregation(AggregationBuilders.filter("filter")
												.filter(QueryBuilders.boolQuery()
														.must(QueryBuilders.termQuery("pageviews.prefixUrl",
																param.getPageUrl()))
														.must(QueryBuilders.termQuery("pageviews.sepPv", 1)))
												.subAggregation(
														AggregationBuilders.count("entries").field("pageviews.url"))))
								.subAggregation(AggregationBuilders.nested("nestpv").path("pageviews")
										.subAggregation(AggregationBuilders.filter("filter")
												.filter(QueryBuilders.boolQuery()
														.must(QueryBuilders.termQuery("pageviews.prefixUrl",
																param.getPageUrl())))
												.subAggregation(AggregationBuilders.count("pv").field("pageviews.url"))
												.subAggregation(
														AggregationBuilders.sum("viewTime").field("pageviews.viewTime"))
												.subAggregation(
														AggregationBuilders.scriptedMetric("participationOrdersCount")
																.initScript(initOrdersCountScript)
																.mapScript(mapOrdersCountScript)
																.combineScript(combineOrdersCountScript)
																.reduceScript(reduceOrdersCountScript))
												.subAggregation(AggregationBuilders.scriptedMetric("amount")
														.initScript(initOrdersAmmountScript)
														.mapScript(mapOrdersAmmountScript)
														.combineScript(combineOrdersAmmountScript)
														.reduceScript(reduceOrdersAmmountScript))))
								.subAggregation(
										AggregationBuilders.filter("exitRatefilter")
												.filter(QueryBuilders.boolQuery()
														.must(QueryBuilders.termQuery("exitURLnoParam",
																param.getPageUrl())))
												.subAggregation(
														AggregationBuilders.count("exitRate").field("sessionId"))))
				.setSize(0);

		logger.info("queryChanelByPage > " + req);

		SearchResponse searchResponse = req.execute().actionGet();

		logger.info("queryChanelByPage > " + searchResponse);

		Aggregations aggall = searchResponse.getAggregations();

		Map<String, ChannelResponse> channels = new LinkedHashMap<String, ChannelResponse>();

		Terms terms = aggall.get("group");

		for (Bucket bucket : terms.getBuckets()) {

			String key = bucket.getKeyAsString();

			Aggregations agg = bucket.getAggregations();

			InternalCardinality uvALL = agg.get("uvAllStation");

			ValueCount vistorALL = agg.get("vistorAllstation");

			ValueCount pvALL = ((InternalNested) agg.get("nestpvALlStation")).getAggregations().get("pvALLStation");

			ValueCount bounceALL = ((InternalFilter) agg.get("bounceALLStationFilter")).getAggregations()
					.get("bounceALLStation");

			InternalCardinality uv = ((InternalFilter) agg.get("filter")).getAggregations().get("uv");

			ValueCount vistor = ((InternalFilter) agg.get("filter")).getAggregations().get("vistor");

			ValueCount bounce = ((InternalFilter) agg.get("bounceRateFilter")).getAggregations().get("bounceRate");

			ValueCount pv = ((InternalFilter) ((InternalNested) agg.get("nestpv")).getAggregations().get("filter"))
					.getAggregations().get("pv");

			Sum viewTime = ((InternalFilter) ((InternalNested) agg.get("nestpv")).getAggregations().get("filter"))
					.getAggregations().get("viewTime");

			ValueCount click = ((InternalFilter) ((InternalNested) agg.get("nestClick")).getAggregations()
					.get("clickFilter")).getAggregations().get("click");

			InternalScriptedMetric participationOrdersCount = ((InternalFilter) ((InternalNested) agg.get("nestpv"))
					.getAggregations().get("filter")).getAggregations().get("participationOrdersCount");

			InternalScriptedMetric amount = ((InternalFilter) ((InternalNested) agg.get("nestpv")).getAggregations()
					.get("filter")).getAggregations().get("amount");

			ValueCount exitcount = ((InternalFilter) agg.get("exitRatefilter")).getAggregations().get("exitRate");

			ValueCount entries = ((InternalFilter) ((InternalNested) agg.get("nestEntries")).getAggregations()
					.get("filter")).getAggregations().get("entries");

			ChannelResponse chanel = new ChannelResponse();
			chanel.setBounce(bounce.getValue(), entries.getValue());
			chanel.setBounceRate(bounce.getValue(), bounceALL.getValue());
			chanel.setCode(key);
			chanel.setEntriesCount(entries.getValue());
			chanel.setExitCount(exitcount.getValue());
			chanel.setExitRate(chanel.getExitCount(), vistor.getValue());
			chanel.setOrderTotalAmount(ScriptConvDouble(amount));
			chanel.setPv(pv.getValue());
			chanel.setPvRate(pv.getValue(), pvALL.getValue());
			chanel.setTakeOrderRate(ScriptConvDouble(participationOrdersCount), uv.getValue());
			chanel.setUv(uv.getValue());
			chanel.setUvRate(chanel.getUv(), uvALL.getValue());
			chanel.setViewTime(viewTime.getValue());
			chanel.setVistor(vistor.getValue());
			chanel.setVistorRate(vistor.getValue(), vistorALL.getValue());
			chanel.setClick(click.getValue());
			chanel.setTakeOrderCount(ScriptConvDouble(participationOrdersCount));

			channels.put(key, chanel);

		}

		return channels;

	}

	/**
	 * 根据资源位的code查询活动
	 * 
	 * @param param
	 * @param client
	 * @param index
	 * @return
	 */
	public LinkedList<ResourceResponse> queryActiveByCode(QueryParam param, String index) {

		Script initPvScript = new Script(" _agg['pv']=[:] ");

		Script mapPvScript = new Script(
				"  def key=doc['_uid'].value+'\\001'+doc['pageviews.url'].value +'\\001'+doc['pageviews.intcmp'].value+'\\001'+doc['pageviews.pageTitle'].value; def flowPV=doc['pageviews.followingPV'].value;  if(!_agg.pv.containsKey(key) ) { _agg.pv.put(key,flowPV);}  else { tmp=_agg.pv.get(key); if(flowPV >tmp) {  _agg.pv.put(key,flowPV); } } ");

		Script combinePvScript = new Script(" sum=0; for( a in _agg.pv){  sum +=a.value } ;return sum  ");

		Script reducePvScript = new Script(" sum=0;for (a in _aggs) { if(a !=null){ sum += a }}; return sum ");

		Script initOrdersAmmountScript = new Script(" _agg['pv']=[:] ");

		Script mapOrdersAmmountScript = new Script(
				"  def key=doc['_uid'].value+'\\001'+doc['pageviews.url'].value +'\\001'+doc['pageviews.intcmp'].value+'\\001'+doc['pageviews.pageTitle'].value; def participationOrdersAmmount=doc['pageviews.participationOrdersAmmount'].value;  if(!_agg.pv.containsKey(key) ) { _agg.pv.put(key,participationOrdersAmmount);} else { tmp=_agg.pv.get(key); if(participationOrdersAmmount >tmp) {  _agg.pv.put(key,participationOrdersAmmount); } }  ");

		Script combineOrdersAmmountScript = new Script(" sum=0; for( a in _agg.pv){  sum +=a.value } ;return sum  ");

		Script reduceOrdersAmmountScript = new Script(
				" sum=0;for (a in _aggs) { if(a !=null){ sum += a } }; return sum ");

		Script initOrdersCountScript = new Script(" _agg['pv']=[:] ");

		Script mapOrdersCountScript = new Script(
				"  def key=doc['_uid'].value+'\\001'+doc['pageviews.url'].value +'\\001'+doc['pageviews.intcmp'].value+'\\001'+doc['pageviews.pageTitle'].value; def participationOrdersCount=doc['pageviews.participationOrdersCount'].value;  if(!_agg.pv.containsKey(key) ) { _agg.pv.put(key,participationOrdersCount);}  else { tmp=_agg.pv.get(key); if(participationOrdersCount >tmp) {  _agg.pv.put(key,participationOrdersCount); } } ");

		Script combineOrdersCountScript = new Script(" sum=0; for( a in _agg.pv){  sum +=a.value } ;return sum  ");

		Script reduceOrdersCountScript = new Script(" sum=0;for (a in _aggs) {if(a !=null){ sum += a }}; return sum ");

		BoolQueryBuilder boolQueryBuilder = getQuery(param);

		SearchRequestBuilder req =esClient.getTransportClient().prepareSearch(index).setTypes("sessions").setQuery(boolQueryBuilder)

				.addAggregation(aggTotalOrder())
				// 计算页面
				.addAggregation(AggregationBuilders.filter("filter")
						.filter(QueryBuilders.boolQuery()
								.must(QueryBuilders.nestedQuery("pageviews",
										QueryBuilders.termQuery("pageviews.prefixUrl", param.getPageUrl()))))
						.subAggregation(
								AggregationBuilders.cardinality("uv").field("visitorId").precisionThreshold(400))
						.subAggregation(AggregationBuilders.count("vistor").field("sessionId")))
				// 计算页面点击量
				.addAggregation(AggregationBuilders.nested("nestPage").path("pageviews")
						.subAggregation(AggregationBuilders.filter("filter")
								.filter(QueryBuilders.boolQuery()
										.must(QueryBuilders.termQuery("pageviews.referrerPrefixURL",
												param.getPageUrl()))
										.mustNot(QueryBuilders.termQuery("pageviews.intcmp", ""))

								).subAggregation(AggregationBuilders.count("click").field("pageviews.url"))))
				// 页面的PV
				.addAggregation(AggregationBuilders.nested("netted").path("pageviews")
						.subAggregation(AggregationBuilders.filter("filter").filter(QueryBuilders.boolQuery()
								.mustNot(QueryBuilders.termQuery("pageviews.intcmp", ""))
								.must(QueryBuilders.termQuery("pageviews.referrerPrefixURL", param.getPageUrl()))
								.must(QueryBuilders.scriptQuery(new Script(
										"doc['pageviews.intcmp'].value.substring(doc['pageviews.intcmp'].value.indexOf('-')+1)=='"
												+ param.getIntcmp() + "'"))))
								.subAggregation(AggregationBuilders.terms("groupSy")
										.script(new Script(
												"doc['pageviews.intcmp'].value+'\\001'+doc['pageviews.pageTitle'].value+'\\001'+doc['pageviews.prefixUrl'].value "))
										.size(0)
										.subAggregation(AggregationBuilders.count("click").field("pageviews.intcmp"))
										.subAggregation(AggregationBuilders.scriptedMetric("followingPV")
												.initScript(initPvScript).mapScript(mapPvScript)
												.combineScript(combinePvScript).reduceScript(reducePvScript))
										.subAggregation(AggregationBuilders.scriptedMetric("participationOrdersCount")
												.initScript(initOrdersCountScript).mapScript(mapOrdersCountScript)
												.combineScript(combineOrdersCountScript)
												.reduceScript(reduceOrdersCountScript))
										.subAggregation(AggregationBuilders.scriptedMetric("participationOrdersAmmount")
												.initScript(initOrdersAmmountScript).mapScript(mapOrdersAmmountScript)
												.combineScript(combineOrdersAmmountScript)
												.reduceScript(reduceOrdersAmmountScript))
										.subAggregation(AggregationBuilders.reverseNested("parent")
												.subAggregation(AggregationBuilders.count("vistor").field("sessionId"))
												.subAggregation(AggregationBuilders.cardinality("uv").field("visitorId")
														.precisionThreshold(400))))))
				.setSize(0);

		logger.info("queryActiveByCode  request > " + req);

		SearchResponse searchResponse = req.execute().actionGet();

		logger.info("queryActiveByCode  response >" + searchResponse);

		Aggregations agg = searchResponse.getAggregations();

		LinkedList<ResourceResponse> resources = new LinkedList<ResourceResponse>();

		ValueCount orderALlcount = ((InternalFilter) ((InternalNested) agg.get("nestorder")).getAggregations()
				.get("filter")).getAggregations().get("orderALlcount");

		Terms termssy = ((InternalFilter) ((InternalNested) agg.get("netted")).getAggregations().get("filter"))
				.getAggregations().get("groupSy");

		InternalCardinality uvPageview = ((InternalFilter) agg.get("filter")).getAggregations().get("uv");

		ValueCount vistorPage = ((InternalFilter) agg.get("filter")).getAggregations().get("vistor");

		ValueCount clickPage = ((InternalFilter) ((InternalNested) agg.get("nestPage")).getAggregations().get("filter"))
				.getAggregations().get("click");

		for (Bucket bucketSy : termssy.getBuckets()) {

			ResourceResponse pageResource = new ResourceResponse();

			String key = bucketSy.getKeyAsString();

			String[] array = key.split("\\001");

			pageResource.setCode(array[0]);
			pageResource.setTitle(array[1]);
			pageResource.setUrl(array[2]);

			ValueCount clickValue = bucketSy.getAggregations().get("click");
			pageResource.setClick(clickValue.getValue());

			InternalScriptedMetric followingPV = bucketSy.getAggregations().get("followingPV");

			pageResource.setPv(ScriptConvDouble(followingPV));

			InternalScriptedMetric participationOrdersCount = bucketSy.getAggregations()
					.get("participationOrdersCount");
			pageResource.setOrderCount(ScriptConvDouble(participationOrdersCount));

			InternalScriptedMetric participationOrdersAmmount = bucketSy.getAggregations()
					.get("participationOrdersAmmount");
			pageResource.setSaleAmount(ScriptConvDouble(participationOrdersAmmount));

			InternalReverseNested parentagg = (InternalReverseNested) bucketSy.getAggregations().get("parent");

			ValueCount vistorValue = parentagg.getAggregations().get("vistor");

			pageResource.setVistor(vistorValue.getValue());

			InternalCardinality uv = parentagg.getAggregations().get("uv");

			pageResource.setUv(uv.getValue());

			pageResource.setClickRate(pageResource.getClick(), clickPage.getValue());
			pageResource.setVistorRate(pageResource.getVistor(), vistorPage.getValue());

			pageResource.setUvRate(pageResource.getUv(), uvPageview.getValue());
			pageResource.setAvgPv(pageResource.getPv(), pageResource.getVistor());
			pageResource.setTakeOrderRate(pageResource.getOrderCount(), pageResource.getUv());
			pageResource.setOrderAllRate(pageResource.getOrderCount(), orderALlcount.getValue());
			resources.add(pageResource);

		}

		return resources;

	}

	/**
	 * 转化脚本执行的结果
	 * 
	 * @param metric
	 * @return
	 */

	public Double ScriptConvDouble(InternalScriptedMetric metric) {

		Object object = metric.aggregation();

		return new Double(object.toString());

	}

	private BoolQueryBuilder getQuery(QueryParam param) {

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

		if (param.getDataSource() != null) {
			boolQueryBuilder.must(QueryBuilders.termQuery("dataSource", param.getDataSource()));
		}

		if (param.getStartTime() != null) {

			boolQueryBuilder.must(QueryBuilders.termQuery("date", param.getStartTime()));
		}

		if (param.getChannel() != null) {
			boolQueryBuilder.must(QueryBuilders.termQuery("channel", param.getChannel()));
		}

		return boolQueryBuilder;

	}

	private AggregationBuilder aggTotalOrder() {

		AggregationBuilder aggregationBuilders = AggregationBuilders.nested("nestorder").path("events")
				.subAggregation(AggregationBuilders.filter("filter")
						.filter(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("events.goalID", "52")))
						.subAggregation(AggregationBuilders.count("orderALlcount").field("events.goalID")));

		return aggregationBuilders;
	}

	/**
	 * 
	 * @param pageUrl
	 * @return
	 */
	private AggregationBuilder aggPageTakeOrderAndAmount(String pageUrl) {

		Script initOrdersCountScript = new Script(" _agg['pv']=[:] ");

		Script mapOrdersCountScript = new Script(
				"  def key=doc['_uid'].value+'\\001'+doc['pageviews.prefixUrl'].value; def participationOrdersCount=doc['pageviews.participationOrdersCount'].value;  if(!_agg.pv.containsKey(key) ) { _agg.pv.put(key,participationOrdersCount);}  else { tmp=_agg.pv.get(key); if(participationOrdersCount >tmp) {  _agg.pv.put(key,participationOrdersCount); } } ");

		Script combineOrdersCountScript = new Script(" sum=0; for( a in _agg.pv){  sum +=a.value } ;return sum  ");

		Script reduceOrdersCountScript = new Script(" sum=0;for (a in _aggs) { if(a !=null){ sum += a} }; return sum ");

		Script initOrdersAmmountScript = new Script(" _agg['pv']=[:] ");

		Script mapOrdersAmmountScript = new Script(
				"  def key=doc['_uid'].value +'\\001'+doc['pageviews.prefixUrl'].value; def participationOrdersAmmount=doc['pageviews.participationOrdersAmmount'].value;  if(!_agg.pv.containsKey(key) ) { _agg.pv.put(key,participationOrdersAmmount);} else { tmp=_agg.pv.get(key); if(participationOrdersAmmount >tmp) {  _agg.pv.put(key,participationOrdersAmmount); } }  ");

		Script combineOrdersAmmountScript = new Script(" sum=0; for( a in _agg.pv){  sum +=a.value } ;return sum  ");

		Script reduceOrdersAmmountScript = new Script(
				" sum=0;for (a in _aggs) { if(a !=null){sum += a} }; return sum ");

		AggregationBuilder pageTakeOrderAgg = AggregationBuilders.nested("nestOrderCount").path("pageviews")
				.subAggregation(AggregationBuilders.filter("filter")
						.filter(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("pageviews.prefixUrl", pageUrl)))
						.subAggregation(AggregationBuilders.scriptedMetric("orderCount")
								.initScript(initOrdersCountScript).mapScript(mapOrdersCountScript)
								.combineScript(combineOrdersCountScript).reduceScript(reduceOrdersCountScript))
						.subAggregation(AggregationBuilders.scriptedMetric("amount").initScript(initOrdersAmmountScript)
								.mapScript(mapOrdersAmmountScript).combineScript(combineOrdersAmmountScript)
								.reduceScript(reduceOrdersAmmountScript)));

		return pageTakeOrderAgg;
	}

	/**
	 * 计算页面你的点击量
	 * 
	 * @param pageUrl
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private AggregationBuilder aggPageClick(String pageUrl) {

		AggregationBuilder aggregationBuilders = AggregationBuilders.nested("nestclick").path("pageviews")
				.subAggregation(AggregationBuilders.filter("filter")
						.filter(QueryBuilders.boolQuery()
								.must(QueryBuilders.termQuery("pageviews.referrerPrefixURL", pageUrl))
								.mustNot(QueryBuilders.termQuery("pageviews.intcmp", "")))
						.subAggregation(AggregationBuilders.count("click").field("pageviews.intcmp")));

		return aggregationBuilders;
	}

	private SearchRequestBuilder aggPageInfo(String pageUrl, SearchRequestBuilder req) {

		// 计算页面的
		req.addAggregation(AggregationBuilders.filter("page")
				.filter(QueryBuilders.boolQuery()
						.must(QueryBuilders.nestedQuery("pageviews",
								QueryBuilders.termQuery("pageviews.prefixUrl", pageUrl))))
				.subAggregation(AggregationBuilders.cardinality("uv").field("visitorId").precisionThreshold(400))
				.subAggregation(AggregationBuilders.count("vistor").field("sessionId")))
				.addAggregation(
						AggregationBuilders.filter("bouncePage")
								.filter(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("pageViewsCount", 1))
										.must(QueryBuilders.termQuery("exitURLnoParam", pageUrl))
										.must(QueryBuilders.nestedQuery("pageviews",
												QueryBuilders.boolQuery()
														.must(QueryBuilders.termQuery("pageviews.prefixUrl",
																pageUrl)))))
								.subAggregation(AggregationBuilders.count("bouncePage").field("sessionId")))
				.addAggregation(AggregationBuilders.nested("nestEntries").path("pageviews")
						.subAggregation(AggregationBuilders.filter("filter")
								.filter(QueryBuilders.boolQuery()
										.must(QueryBuilders.termQuery("pageviews.prefixUrl", pageUrl))
										.must(QueryBuilders.termQuery("pageviews.sepPv", 1)))
								.subAggregation(AggregationBuilders.count("entries").field("pageviews.url"))))
				.addAggregation(
						AggregationBuilders
								.nested("nestpv").path(
										"pageviews")
								.subAggregation(AggregationBuilders.filter("filter")
										.filter(QueryBuilders.boolQuery()
												.must(QueryBuilders.termQuery("pageviews.prefixUrl", pageUrl)))
										.subAggregation(AggregationBuilders.count("pv").field("pageviews.url"))
										.subAggregation(AggregationBuilders.sum("viewTime").field("pageviews.viewTime"))
										.subAggregation(AggregationBuilders.max("maxTime").field("visitEndTime")
												.format("yyy-MM-dd HH:mm"))))
				.addAggregation(AggregationBuilders.filter("exitCountfilter")
						.filter(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("exitURLnoParam", pageUrl)))
						.subAggregation(AggregationBuilders.count("exitCount").field("sessionId")));

		return req;
	}

	/**
	 * 查询全站的UV,PV,跳出，访问
	 * 
	 * @return
	 */
	private SearchRequestBuilder aggAllStation(SearchRequestBuilder req) {
		// 查询全站的数据
		req.addAggregation(AggregationBuilders.cardinality("uvAllstation").field("visitorId").precisionThreshold(400))
				.addAggregation(AggregationBuilders.count("vistorAllstation").field("sessionId"))
				.addAggregation(AggregationBuilders.filter("bounceStation")
						.filter(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("pageViewsCount", 1)))
						.subAggregation(AggregationBuilders.count("bounceAllStation").field("sessionId")))
				.addAggregation(AggregationBuilders.nested("pvAllStation").path("pageviews")
						.subAggregation(AggregationBuilders.count("pv").field("pageviews.prefixUrl")));

		return req;
	}

	public List<String> queryMaxUv(int size, String index, QueryParam param) {

		BoolQueryBuilder boolQueryBuilder = getQuery(param);

		SearchRequestBuilder req =esClient.getTransportClient().prepareSearch(index).setTypes("sessions").setQuery(boolQueryBuilder)
				.addAggregation(
						AggregationBuilders.nested("pageviews").path("pageviews")
								.subAggregation(AggregationBuilders.filter("filter")
										.filter(QueryBuilders.boolQuery()
												.must(QueryBuilders.prefixQuery("pageviews.prefixUrl",
														param.getPageUrl())))
										.subAggregation(AggregationBuilders.terms("group").field("pageviews.prefixUrl")
												.order(Order.aggregation("parent > uv ", false)).size(size)
												.subAggregation(AggregationBuilders.reverseNested("parent")
														.subAggregation(AggregationBuilders.cardinality("uv")
																.field("visitorId").precisionThreshold(400))))))
				.setSize(0);

		logger.info("queryMaxUv  request >> " + req.toString());

		SearchResponse response = req.execute().actionGet();

		logger.info("queryMaxUv  response >> " + response.toString());

		Terms terms = ((InternalFilter) ((InternalNested) response.getAggregations().get("pageviews")).getAggregations()
				.get("filter")).getAggregations().get("group");

		List<String> lists = new LinkedList<String>();
		for (Bucket bucket : terms.getBuckets()) {

			lists.add(bucket.getKeyAsString());
		}

		return lists;
	}

}
