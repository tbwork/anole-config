package org.tbwork.anole.gui.domain.config.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.tbwork.anole.gui.domain.config.IConfigSearchService;
import org.tbwork.anole.gui.domain.config.IConfigService;
import org.tbwork.anole.gui.domain.model.Config;
import org.tbwork.anole.gui.domain.model.ConfigBrief; 
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
import org.tbwork.anole.common.ConfigType;

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
	private IConfigSearchService configSearchService;
	
	@Autowired 
	@Qualifier("localCache")
	private Cache lc ;
	 
	
	@Override
	public List<ConfigBrief> getConfigsByProjectAndEnv(GetConfigsByProjectAndEnvDemand demand) { 
		List<ConfigBrief> result = new ArrayList<ConfigBrief>();
		String cacheKey = CacheKeys.buildConfigsForProjectKey(demand.getProject(), demand.getEnv());
		List<ConfigBrief> cachedData = lc.get(cacheKey);
		if(cachedData == null){
			cachedData = new ArrayList<ConfigBrief>();
			List<AnoleConfigCombine> dbData = anoleConfigCombineMapper.selectConfigsByProjectAndEnv(demand.getProject(), demand.getEnv());
			for(AnoleConfigCombine item : dbData ){
				cachedData.add(convert2Config(item));
			}
			lc.set(cacheKey, cachedData);
		}
		int permission = pers.getUserRole(demand.getProject(), demand.getOperator(), demand.getEnv()); 
		if(permission == 0){
			for(ConfigBrief item: cachedData){
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
			config.preCheck(); 
			ConfigChangeRequest ccr = new ConfigChangeRequest();
			ConfigModifyDTO cmDto = new ConfigModifyDTO();
			cmDto.setConfigType(ConfigType.configType(config.getDestConfigType()));
			cmDto.setValue(config.getDestValue());
			cmDto.setKey(config.getKey());
			cmDto.setProject(config.getProject());
			cmDto.setTimestamp(System.currentTimeMillis());
			cmDto.setEnv(config.getEnv());
			cmDto.setDescription(config.getDescription());
			cmDto.setCreateNew(true);
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

	private boolean checkExists(String key){
		AnoleConfig anoleConfig = anoleConfigMapper.selectByConfigKey(key);
		return anoleConfig != null;
	}
	
	@Override
	public ModifyConfigResult modifyConfig(ModifyConfigDemand config) {
		ModifyConfigResult result = new ModifyConfigResult();
		try{
			ConfigChangeRequest ccr = new ConfigChangeRequest();
			ConfigModifyDTO cmDto = new ConfigModifyDTO(); 
			cmDto.setConfigType(config.getConfigType());
			cmDto.setValue(config.getValue());
			cmDto.setKey(config.getKey());
			cmDto.setProject(config.getProject());
			cmDto.setTimestamp(System.currentTimeMillis());
			cmDto.setCreateNew(false);
			cmDto.setEnv(config.getEnv());
			cmDto.setDescription(config.getDescription());
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
	public ConfigBrief getConfigByKeyAndEnv(GetConfigByKeyAndEnvDemand demand) { 
		ConfigBrief config = null;
		AnoleConfigItem ci = anoleConfigItemMapper.selectByConfigKeyAndEnv(demand.getKey(), demand.getEnv());
		AnoleConfig ac = anoleConfigMapper.selectByConfigKey(demand.getKey());
		if(ci != null){
			config = new ConfigBrief();
			config.setDesc(ac.getDescription());
			config.setEnv(demand.getEnv());
			config.setKey(demand.getKey());
			config.setLastModifier(ci.getLastOperator());
			config.setType(ac.getType());
			config.setValue(ci.getValue());
		} 
		return config;
	}
 
	
	private ConfigBrief convert2Config(AnoleConfigCombine acc){
		ConfigBrief result = new ConfigBrief();
		result.setDesc(acc.getDescription());
		result.setEnv(acc.getEnvName());
		result.setKey(acc.getKey());
		result.setLastModifier(acc.getLastOperator());
		result.setType(acc.getType());
		result.setValue(acc.getValue());
		return result;
	}
	
	private ConfigBrief shiledValue(ConfigBrief org){
		ConfigBrief result = new ConfigBrief();
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

	@Override
	public Config getConfigByKeyCacheable(String key) {
		String cacheKey = CacheKeys.buildConfigCacheKey(key);
		Config result = lc.get(cacheKey);
		if(result != null) return result;
		result = new Config();
		AnoleConfig anoleConfig = anoleConfigMapper.selectByConfigKey(key);
		if(anoleConfig == null)
			return null;
		result.setDesc(anoleConfig.getDescription());
		result.setProject(anoleConfig.getProject());
		result.setType((int)anoleConfig.getType());
		Map<String,String> valueMap = new HashMap<String, String>();
		List<AnoleConfigItem> anoleConfigItems = anoleConfigItemMapper.selectByConfigKey(key);
		if(anoleConfigItems!=null){
			for(AnoleConfigItem item : anoleConfigItems){
				valueMap.put(item.getEnvName(), item.getValue());
			}
		}
		result.setValues(valueMap);
		lc.set(cacheKey, result, 5*60*1000);
		return result;
	}


}
