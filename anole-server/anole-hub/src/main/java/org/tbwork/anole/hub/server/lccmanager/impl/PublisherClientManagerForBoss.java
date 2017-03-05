package org.tbwork.anole.hub.server.lccmanager.impl;

import io.netty.channel.socket.SocketChannel;

import java.util.Date;

import org.anole.infrastructure.dao.AnoleConfigItemMapper;
import org.anole.infrastructure.dao.AnoleConfigMapper;
import org.anole.infrastructure.dao.AnoleUserProjectMapMapper;
import org.anole.infrastructure.model.AnoleConfig;
import org.anole.infrastructure.model.AnoleConfigItem;
import org.anole.infrastructure.model.AnoleEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; 
import org.tbwork.anole.common.enums.Role;
import org.tbwork.anole.common.model.ConfigModifyResultDTO;
import org.tbwork.anole.common.model.ConfigModifyDTO;
import org.tbwork.anole.hub.repository.EnvironmentRepository;
import org.tbwork.anole.hub.server.lccmanager.model.clients.LongConnectionClient;
import org.tbwork.anole.hub.server.lccmanager.model.clients.PublisherClient; 
import org.tbwork.anole.hub.server.lccmanager.model.requests.RegisterRequest; 

import com.google.common.base.Preconditions; 

/**
 * Publisher manager used by boss server.
 * @author tommy.tang
 */
@Service("publisherClientManager")
public class PublisherClientManagerForBoss  extends LongConnectionClientManager {

	private static final Logger logger = LoggerFactory.getLogger(PublisherClientManagerForBoss.class); 
 
	@Autowired
	private AnoleConfigItemMapper anoleConfigItemMapper;
	@Autowired
	private AnoleUserProjectMapMapper anoleUserProjectMapMapper;
	@Autowired
	private AnoleConfigMapper anoleConfigMapper;
	
	@Autowired
	private EnvironmentRepository environmentRepository;
	
	public static enum Operation{
		VIEW(1),   // never used in publisher.
		MODIFY(2), // Modifying a configuration needs at least admin right
		CREATE(2), // Creating a configuration needs at least admin right
		DELETE(3); // Deleting a configuration needs at least owner right
		private int level;
		private Operation(int level){
			this.level = level;
		}
	}
	
	@Override
	protected LongConnectionClient createClient(int token, RegisterRequest registerRequest) { 
		return new PublisherClient(token, registerRequest.getSocketChannel());
	} 
	
	private void createConfigItem(AnoleConfigItem configItem){ 
		if("all".equals(configItem.getEnvName())){
			for(AnoleEnvironment env : environmentRepository.getEnviroments()){
				configItem.setEnvName(env.getName());
				anoleConfigItemMapper.insert(configItem);
			}
		}
		else{
			anoleConfigItemMapper.insert(configItem);
		}
	}
	
	private void updateConfigItem(AnoleConfigItem configItem, String operator, String project){
		Date now = new Date();
		if("all".equals(configItem.getEnvName())){
			for(AnoleEnvironment env : environmentRepository.getEnviroments()){
				rightCheck(operator, project, Operation.MODIFY, env.getName());
				configItem.setEnvName(env.getName());
				anoleConfigItemMapper.updateByKeyAndEnv(configItem);
			}
		}
		else{
			rightCheck(operator, project, Operation.MODIFY, configItem.getEnvName());
			anoleConfigItemMapper.insert(configItem);
		}
	}
	
 
	public ConfigModifyResultDTO motifyConfig(String operator, ConfigModifyDTO ccd){
		ConfigModifyResultDTO result  = new ConfigModifyResultDTO();
		try{  
			Preconditions.checkArgument(operator!=null && !operator.isEmpty(), "The operator should not be null or empty.");
			Preconditions.checkNotNull (ccd, "Config change content should not be null.");
			Preconditions.checkArgument(ccd.getProject()!=null && !ccd.getProject().isEmpty(), "A project should be specified before you changing its configurations.");		
			basickPreCheck(operator, ccd); 
			AnoleConfig anoleConfig = anoleConfigMapper.selectByConfigKey(ccd.getKey()); 
			if(ccd.isCreateNew()){ //new configuration 
				rightCheck(operator, ccd.getProject(), Operation.CREATE, null); 
				if(anoleConfig != null){
					throw new RuntimeException("The config with key <"+ccd.getKey()+"> is already existed");
				}
				Date now = new Date();
				//create config first
				anoleConfig = new AnoleConfig();
				anoleConfig.setCreateTime(now);
				anoleConfig.setCreator(operator);
				anoleConfig.setDescription(ccd.getDescription());
				anoleConfig.setKey(ccd.getKey());
				anoleConfig.setLastOperator(operator);
				anoleConfig.setProject(ccd.getProject());
				anoleConfig.setType(ccd.getConfigType().index());
				anoleConfig.setUpdateTime(now);
				anoleConfigMapper.insert(anoleConfig);
				
				//create 
				AnoleConfigItem configItem = new AnoleConfigItem();
				configItem.setCreateTime(now);
				configItem.setEnvName(ccd.getEnv());
				configItem.setKey(ccd.getKey());
				configItem.setLastOperator(operator);
				configItem.setUpdateTime(now);
				configItem.setValue(ccd.getValue());
				createConfigItem(configItem);
			}
			else{
				if(anoleConfig == null){
					throw new RuntimeException("The config with key <"+ccd.getKey()+"> is not existed");
				}
				Date now = new Date(); 
				if(anoleConfig.getDescription() == null && !ccd.getDescription().isEmpty()
				|| 	anoleConfig.getDescription() != null && anoleConfig.getDescription().equals(ccd.getDescription())
						){
					anoleConfig.setUpdateTime(now);
					anoleConfig.setDescription(ccd.getDescription());
					anoleConfigMapper.updateByPrimaryKey(anoleConfig);
				}
				
				AnoleConfigItem configItem = new AnoleConfigItem();
				configItem.setKey(ccd.getKey());
				configItem.setLastOperator(operator); 
				configItem.setValue(ccd.getValue());
				configItem.setEnvName(ccd.getEnv());
				configItem.setUpdateTime(now);
				updateConfigItem(configItem, operator, ccd.getEnv());
			}
			result.setErrorMsg("OK");
			result.setSuccess(true);
		}
		catch(Exception e){
			logger.error("Add config failed, details: {}", e.getMessage());
			result.setSuccess(false);
			result.setErrorMsg("Add config failed, details: " + e.getMessage());
		}
		return result;
	} 
	
	private boolean validateRight(String operator, String project, Operation operation, String env){
		if(Role.ADMIN._name().equals(operator))
			return true;
		Integer roleValue = anoleUserProjectMapMapper.selectRoleByProjectKeyEnv(operator, project, env);
		if( roleValue == null) return false;
		Role role = Role.getRoleByValue(roleValue);
		if(operation.equals(Operation.CREATE)){//增
			return role.value() >= Role.STRANGER.value();
		}
		if(operation.equals(Operation.DELETE)){//删
			return role.value() >= Role.OWNER.value();
		}
		if(operation.equals(Operation.MODIFY)){//改
			return role.value() >= Role.MANAGER.value();
		}
		if(operation.equals(Operation.VIEW)){  //查
			return role.value() >= Role.VISTOR.value();
		}
		return false;
	}
	
	private void basickPreCheck(String operator, ConfigModifyDTO modifyDTO){
		Preconditions.checkArgument(modifyDTO.getKey()!=null && !modifyDTO.getKey().isEmpty(), "Key must be specified!");
		Preconditions.checkArgument(modifyDTO.getEnv()!=null && !modifyDTO.getEnv().isEmpty(), "Env must be specified!");
		Preconditions.checkArgument(modifyDTO.getProject()!=null && !modifyDTO.getProject().isEmpty(), "Project must be specified!");
		Preconditions.checkNotNull (modifyDTO.getConfigType(),"Config type must be specified!");  
	}
	 
	private void rightCheck(String operator, String project, Operation opeartion, String env){ 
		Preconditions.checkArgument(validateRight(operator, project, opeartion, env), "The operator ("+operator+") has no right to this operation.");
	}
	
}






