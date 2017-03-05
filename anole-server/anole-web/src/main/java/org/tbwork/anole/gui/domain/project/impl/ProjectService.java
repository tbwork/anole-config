package org.tbwork.anole.gui.domain.project.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.anole.infrastructure.dao.AnoleEnvironmentMapper;
import org.anole.infrastructure.dao.AnoleProductLineMapper;
import org.anole.infrastructure.dao.AnoleProjectMapper;
import org.anole.infrastructure.dao.AnoleUserMapper;
import org.anole.infrastructure.model.AnoleEnvironment;
import org.anole.infrastructure.model.AnoleProductLine;
import org.anole.infrastructure.model.AnoleProject;
import org.anole.infrastructure.model.AnoleUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.tbwork.anole.gui.domain.cache.Cache;
import org.tbwork.anole.gui.domain.model.Project;
import org.tbwork.anole.gui.domain.model.demand.CreateProjectDemand;
import org.tbwork.anole.gui.domain.model.demand.InitializeEnvsDemand;
import org.tbwork.anole.gui.domain.model.result.CreateProjectResult;
import org.tbwork.anole.gui.domain.model.result.InitializeEnvsResult;
import org.tbwork.anole.gui.domain.project.IProjectService;
import org.tbwork.anole.gui.domain.util.CacheKeys;

import com.google.common.base.Preconditions;

@Service
public class ProjectService implements IProjectService{

	@Autowired
	private AnoleProjectMapper anoleProjectMapper;
	
	@Autowired
	private AnoleProductLineMapper anoleProductLineMapper;
	
	@Autowired
	private AnoleEnvironmentMapper anoleEnvironmentMapper;
	
	@Autowired 
	@Qualifier("localCache")
	private Cache lc ;
	
	private boolean initialized = false;
	
	
	
	@PostConstruct
	private void initialize(){
		List<AnoleEnvironment> envs = anoleEnvironmentMapper.selectAll();
		initialized = envs!=null && !envs.isEmpty();
	}
	
	@Override
	public CreateProjectResult createProject(CreateProjectDemand createProjectDemand) {
		CreateProjectResult result = new CreateProjectResult();
		try{
			Preconditions.checkNotNull (createProjectDemand, "Input is null!");
			Preconditions.checkArgument(createProjectDemand.getProductLineName()!=null && !createProjectDemand.getProductLineName().isEmpty(), "Product line name should not be null or empty.");
			Preconditions.checkArgument(createProjectDemand.getProjectName()!=null && !createProjectDemand.getProjectName().isEmpty(), "Project name should not be null or empty.");
			Preconditions.checkArgument(createProjectDemand.getOperator()!=null && !createProjectDemand.getOperator().isEmpty(), "Opeartor should not be null or empty.");
			AnoleProductLine apl = anoleProductLineMapper.selectByName(createProjectDemand.getProductLineName());
			if(apl == null){
				AnoleProductLine newApl = new AnoleProductLine();
				newApl.setName(createProjectDemand.getProductLineName()); 
			    anoleProductLineMapper.insert(newApl);
			    List<String> cachedPrdLines = lc.get(CacheKeys.PRODUCT_LINE_CACHE_KEY);
			    // refresh local cache
			    lc.remove(CacheKeys.PRODUCT_LINE_CACHE_KEY);
			} 
			if(anoleProjectMapper.countProject(createProjectDemand.getProjectName()) == 0){
				AnoleProject ap = new AnoleProject();
				ap.setCreator(createProjectDemand.getOperator());
				ap.setOwner(createProjectDemand.getOperator());
				ap.setName(createProjectDemand.getProjectName());
				ap.setProductLine(createProjectDemand.getProductLineName()); 
				anoleProjectMapper.insert(ap);
				// refresh local cache
				lc.remove(CacheKeys.PROJECTS_CACHE_KEY);
				
				result.setErrorMessage("OK");
				result.setSuccess(true);
			}
			else{
				result.setErrorMessage("The project is already existed!");
				result.setSuccess(false);
			} 
		}
		catch(Exception e){
			result.setSuccess(false);
			result.setErrorMessage(e.getMessage());
		} 
		return result;
	}

	@Override
	public List<Project> getProjects() {
		List<Project> result = lc.get(CacheKeys.PROJECTS_CACHE_KEY);
		if(result != null && !result.isEmpty())
			return result;
		result = new ArrayList<Project>();
		List<AnoleProject> resultFromDB = anoleProjectMapper.selectAll();
		if(resultFromDB!=null){
			for(AnoleProject item: resultFromDB){
				Project resultItem = new Project();
				resultItem.setCreator(item.getCreator());
				resultItem.setDescription(item.getDescription());
				resultItem.setOwner(item.getOwner());
				resultItem.setProductLine(item.getProductLine());
				resultItem.setProjectName(item.getName());
				result.add(resultItem);
			}
			lc.set(CacheKeys.PROJECTS_CACHE_KEY, result);
		}		
		return result;
	}
	
	
	@Override
	public List<String> getProductLines() {
		List<String> result = lc.get(CacheKeys.PRODUCT_LINE_CACHE_KEY);
		if(result != null && !result.isEmpty())
			return result;
		result = new ArrayList<String>();
		List<AnoleProductLine> resultFromDB = anoleProductLineMapper.selectAll();
		if(resultFromDB!=null){
			for(AnoleProductLine item: resultFromDB){
				 result.add(item.getName());
			}
			lc.set(CacheKeys.PRODUCT_LINE_CACHE_KEY, result);
		}		
		return result;
	}

	@Override
	public List<String> getEnvs() {
		List<String> result = lc.get(CacheKeys.ENVS_CACHE_KEY);
		if(result != null && !result.isEmpty())
			return result;
		result = new ArrayList<String>();
		List<AnoleEnvironment> resultFromDB = anoleEnvironmentMapper.selectAll();
		if(resultFromDB != null){
			for(AnoleEnvironment item : resultFromDB){
				result.add(item.getName());
			}
			lc.set(CacheKeys.ENVS_CACHE_KEY, result);
		}
		return result;
	}

	@Override
	public InitializeEnvsResult initializeEnvs(InitializeEnvsDemand envsDemand) {
		InitializeEnvsResult result = new InitializeEnvsResult();
		try{
			Preconditions.checkNotNull(envsDemand, "Input is null!");
			Preconditions.checkArgument(envsDemand.getEnvs()!= null && !envsDemand.getEnvs().isEmpty(), "Input envs is null or empty!");
			Preconditions.checkArgument(envsDemand.getOperator()!= null && !envsDemand.getOperator().isEmpty(), "Operator is null or empty!");
			String [] envs = envsDemand.getEnvs().split(",");
			if(envs == null || envs.length == 0)
				throw new RuntimeException("Can not find any env in the input string.");
			for(String env : envs){
				if(env.length() > 15)
					throw new RuntimeException("The envs ('" + env +"') is too long. No more than 15 chars per env.");
			}
			for(String env: envs){
				AnoleEnvironment ae = new AnoleEnvironment();
				ae.setName(env);
				ae.setLastOperator("admin");
				anoleEnvironmentMapper.insert(ae);
			}
			result.setErrorMessage("OK");
			result.setSuccess(true);
			initialized = true;
		}
		catch(Exception e){
			result.setErrorMessage(e.getMessage());
			result.setSuccess(false);
		} 
		return result;
	}

	@Override
	public boolean isInitialized() { 
		return initialized;
	}

}
