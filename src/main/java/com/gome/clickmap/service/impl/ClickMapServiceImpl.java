package com.gome.clickmap.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.gome.clickmap.dao.ClickMapDao;
import com.gome.clickmap.model.ClickMapModel;
import com.gome.clickmap.model.QueryParam;
import com.gome.clickmap.service.ClickMapService;

@Service
public class ClickMapServiceImpl implements ClickMapService {

	private Logger logger = LoggerFactory.getLogger(ClickMapServiceImpl.class);

	@Autowired
	private ClickMapDao clickMapDao;

	@Override
	public String queryByUrl(QueryParam param) {

		StringBuffer sb = new StringBuffer();

		List<ClickMapModel> lists = clickMapDao.queryByUrl(param);

		Double maxValue = clickMapDao.queryMaxClick(param);

		if (maxValue == null) {
			maxValue = 0.0;
		}

		String listsJson = JSONObject.toJSONString(lists);

		logger.info(" click response size : " + lists.size());
		
		sb.append("{ ");
		sb.append("\"maxValue\":");
		sb.append(maxValue);
		sb.append(",");
		sb.append(" \"clicks\": ");

		sb.append(listsJson);
		sb.append("}");
		logger.debug(" json : " + sb.toString());

		return sb.toString();
	}

}
