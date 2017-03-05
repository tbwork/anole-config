package org.tbwork.anole.gui.domain.user.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.anole.infrastructure.dao.AnoleConfigItemMapper;
import org.anole.infrastructure.dao.AnoleUserMapper;
import org.anole.infrastructure.model.AnoleEnvironment;
import org.anole.infrastructure.model.AnoleUser;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.tbwork.anole.gui.domain.authorization.IAuthorizationService;
import org.tbwork.anole.gui.domain.cache.Cache;
import org.tbwork.anole.gui.domain.enums.Operation;
import org.tbwork.anole.gui.domain.model.demand.AuthenUserDemand;
import org.tbwork.anole.gui.domain.model.demand.CreateUserDemand;
import org.tbwork.anole.gui.domain.model.demand.ModifyPasswordDemand;
import org.tbwork.anole.gui.domain.model.result.AuthenUserResult;
import org.tbwork.anole.gui.domain.model.result.CreateUserResult;
import org.tbwork.anole.gui.domain.model.result.ModifyPasswordResult;
import org.tbwork.anole.gui.domain.permission.IPermissionService;
import org.tbwork.anole.gui.domain.user.IUserService;
import org.tbwork.anole.gui.domain.util.CacheKeys;

import com.google.common.base.Preconditions;

@Service
public class UserService implements IUserService {

	@Autowired
	private AnoleUserMapper anoleUserMapper;
	
	@Autowired
	private IAuthorizationService authenService;
	
	private boolean initialized = false;

	@Autowired
	private IPermissionService pers;
	
	
	@PostConstruct
	private void initialize(){
		AnoleUser anoleUser = anoleUserMapper.selectByUsername("admin");
		initialized =  anoleUser != null;
	}
	
	
	@Autowired 
	@Qualifier("localCache")
	private Cache lc ;
	 
	
	@Override
	public CreateUserResult createUser(CreateUserDemand createUserDemand) { 
		CreateUserResult result = new CreateUserResult();
		try{
			Preconditions.checkNotNull(createUserDemand, "Input is null!");
			Preconditions.checkArgument(createUserDemand.getUsername()!=null && !createUserDemand.getUsername().isEmpty(), "Username should not be null or empty.");
			Preconditions.checkArgument(createUserDemand.getPassword()!=null && !createUserDemand.getPassword().isEmpty(), "Password should not be null or empty.");
			if(!initialized && !"admin".equals(createUserDemand.getUsername()))
				throw new RuntimeException("Please create admin account first.");
			if(initialized) authenService.authenAdminOperation(createUserDemand.getOperator(), Operation.CREATE_USER); 
			String username = createUserDemand.getUsername();
			String password = createUserDemand.getPassword(); 
			AnoleUser anoleUser = anoleUserMapper.selectByUsername(username);
			if(anoleUser == null){
				AnoleUser tempUser = new AnoleUser();
				tempUser.setUsername(username);
				tempUser.setPassword(DigestUtils.md5Hex(password));
				anoleUserMapper.insert(tempUser);
				if("admin".equals(username))
					initialized = true;
				
				List<String> cachedUsers = lc.get(CacheKeys.USERS_CACHE_KEY);
				if(cachedUsers!=null && !cachedUsers.contains(username)){
					cachedUsers.add(username);
					//lc.set(USERS_CACHE_KEY, cachedUsers); //local cache need not do this
				}
				
				result.setSuccess(true); 
				result.setErrorMessage("OK.");
			}
			else{
				if("admin".equals(username)){
					result.setSuccess(false); 
					result.setErrorMessage("Can not create amdin user!");
				}
				else{
					result.setSuccess(false); 
					result.setErrorMessage(String.format("The user (%s) is already existed.", username));
				} 
			} 
		}
		catch(Exception e){
			result.setSuccess(false);
			result.setErrorMessage(e.getMessage());
		} 
		return result;
	}

	@Override
	public AuthenUserResult authenUser(AuthenUserDemand authenUserDemand) {
		AuthenUserResult result = new AuthenUserResult();
		try{
				Preconditions.checkNotNull(authenUserDemand, "Input is null!");
				Preconditions.checkArgument(authenUserDemand.getUsername()!=null && !authenUserDemand.getUsername().isEmpty(), "Username should not be null or empty.");
				Preconditions.checkArgument(authenUserDemand.getPassword()!=null && !authenUserDemand.getPassword().isEmpty(), "Password should not be null or empty.");
				
				String username = authenUserDemand.getUsername();
				String password = authenUserDemand.getPassword(); 
				AnoleUser anoleUser = anoleUserMapper.selectByUsername(username);
				if(anoleUser == null){
					result.setPass(false);
					result.setErrorMessage("The user is not existed!");
				}
				else{
					String md5Password = DigestUtils.md5Hex(password);
					if(md5Password.equals(anoleUser.getPassword())){
						result.setPass(true);
						result.setErrorMessage("OK");
						if("admin".equals(username))
								initialized = true;
						// 获取 权限
						if(authenUserDemand.getProject()!=null && authenUserDemand.getEnv()!=null){
							result.setPermission(pers.getUserRole(authenUserDemand.getProject(), username, authenUserDemand.getEnv()));
						}
						
					}
					else{
						result.setPass(false);
						result.setErrorMessage("Wrong password!");
					} 
				} 
		}
		catch(Exception e){
			result.setPass(false);
			result.setErrorMessage(e.getMessage());
		} 
		return result;
	}

	@Override
	public ModifyPasswordResult modifyPassword(ModifyPasswordDemand modifyUserDemand) {
		ModifyPasswordResult result = new ModifyPasswordResult();
		try{
			Preconditions.checkNotNull(modifyUserDemand, "Input is null!");
			Preconditions.checkArgument(modifyUserDemand.getUsername()!=null && !modifyUserDemand.getUsername().isEmpty(), "Username should not be null or empty.");
			Preconditions.checkArgument(modifyUserDemand.getPassword()!=null && !modifyUserDemand.getPassword().isEmpty(), "Password should not be null or empty.");
			if(!initialized) throw new RuntimeException("Please create admin account first.");
			if(initialized) authenService.authenAdminOperation(modifyUserDemand.getOperator(), Operation.MODIFY_PASSWORD); 
			String username = modifyUserDemand.getUsername();
			String password = modifyUserDemand.getPassword(); 
			AnoleUser anoleUser = anoleUserMapper.selectByUsername(username);
			if(anoleUser == null){
				result.setSuccess(false); 
				result.setErrorMessage(String.format("The user (%s) is not existed.", username));
			}
			else{
				anoleUser.setPassword(DigestUtils.md5Hex(password));
				anoleUserMapper.updateByPrimaryKey(anoleUser);
				result.setSuccess(true); 
				result.setErrorMessage("OK");
			} 
		}
		catch(Exception e){
			result.setSuccess(false);
			result.setErrorMessage(e.getMessage());
		} 
		return result;
	}

	@Override
	public boolean isInitialized() {
	   return initialized;
	}

	@Override
	public List<String> getUsers() {
		List<String> result = lc.get(CacheKeys.USERS_CACHE_KEY);
		if(result != null && !result.isEmpty())
			return result; 
	    result = new ArrayList<String>();
		List<AnoleUser> userList = anoleUserMapper.selectAll();
		if(userList!=null){
			for(AnoleUser user : userList){
				result.add(user.getUsername());
			}
			lc.set(CacheKeys.USERS_CACHE_KEY, result);
		}
		return result;
	}

}
