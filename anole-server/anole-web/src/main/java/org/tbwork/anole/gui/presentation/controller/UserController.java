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
import org.tbwork.anole.gui.domain.model.demand.AuthenUserDemand;
import org.tbwork.anole.gui.domain.model.demand.CreateUserDemand;
import org.tbwork.anole.gui.domain.model.demand.ModifyPasswordDemand;
import org.tbwork.anole.gui.domain.model.result.AuthenUserResult;
import org.tbwork.anole.gui.domain.model.result.CreateUserResult;
import org.tbwork.anole.gui.domain.model.result.ModifyPasswordResult;
import org.tbwork.anole.gui.domain.project.IProjectService;
import org.tbwork.anole.gui.domain.user.IUserService;
import org.tbwork.anole.gui.presentation.model.LoginStatus;
import org.tbwork.anole.gui.presentation.model.PostResponse;
import org.tbwork.anole.gui.presentation.model.SessionBox;

@Controller
@RequestMapping("/user")
@SessionAttributes(value={"sessionBox"})
public class UserController {
 
	@Autowired
	private IUserService us;
	
	@Autowired
	private IProjectService ps; 
	
	@RequestMapping(value="/modifyPassword", method=RequestMethod.POST)
	@ResponseBody
	public String modifyPassword(@ModelAttribute("sessionBox") SessionBox sessionBox, @RequestBody ModifyPasswordDemand user)
	{  
		 PostResponse<ModifyPasswordResult> result = new PostResponse<ModifyPasswordResult>();
		 try{ 
			 initializeCheck(1);
			 loginCheck(sessionBox);
			 user.setOperator(sessionBox.getUsername());
			 result.setResult(us.modifyPassword(user));
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
	
	
	@RequestMapping(value="/create", method=RequestMethod.POST)
	@ResponseBody
	public String create(@ModelAttribute("sessionBox") SessionBox sessionBox, @RequestBody CreateUserDemand user)
	{ 
		 PostResponse<CreateUserResult> result = new PostResponse<CreateUserResult>();
		 try{
			 initializeCheck(0);
			 loginCheck(sessionBox);
			 user.setOperator(sessionBox.getUsername());
			 result.setResult(us.createUser(user));
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
	
	@RequestMapping(value="/auth", method=RequestMethod.POST)
	@ResponseBody
	public String authenticate(@ModelAttribute("sessionBox") SessionBox sessionBox, @RequestBody AuthenUserDemand user)
	{ 
		 PostResponse<AuthenUserResult> result = new PostResponse<AuthenUserResult>();
		 try{
			 AuthenUserResult innerResult = us.authenUser(user);
			 if(innerResult.isPass()){
				 sessionBox.setLogined(true);
				 sessionBox.setUsername(user.getUsername());
			 } 
			 result.setResult(innerResult);
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
	
	@RequestMapping(value="/status", method=RequestMethod.GET)
	@ResponseBody
	public String getStatus(@ModelAttribute("sessionBox") SessionBox sessionBox)
	{ 
		 PostResponse<LoginStatus> result = new PostResponse<LoginStatus>(); 
		 LoginStatus innerResult = new LoginStatus();
		 innerResult.setStatus(sessionBox.isLogined());
		 innerResult.setUsername(sessionBox.getUsername()); 
		 result.setResult(innerResult);
		 result.setSuccess(true);  
		 result.setErrorMessage("OK"); 
		 return result.toStringForReturn(sessionBox.isLogined());
	}
	
	@RequestMapping(value="/logout", method=RequestMethod.GET)
	@ResponseBody
	public String logout(@ModelAttribute("sessionBox") SessionBox sessionBox){
		
		PostResponse<Boolean> result  = new PostResponse<Boolean>();
		sessionBox.setLogined(false);
		sessionBox.setUsername("");
		result.setResult(true);
		result.setErrorMessage("OK");
		result.setSuccess(true);
		return result.toStringForReturn(sessionBox.isLogined());
	}
	
	@ResponseBody
	@RequestMapping(value="/users", method=RequestMethod.GET)
	public String getUsers(@ModelAttribute("sessionBox") SessionBox sessionBox){
		PostResponse<List<String>> result = new PostResponse<List<String>>(); 
		 try{
			 initializeCheck(0);
			 loginCheck(sessionBox);
			 result.setResult(us.getUsers());
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
