package org.tbwork.anole.gui.presentation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.tbwork.anole.gui.domain.model.Project;
import org.tbwork.anole.gui.domain.model.demand.CreateProjectDemand;
import org.tbwork.anole.gui.domain.model.demand.InitializeEnvsDemand;
import org.tbwork.anole.gui.domain.model.demand.ModifyPasswordDemand;
import org.tbwork.anole.gui.domain.model.result.CreateProjectResult;
import org.tbwork.anole.gui.domain.model.result.InitializeEnvsResult;
import org.tbwork.anole.gui.domain.model.result.ModifyPasswordResult;
import org.tbwork.anole.gui.domain.project.IProjectService;
import org.tbwork.anole.gui.domain.user.IUserService;
import org.tbwork.anole.gui.presentation.model.LoginStatus;
import org.tbwork.anole.gui.presentation.model.PostResponse;
import org.tbwork.anole.gui.presentation.model.SessionBox; 

import com.alibaba.fastjson.JSON;

@Controller 
@RequestMapping("/project")
@SessionAttributes(value={"sessionBox"})
public class ProjectInfoController {
	
	@Autowired
	private IUserService us;

	@Autowired
	private IProjectService ps; 
	 
	
	@RequestMapping(value="/create", method=RequestMethod.POST)
	@ResponseBody
	public String createProject(@ModelAttribute("sessionBox") SessionBox sessionBox, @RequestBody CreateProjectDemand project)
	{  
		 PostResponse<CreateProjectResult> result = new PostResponse<CreateProjectResult>();
		 try{ 
			 initializeCheck();
			 loginCheck(sessionBox);
			 project.setOperator(sessionBox.getUsername());
			 result.setResult(ps.createProject(project));
			 result.setSuccess(true);  
			 result.setErrorMessage("OK");
		 }
		 catch(Exception e){
			 result.setErrorMessage(e.getMessage());
			 result.setResult(null);
			 result.setSuccess(false);
		 } 
		 return result.toStringForReturn(sessionBox.isLogined());
	}
	
	@RequestMapping(value="/setEnvs", method=RequestMethod.POST)
	@ResponseBody
	public String setEnvs(@ModelAttribute("sessionBox") SessionBox sessionBox, @RequestBody InitializeEnvsDemand envs)
	{  
		 PostResponse<InitializeEnvsResult> result = new PostResponse<InitializeEnvsResult>();
		 try{
			 if(!us.isInitialized()) throw new RuntimeException("Please create admin account first.");
			 loginCheck(sessionBox); 
			 envs.setOperator(sessionBox.getUsername());
			 result.setResult(ps.initializeEnvs(envs));
			 result.setSuccess(true);  
			 result.setErrorMessage("OK");
		 }
		 catch(Exception e){
			 result.setErrorMessage(e.getMessage());
			 result.setResult(null);
			 result.setSuccess(false);
		 } 
		 return result.toStringForReturn(sessionBox.isLogined());
	}
	
	@RequestMapping(value="/envs", method=RequestMethod.GET)
	@ResponseBody
	public String getEnvs(@ModelAttribute("sessionBox") SessionBox sessionBox)
	{ 
		 PostResponse<List<String>> result = new PostResponse<List<String>>(); 
		 List<String> envs = ps.getEnvs();
		 result.setResult(envs);
		 result.setSuccess(true);  
		 result.setErrorMessage("OK"); 
		 return result.toStringForReturn(sessionBox.isLogined());
	}
	
	
	@RequestMapping(value="/prdlines", method=RequestMethod.GET)
	@ResponseBody
	public String getProductLines(@ModelAttribute("sessionBox") SessionBox sessionBox)
	{ 
		 PostResponse<List<String>> result = new PostResponse<List<String>>(); 
		 List<String> envs = ps.getProductLines();
		 result.setResult (envs);
		 result.setSuccess(true);  
		 result.setErrorMessage("OK"); 
		 return result.toStringForReturn(sessionBox.isLogined());
	}
	
	@RequestMapping(value="/envstatus", method=RequestMethod.GET)
	@ResponseBody
	public String getEnvstatus(@ModelAttribute("sessionBox") SessionBox sessionBox)
	{ 
		 PostResponse<Boolean> result = new PostResponse<Boolean>();  
		 result.setResult(ps.isInitialized());
		 result.setSuccess(true);  
		 result.setErrorMessage("OK"); 
		 return result.toStringForReturn(sessionBox.isLogined());
	}
	
	@RequestMapping(value="/projects", method=RequestMethod.GET)
	@ResponseBody
	public String getProjects(@ModelAttribute("sessionBox") SessionBox sessionBox)
	{ 
		 PostResponse<List<Project>> result = new PostResponse<List<Project>>(); 
		 List<Project> projects = ps.getProjects();
		 result.setResult(projects);
		 result.setSuccess(true);  
		 result.setErrorMessage("OK"); 
		 return result.toStringForReturn(sessionBox.isLogined());
	}
	
	private void initializeCheck(){
		 if(!us.isInitialized()) throw new RuntimeException("Please create admin account first.");
		 if(!ps.isInitialized()) throw new RuntimeException("Please intialize environments first.");
	}
	private void loginCheck(SessionBox sessionBox) {
		 if(!sessionBox.isLogined()) throw new RuntimeException("Please login first.");
	}
}
