package org.tbwork.anole.gui.presentation.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.tbwork.anole.gui.domain.project.IProjectService;
import org.tbwork.anole.gui.domain.user.IUserService;
import org.tbwork.anole.gui.presentation.model.SessionBox;
 
 
/**
 * @author tommy.tang 
 */
@Repository
public class SessionInterceptor extends HandlerInterceptorAdapter { 
	
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

	
}
