package org.tbwork.anole.gui.domain.config.impl;

import java.util.ArrayList;
import java.util.List;

import org.anole.infrastructure.dao.AnoleConfigCombineMapper;
import org.anole.infrastructure.dao.AnoleConfigItemMapper;
import org.anole.infrastructure.dao.AnoleConfigMapper;
import org.anole.infrastructure.model.AnoleConfig;
import org.anole.infrastructure.model.AnoleConfigItem;
import org.anole.infrastructure.model.custom.AnoleConfigCombine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.tbwork.anole.common.model.ConfigModifyDTO;
import org.tbwork.anole.gui.domain.cache.Cache;
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
import org.tbwork.anole.gui.domain.permission.IPermissionService;
import org.tbwork.anole.gui.domain.user.IUserService;
import org.tbwork.anole.gui.domain.util.CacheKeys;
import org.tbwork.anole.publisher.core.AnolePublisher;
import org.tbwork.anole.publisher.model.ConfigChangeRequest;
import org.tbwork.anole.publisher.model.ConfigChangeResponse;

@Service
public class ConfigService implements IConfigService {

	
	@Autowired
	private AnoleConfigItemMapper anoleConfigItemMapper;
	
	@Autowired
	private AnoleConfigMapper anoleConfigMapper;
	
	@Autowired
	private AnoleConfigCombineMapper anoleConfigCombineMapper;
	
	@Autowired
	private IUserService us;
	
	@Autowired
	private IPermissionService pers;
	 
	@Autowired 
	@Qualifier("localCache")
	private Cache lc ;
	 
	
	@Override
	public List<Config> getConfigsByProjectAndEnv(GetConfigsByProjectAndEnvDemand demand) { 
		List<Config> result = new ArrayList<Config>();
		String cacheKey = CacheKeys.buildConfigsForProjectKey(demand.getProject(), demand.getEnv());
		List<Config> cachedData = lc.get(cacheKey);
		if(cachedData == null){
			cachedData = new ArrayList<Config>();
			List<AnoleConfigCombine> dbData = anoleConfigCombineMapper.selectConfigsByProjectAndEnv(demand.getProject(), demand.getEnv());
			for(AnoleConfigCombine item : dbData ){
				cachedData.add(convert2Config(item));
			}
			lc.set(cacheKey, cachedData);
		}
		int permission = pers.getPermission(demand.getProject(), demand.getOperator(), demand.getEnv()); 
		if(permission == 0){
			for(Config item: cachedData){
				result.add(shiledValue(item)); 
			} 
		}
		else
			result = cachedData;
		
		return result;
	}

	@Override
	public AddConfigResult addConfig(AddConfigDemand config) {
		AddConfigResult result = new AddConfigResult();
		try{
			ConfigChangeRequest ccr = new ConfigChangeRequest();
			ConfigModifyDTO cmDto = new ConfigModifyDTO();
			cmDto.setDestConfigType(config.getDestConfigType());
			cmDto.setDestValue(config.getDestValue());
			cmDto.setKey(config.getKey());
			cmDto.setProject(config.getProject());
			cmDto.setTimestamp(System.currentTimeMillis());
			ccr.setConfigChangeDTO(cmDto);
			ccr.setOperator(config.getOperator());
			ConfigChangeResponse response = AnolePublisher.add(ccr);
			if(response == null)
				throw new RuntimeException("Add failed.");
			result.setSuccess(response.isSuccess());
			result.setErrorMessage(response.getErrorMessage());
		}
		catch(Exception e){
			result.setErrorMessage(e.getMessage());
			result.setSuccess(false);
		} 
		return result;
	}

	@Override
	public ModifyConfigResult modifyConfig(ModifyConfigDemand config) {
		ModifyConfigResult result = new ModifyConfigResult();
		try{
			ConfigChangeRequest ccr = new ConfigChangeRequest();
			ConfigModifyDTO cmDto = new ConfigModifyDTO();
			cmDto.setOriConfigType(config.getOriConfigType());
			cmDto.setOrigValue(config.getOrigValue());
			cmDto.setDestConfigType(config.getDestConfigType());
			cmDto.setDestValue(config.getDestValue());
			cmDto.setKey(config.getKey());
			cmDto.setProject(config.getProject());
			cmDto.setTimestamp(System.currentTimeMillis());
			cmDto.setEnv(config.getEnv());
			ccr.setConfigChangeDTO(cmDto);
			ccr.setOperator(config.getOperator());
			ConfigChangeResponse response = AnolePublisher.edit(ccr);
			if(response == null)
				throw new RuntimeException("Add failed.");
			result.setSuccess(response.isSuccess());
			result.setErrorMessage(response.getErrorMessage());
		}
		catch(Exception e){
			result.setErrorMessage(e.getMessage());
			result.setSuccess(false);
		} 
		return result;
	}

	@Override
	public Config getConfigByKeyAndEnv(GetConfigByKeyAndEnvDemand demand) { 
		Config config = null;
		AnoleConfigItem ci = anoleConfigItemMapper.selectByConfigKeyAndEnv(demand.getKey(), demand.getEnv());
		AnoleConfig ac = anoleConfigMapper.selectByConfigKey(demand.getKey());
		if(ci != null){
			config = new Config();
			config.setDesc(ac.getDescription());
			config.setEnv(demand.getEnv());
			config.setKey(demand.getKey());
			config.setLastModifier(ci.getLastOperator());
			config.setType(ac.getType());
			config.setValue(ci.getValue());
		} 
		return config;
	}
 
	
	private Config convert2Config(AnoleConfigCombine acc){
		Config result = new Config();
		result.setDesc(acc.getDescription());
		result.setEnv(acc.getEnvName());
		result.setKey(acc.getKey());
		result.setLastModifier(acc.getLastOperator());
		result.setType(acc.getType());
		result.setValue(acc.getValue());
		return result;
	}
	
	private Config shiledValue(Config org){
		Config result = new Config();
		result.setDesc(org.getDesc());
		result.setEnv(org.getEnv());
		result.setKey(org.getKey());
		result.setLastModifier(org.getLastModifier());
		result.setType(org.getType()); 
		result.setValue("==NO RIGHT==");  
		return result;
	}

	@Override
	public DeleteConfigResult deleteConfig(DeleteConfigDemand demand) { 
		DeleteConfigResult result = new DeleteConfigResult(); 
		try{
			if(!pers.isOwner(demand.getProject(), demand.getOperator())) 
				throw new RuntimeException("Permission denied!!");
			anoleConfigCombineMapper.deleteConfigByKey(demand.getKey());
			result.setErrorMessage("OK");
			result.setSuccess(true);
		}
		catch(Exception e){
			result.setErrorMessage(e.getMessage());
			result.setSuccess(false);
		}
		return result;
	} 
}
