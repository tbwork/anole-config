package org.tbwork.anole.hub.repository.impl;

import org.anole.infrastructure.dao.AnoleConfigItemMapper;
import org.anole.infrastructure.model.AnoleConfigItemWithBLOBs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tbwork.anole.common.ConfigType;
import org.tbwork.anole.hub.cache.Cache;
import org.tbwork.anole.hub.exceptions.ConfigItemAlreadyExistsException;
import org.tbwork.anole.hub.model.ConfigDO;
import org.tbwork.anole.hub.model.EnvDO;
import org.tbwork.anole.hub.repository.ConfigRepository; 
import org.tbwork.anole.hub.repository.EnvironmentRepository;
import org.tbwork.anole.hub.repository.LockRepository;

import com.google.common.base.Preconditions;

@Service
public class ConfigRepositoryImpl implements ConfigRepository{

	@Autowired
	private Cache cache;
	
	@Autowired
	private LockRepository lr;
	
	@Autowired
	private AnoleConfigItemMapper anoleConfigItemDao;
	
	@Autowired
	private EnvironmentRepository envRepo;
	
	@Override
	public ConfigDO retrieveConfigByKey(String key, String env) {
		String cacheKey = getCacheKey(env, key);
		ConfigDO cdo = cache.get(cacheKey);
		if(cdo != null){
			return cdo;
		} 
		AnoleConfigItemWithBLOBs aci = anoleConfigItemDao.selectByKeyAndEnv(key, env);
		if(aci != null)
		{
			cdo = parseDPO(aci);
			cache.set(cacheKey, cdo); // update lc
		} 
		return null;
	}
	
	@Override
	public void addConfig(ConfigDO config, String operator) {  
		if(!checkExistsAndReturn(config.getKey())){
			synchronized(lr.getInsertLock(config.getKey())){
				if(!checkExistsAndReturn(config.getKey())){
					createConfiguration(config, operator); 
				}
			} 
		} 
		throw new ConfigItemAlreadyExistsException(config.getKey());
	}

	@Override
	public void setConfig(ConfigDO config, String env, String operator) {
		
		String key = config.getKey();
		String cacheKey = getCacheKey(env, key);
		// Asynchronously remove cache item.
		cache.asynRemove(cacheKey);
		
		// update database
		AnoleConfigItemWithBLOBs aci = anoleConfigItemDao.selectByKeyAndEnvWithoutStatus(key, env);
		if(aci == null){
			aci = formMutableDPO(config);
			aci.setEnvName(env);
			aci.setLastOperator(operator);
			aci.setCreator(operator); 
			aci.setStatus((byte)1);
			int id = anoleConfigItemDao.insert(aci);
			aci.setId(id);
		}
		else //already exists
		{ 
			AnoleConfigItemWithBLOBs tempAci = new AnoleConfigItemWithBLOBs();
			tempAci.setId(aci.getId());
			// fields that need to be updated.
			tempAci.setEnvName(env);
			tempAci.setLastOperator(operator);
			tempAci.setValue(config.getValue());
			tempAci.setDescription(config.getDescription());
		}
		
		// Asynchronously update cache
		
	}
	
	private ConfigDO parseDPO(AnoleConfigItemWithBLOBs dpo){
		ConfigDO result = new ConfigDO(); 
		result.setValue(dpo.getValue());
		result.setDescription(dpo.getDescription());
		result.setConfigType(ConfigType.configType(dpo.getType()));
		result.setKey(dpo.getKey()); 
		return result;
	}
	
	/**
	 * Mutable fields of ConfigDO: {@link ConfigDO}
	 */
	private AnoleConfigItemWithBLOBs formMutableDPO(ConfigDO cdo){
		AnoleConfigItemWithBLOBs result = new AnoleConfigItemWithBLOBs(); 
		result.setDescription(cdo.getDescription());
		result.setValue(cdo.getValue());
		result.setType(cdo.getConfigType().index()); 
		return result;
	}
 
	private boolean checkExistsAndReturn(String key){
		先看看缓存有没有
		String anyEnv = envRepo.getAnyoneEnv();
		AnoleConfigItemWithBLOBs aci = anoleConfigItemDao.selectByKeyAndEnvWithoutStatus(key, anyEnv);
		return aci == null;
	}
	
	private String getCacheKey(String env, String key){
		return env + key;
	}
	
	private void createConfiguration(ConfigDO cdo, String operator){
		for(EnvDO item: envRepo.getAllEnvs()){
			String envName = item.getName();
			AnoleConfigItemWithBLOBs aci = formMutableDPO(cdo);
			aci.setEnvName(envName);
			aci.setLastOperator(operator);
			aci.setCreator(operator); 
			aci.setStatus((byte)1);
			anoleConfigItemDao.insert(aci);
		}
	}

	
}
