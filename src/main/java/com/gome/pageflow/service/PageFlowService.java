package com.gome.pageflow.service;

import java.util.Map;

import com.gome.pageflow.model.PageFlowQueryParams;

public interface PageFlowService {

	public Map<String, Object> queryTopN(PageFlowQueryParams params);

	public Map<String, Object> queryChart(PageFlowQueryParams params);

	public Map<String, Object> querySearch(PageFlowQueryParams params);

	public Map<String, Object> querySearchChart(PageFlowQueryParams params);
    /**
     * 验证参数
     * @param queryParams
     * @return
     */
	public boolean valid(PageFlowQueryParams queryParams);

	public Map<String, Object> queryPlusTopN(PageFlowQueryParams params);

	public Map<String, Object> queryPlusSearch(PageFlowQueryParams params);

	public Map<String, Object> queryPlusChart(PageFlowQueryParams queryParams);

	public Map<String, Object> queryPlusSearchChart(
			PageFlowQueryParams queryParams);

}
