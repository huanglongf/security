package com.gome.clickmap.Controller;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gome.clickmap.model.QueryParam;
import com.gome.clickmap.service.ClickMapService;

/**
 * 
 * @author chixiaoyong
 *
 */
@RestController
public class ClickMapController {

	private Logger log = LoggerFactory.getLogger(ClickMapController.class);

	@Autowired
	private ClickMapService mapService;

	/*
	 * 查询点击。在Http header 里面添加Access-Control-Allow-Origin：* 解决跨域问题
	 */

	@RequestMapping(value = "/click/query")

	public String queryClicks(QueryParam param, HttpServletResponse response) {

		if (!validParam(param)) {

			return "{\"error\":\" params  at least null or empty !\"}";
		}
		response.addHeader("Access-Control-Allow-Origin", "*");
		return mapService.queryByUrl(param);

	}

	public boolean validParam(QueryParam param) {

		log.info(" query param : " + param);
		
		
		
		if (StringUtils.isNotEmpty(param.getPageUrl()) && StringUtils.isNotEmpty(param.getStartTime())
				&& StringUtils.isNotEmpty(param.getEndTime())) {

			return true;
		}

		if (param.getMin() <= 1) {
			
			param.setMin(5);
		}
		

		return false;
	}
	
	@RequestMapping("/click/test")
	 public void getScreenImg(HttpServletRequest request, HttpServletResponse response) throws Exception {  
	        Thread.sleep(3000);  
	        response.setContentType("image/jpg");  
	        ServletOutputStream sos = response.getOutputStream();  
	        // 禁止页面缓存  
	        response.setHeader("Pragma", "No-cache");  
	        response.setHeader("Cache-Control", "no-cache");  
	        response.setDateHeader("Expires", 0);  
	        // 创建内存图象并获得其图形上下文  
	        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();  
	        BufferedImage screenshot = (new Robot())  
	                .createScreenCapture(new Rectangle(0, 0, (int) dimension  
	                        .getWidth(), (int) dimension.getHeight()));  
	        // 将图像输出到客户端  
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();  
	        ImageIO.write(screenshot, "jpg", bos);  
	        byte[] buf = bos.toByteArray();  
	        response.setContentLength(buf.length);  
	        sos.write(buf);  
	        bos.close();  
	        sos.close();  
	    }  
}
  