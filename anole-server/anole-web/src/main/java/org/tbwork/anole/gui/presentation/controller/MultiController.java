package org.tbwork.anole.gui.presentation.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.tbwork.anole.gui.domain.project.IProjectService;
import org.tbwork.anole.gui.domain.user.IUserService;
import org.tbwork.anole.gui.presentation.model.LoginStatus;
import org.tbwork.anole.gui.presentation.model.MutliResult;
import org.tbwork.anole.gui.presentation.model.PostResponse;
import org.tbwork.anole.gui.presentation.model.SessionBox;

@Controller 
@RequestMapping("/multi")
@SessionAttributes(value={"sessionBox"})
public class MultiController {

	
	@Autowired
	private IUserService us;
	
	@Autowired
	private IProjectService ps;
	
	
	@RequestMapping(value="/load", method=RequestMethod.POST)
	@ResponseBody
	public String load(@ModelAttribute("sessionBox") SessionBox sessionBox, @RequestBody List<String> methods)
	{ 
		 PostResponse<MutliResult> result = new PostResponse<MutliResult>(); 
		 MutliResult innerResult = new MutliResult(); 
		 for(String method : methods){
			 if("getUsers".equals(method))
				 innerResult.setUsers(us.getUsers()); 
			 if("getPrdLines".equals(method))
				 innerResult.setPrdLines(ps.getProductLines());
			 if("getProjects".equals(method))
				 innerResult.setProjects(ps.getProjects());
			 if("loginStatus".equals(method))
				 innerResult.setLoginStatus(sessionBox.isLogined());
		 } 
		 result.setResult(innerResult);
		 result.setSuccess(true);  
		 result.setErrorMessage("OK"); 
		 return result.toStringForReturn(sessionBox.isLogined());
	}
	
}
