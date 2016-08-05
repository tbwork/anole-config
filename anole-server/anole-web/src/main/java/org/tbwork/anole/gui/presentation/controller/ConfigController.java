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
import org.tbwork.anole.gui.domain.config.IConfigService;
import org.tbwork.anole.gui.domain.model.Config;
import org.tbwork.anole.gui.domain.model.demand.AddConfigDemand; 
import org.tbwork.anole.gui.domain.model.demand.DeleteConfigDemand;
import org.tbwork.anole.gui.domain.model.demand.GetConfigByKeyAndEnvDemand;
import org.tbwork.anole.gui.domain.model.demand.GetConfigsByProjectAndEnvDemand;
import org.tbwork.anole.gui.domain.model.demand.ModifyConfigDemand;
import org.tbwork.anole.gui.domain.model.result.AddConfigResult; 
import org.tbwork.anole.gui.domain.model.result.DeleteConfigResult;
import org.tbwork.anole.gui.domain.model.result.ModifyConfigResult;
import org.tbwork.anole.gui.domain.project.IProjectService;
import org.tbwork.anole.gui.domain.user.IUserService;
import org.tbwork.anole.gui.presentation.model.PostResponse;
import org.tbwork.anole.gui.presentation.model.SessionBox;

@Controller 
@RequestMapping("/config")
@SessionAttributes(value={"sessionBox"})
public class ConfigController {

	@Autowired
	private IUserService us;

	@Autowired
	private IProjectService ps; 
	 
	@Autowired
	private IConfigService cs;
	
	
	@RequestMapping(value="/create", method=RequestMethod.POST)
	@ResponseBody
	public String createConfig(@ModelAttribute("sessionBox") SessionBox sessionBox, @RequestBody AddConfigDemand config)
	{  
		 PostResponse<AddConfigResult> result = new PostResponse<AddConfigResult>();
		 try{ 
			 initializeCheck(0);
			 loginCheck(sessionBox);
			 config.setOperator(sessionBox.getUsername());
			 result.setResult(cs.addConfig(config));
			 result.setSuccess(true);  
			 result.setErrorMessage("OK");
		 }
		 catch(Exception e){
			 result.setErrorMessage(e.getMessage());
			 result.setResult(null);
			 result.setSuccess(false);
		 } 
		 return result.toString();
	}
	
	@RequestMapping(value="/modify", method=RequestMethod.POST)
	@ResponseBody
	public String modifyConfig(@ModelAttribute("sessionBox") SessionBox sessionBox, @RequestBody ModifyConfigDemand config)
	{  
		 PostResponse<ModifyConfigResult> result = new PostResponse<ModifyConfigResult>();
		 try{ 
			 initializeCheck(0);
			 loginCheck(sessionBox);
			 config.setOperator(sessionBox.getUsername());
			 result.setResult(cs.modifyConfig(config));
			 result.setSuccess(true);  
			 result.setErrorMessage("OK");
		 }
		 catch(Exception e){
			 result.setErrorMessage(e.getMessage());
			 result.setResult(null);
			 result.setSuccess(false);
		 } 
		 return result.toString();
	}
	
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	@ResponseBody
	public String modifyConfig(@ModelAttribute("sessionBox") SessionBox sessionBox, @RequestBody DeleteConfigDemand demand)
	{  
		 PostResponse<DeleteConfigResult> result = new PostResponse<DeleteConfigResult>();
		 try{ 
			 initializeCheck(0);
			 loginCheck(sessionBox);
			 demand.setOperator(sessionBox.getUsername());
			 result.setResult(cs.deleteConfig(demand));
			 result.setSuccess(true);  
			 result.setErrorMessage("OK");
		 }
		 catch(Exception e){
			 result.setErrorMessage(e.getMessage());
			 result.setResult(null);
			 result.setSuccess(false);
		 } 
		 return result.toString();
	}
	
	@RequestMapping(value="/getByProjectAndEnv", method=RequestMethod.POST)
	@ResponseBody
	public String getByProjectAndEnv(@ModelAttribute("sessionBox") SessionBox sessionBox, @RequestBody GetConfigsByProjectAndEnvDemand demand)
	{  
		 PostResponse<List<Config>> result = new PostResponse<List<Config>>();
		 try{ 
			 initializeCheck(0);
			 result.setResult(cs.getConfigsByProjectAndEnv(demand));
			 result.setSuccess(true);  
			 result.setErrorMessage("OK");
		 }
		 catch(Exception e){
			 result.setErrorMessage(e.getMessage());
			 result.setResult(null);
			 result.setSuccess(false);
		 } 
		 return result.toString();
	}
	

	@RequestMapping(value="/getByKeyAndEnv", method=RequestMethod.POST)
	@ResponseBody
	public String getByKeyAndEnv(@ModelAttribute("sessionBox") SessionBox sessionBox, @RequestBody GetConfigByKeyAndEnvDemand demand)
	{  
		 PostResponse<Config> result = new PostResponse<Config>();
		 try{ 
			 initializeCheck(0);
			 loginCheck(sessionBox);
			 result.setResult(cs.getConfigByKeyAndEnv(demand));
			 result.setSuccess(true);  
			 result.setErrorMessage("OK");
		 }
		 catch(Exception e){
			 result.setErrorMessage(e.getMessage());
			 result.setResult(null);
			 result.setSuccess(false);
		 } 
		 return result.toString();
	}
	
	private void initializeCheck(int index){
		 if(index == 1 || index == 0) if(!us.isInitialized()) throw new RuntimeException("Please create admin account first.");
		 if(index == 2 || index == 0) if(!ps.isInitialized()) throw new RuntimeException("Please intialize environments first.");
	}
	private void loginCheck(SessionBox sessionBox) {
		 if(!sessionBox.isLogined()) throw new RuntimeException("Please login first.");
	}
}
