package org.tbwork.anole.gui.presentation.interceptor;

import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter; 
import org.tbwork.anole.gui.presentation.model.SessionBox;
 
 
/**
 * @author tommy.tang 
 */
@Repository
public class SessionInterceptor extends HandlerInterceptorAdapter { 
	
	private static Logger logger = LoggerFactory.getLogger(SessionInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {

		if(null == request.getSession().getAttribute("sessionBox")){
			SessionBox sessionBox = new SessionBox();
			sessionBox.setLogined(false); 
			sessionBox.setUsername("");
			request.getSession().setAttribute("sessionBox",sessionBox);
		}   
		return super.preHandle(request, response, handler);
	}
 
	@Override
	public void postHandle(
			HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		 
	}

}
