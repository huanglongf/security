package test;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSON;
import com.gome.dao.model.PageView;
import com.gome.pathanalyse.dao.IKylinPathAnalyseQueryDao;
import com.gome.pathanalyse.model.PathAnalyseQueryParam;
import com.gome.pathanalyse.model.PathAnalyseQueryResult;

public class PathAnalyseQueryDaoTest {

	public void test(){
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		IKylinPathAnalyseQueryDao dao = context.getBean(IKylinPathAnalyseQueryDao.class);
		
		PathAnalyseQueryParam param = new PathAnalyseQueryParam();
		param.setVisitStartDay("20160901");
		param.setVisitEndDay("20160910");
		param.setDatasource("PC");
		param.setPageSite("主站");
		param.setPageChannel("促销专区");
		param.setPageType("促销页");
		param.setPageSize(10);
		param.setPageStart(0);
		param.setTopNGroupColumn("REFERRERPAGETYPE");
		
		List<PathAnalyseQueryResult> list = dao.queryTopNEntries(param);
		
		for(PathAnalyseQueryResult item : list)
			System.out.println(JSON.toJSONString(item));
		
		PathAnalyseQueryResult directEntry = dao.queryDirectEntry(param);
		System.out.println(JSON.toJSONString(directEntry));
		
		PathAnalyseQueryResult directExit = dao.queryExit(param);
		System.out.println(JSON.toJSONString(directExit));
		
		PageView result = dao.queryAllStation(param);
		System.out.println(JSON.toJSONString(result));
	}
}
