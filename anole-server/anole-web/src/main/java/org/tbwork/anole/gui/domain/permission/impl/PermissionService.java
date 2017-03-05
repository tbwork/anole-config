package org.tbwork.anole.gui.domain.permission.impl;

import java.util.List;

import org.anole.infrastructure.dao.AnoleProjectMapper;
import org.anole.infrastructure.dao.AnoleUserProjectMapMapper;
import org.anole.infrastructure.model.AnoleProject;
import org.anole.infrastructure.model.AnoleUserProjectMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.tbwork.anole.gui.domain.authorization.IAuthorizationService;
import org.tbwork.anole.gui.domain.cache.Cache;
import org.tbwork.anole.gui.domain.enums.Operation;
import org.tbwork.anole.gui.domain.model.Project;
import org.tbwork.anole.gui.domain.model.demand.AssignPermissionDemand;
import org.tbwork.anole.gui.domain.model.result.AssignPermissionResult;
import org.tbwork.anole.gui.domain.permission.IPermissionService;
import org.tbwork.anole.gui.domain.project.IProjectService;
import org.tbwork.anole.gui.domain.user.IUserService;
import org.tbwork.anole.gui.domain.util.CacheKeys;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

@Service
public class PermissionService implements IPermissionService {

	
	@Autowired 
	@Qualifier("localCache")
	private Cache lc ;
	
	@Autowired
	private IUserService us;

	@Autowired
	private IProjectService ps; 
	
	@Autowired
	private IAuthorizationService authenService;
	
	@Autowired
	private AnoleUserProjectMapMapper anoleUserProjectMapMapper;
	
	@Autowired
	private AnoleProjectMapper anoleProjectMapper;
	
	private final static List<String> roles = Lists.newArrayList("stranger", "vistor", "manager","owner"); 
	 
	
	
	@Override
	public AssignPermissionResult assignPermission(AssignPermissionDemand permissionDemand) {
		AssignPermissionResult result = new AssignPermissionResult();
		try{
			Preconditions.checkNotNull(permissionDemand,"Input is null.");
			Preconditions.checkArgument(permissionDemand.getOperator()!=null&& !permissionDemand.getOperator().isEmpty(), "Operator should not be null or empty.");
			Preconditions.checkArgument(permissionDemand.getUsername()!=null&& !permissionDemand.getUsername().isEmpty(), "Username should not be null or empty.");
			Preconditions.checkArgument(permissionDemand.getProjectName()!=null&& !permissionDemand.getProjectName().isEmpty(), "Project name should not be null or empty.");
			Preconditions.checkArgument(permissionDemand.getRole()!=null&& !permissionDemand.getRole().isEmpty(), "Role should not be null or empty.");
			checkUsername(permissionDemand.getUsername());
			checkRoles   (permissionDemand.getRole()); 
			if(!permissionDemand.getRole().equals("owner"))
				checkEnv     (permissionDemand.getEnv());
			checkProjects(permissionDemand.getProjectName());
			
			authenService.authen(permissionDemand.getOperator(), permissionDemand.getProjectName(), Operation.ASSIGN_PERMISSION);
			if(!permissionDemand.getRole().equals("owner")){
				AnoleUserProjectMap aupMap = new AnoleUserProjectMap();
				aupMap.setEnv(permissionDemand.getEnv());
				aupMap.setProject(permissionDemand.getProjectName());
				aupMap.setRole(getRole(permissionDemand.getRole())); 
				aupMap.setUsername(permissionDemand.getUsername());
				anoleUserProjectMapMapper.insert(aupMap);
			}else{
				AnoleProject ap = anoleProjectMapper.selectByName(permissionDemand.getProjectName());
				if(ap == null) throw new RuntimeException("The project is not existed!");
				ap.setOwner(permissionDemand.getUsername());
				anoleProjectMapper.updateByPrimaryKey(ap);
			} 
			String cacheKey = CacheKeys.buildPermissionCacheKey(permissionDemand.getProjectName(), permissionDemand.getUsername(), permissionDemand.getEnv());
			lc.set(cacheKey, getRole(permissionDemand.getRole()), 0);
			
			result.setErrorMessage("OK");
			result.setSuccess(true);
		}
		catch(Exception e){
			result.setSuccess(false);
			result.setErrorMessage(e.getMessage());
		} 
		return result;
	}
	
	
	private void checkUsername(String username){
		 List<String> users = us.getUsers();
		 if(users == null || !users.contains(username))
			 throw new RuntimeException("The target user is not existed!"); 
	}
	
	private void checkEnv(String env){
		List<String> envs = ps.getEnvs();
		if(envs == null || !envs.contains(env))
			throw new RuntimeException("Unknown environment.");
	}

	private void checkProjects(String project){
		List<Project> projects = ps.getProjects();
		if(projects == null)
			throw new RuntimeException("The target project is not existed."); 
		for(Project p : projects){
			if(p.getProjectName().equals(project))
				return ;
		}
		throw new RuntimeException("The target project is not existed.");
	}
	
	private void checkRoles(String role){
		if(!roles.contains(role))
			throw new RuntimeException("Unknown role."); 
	}
	
	private byte getRole(String role){
		for(int i =0 ; i< roles.size(); i++){
			if(role.equals(roles.get(i)))
				return (byte) i;
		}
		return -1;
	}


	@Override
	public int getUserRole(String project, String user, String env) {
		try{ 
			if(user == null || user.isEmpty())
				return 0;
			if(user.equals("admin"))// admin is the owner of whole projects.
				return 3; 
			String cacheKey = CacheKeys.buildPermissionCacheKey(project, user, env);
			Integer permission = lc.get(cacheKey);
			if(permission != null)
				return permission; 
			AnoleProject ap = anoleProjectMapper.selectByName(project);
			if(ap!=null && ap.getOwner().equals(user))
				return 3;
			return anoleUserProjectMapMapper.selectRoleByProjectKeyEnv(user, project, env);
		}
		catch(Exception e){
			return 0;
		} 
	}


	@Override
	public boolean isOwner(String project, String user) {
		if(user.equals("admin"))// admin is the owner of whole projects.
			return true;
		AnoleProject ap = anoleProjectMapper.selectByName(project);
		if(ap!=null && ap.getOwner().equals(user))
			return true;
		return false;
	}
}
