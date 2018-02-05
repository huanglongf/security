package com.gome.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
/**
 * 统计每个方法执行的时间
 * @author chixiaoyong
 *
 */
public class ExecutionTimeInterceptor implements HandlerInterceptor {

	private Logger log = LoggerFactory.getLogger(ExecutionTimeInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		long startTime = System.currentTimeMillis();
 
		request.setAttribute("startTime", startTime);

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		//response.addHeader("Access-Control-Allow-Origin", "*");
		long startime = (Long) request.getAttribute("startTime");

		long endTime = System.currentTimeMillis();

		long execuTime = endTime - startime;

		
		log.info("[{}] executeTime : {}ms", handler, execuTime);
	

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}

}
