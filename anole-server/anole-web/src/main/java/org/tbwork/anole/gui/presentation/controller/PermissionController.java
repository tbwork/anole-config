package org.tbwork.anole.gui.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.tbwork.anole.gui.domain.model.demand.AssignPermissionDemand;
import org.tbwork.anole.gui.domain.model.demand.GetPermissionDemand;
import org.tbwork.anole.gui.domain.model.result.AssignPermissionResult;
import org.tbwork.anole.gui.domain.model.result.CommonResult;
import org.tbwork.anole.gui.domain.model.result.CreateProjectResult;
import org.tbwork.anole.gui.domain.permission.IPermissionService;
import org.tbwork.anole.gui.domain.project.IProjectService;
import org.tbwork.anole.gui.domain.user.IUserService;
import org.tbwork.anole.gui.presentation.model.PostResponse;
import org.tbwork.anole.gui.presentation.model.SessionBox;


@Controller 
@RequestMapping("/permission")
@SessionAttributes(value={"sessionBox"})
public class PermissionController {

	@Autowired
	private IPermissionService pers;
	
	@Autowired
	private IUserService us;
	
	@Autowired
	private IProjectService ps; 
	
	@RequestMapping(value="/assign", method=RequestMethod.POST)
	@ResponseBody
	public String assignPermission(@ModelAttribute("sessionBox") SessionBox sessionBox, @RequestBody AssignPermissionDemand permissionDemand){
		PostResponse<AssignPermissionResult> result = new PostResponse<AssignPermissionResult>();
		 try{ 
			 initializeCheck(0);
			 loginCheck(sessionBox);
			 permissionDemand.setOperator(sessionBox.getUsername());
			 result.setResult(pers.assignPermission(permissionDemand));
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
	
	@RequestMapping(value="/get", method=RequestMethod.POST)
	@ResponseBody
	public String getPermission(@ModelAttribute("sessionBox") SessionBox sessionBox, @RequestBody GetPermissionDemand demand){
		PostResponse<Integer> result = new PostResponse<Integer>();
		 try{ 
			 initializeCheck(0); 
			 if(!sessionBox.isLogined()){
				 result.setResult(0);
				 result.setErrorMessage("ok");
				 result.setSuccess(true);
				 return result.toString();
			 } 
			 result.setResult(pers.getUserRole(demand.getProject(), sessionBox.getUsername(), demand.getEnv()));
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
	
	
	private void initializeCheck(int index){
		 if(index == 1 || index == 0) if(!us.isInitialized()) throw new RuntimeException("Please create admin account first.");
		 if(index == 2 || index == 0) if(!ps.isInitialized()) throw new RuntimeException("Please intialize environments first.");
	}
	private void loginCheck(SessionBox sessionBox) {
		 if(!sessionBox.isLogined()) throw new RuntimeException("Please login first.");
	}
	
}
