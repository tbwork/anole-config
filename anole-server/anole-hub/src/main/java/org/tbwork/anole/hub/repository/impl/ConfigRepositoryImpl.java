package org.tbwork.anole.hub.repository.impl;

import java.util.Date;

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
		String cacheKey = buildConfigCacheKey(env, key);
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
		boolean flag = false;
		if(!checkExistsAndReturn(config.getKey())){
			synchronized(lr.getInsertLock(config.getKey())){
				if(!checkExistsAndReturn(config.getKey())){
					createConfiguration(config, operator); 
					flag = true;
				}
			} 
		} 
		if(flag)
			lr.removeInsertLock(config.getKey());
		throw new ConfigItemAlreadyExistsException(config.getKey());
	}

	@Override
	public void setConfig(ConfigDO config, String env, String operator) {
		
		String key = config.getKey();
		String cacheKey = buildConfigCacheKey(env, key);
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
		String firstEnv = envRepo.getFirstEnv();
		String ckey = buildConfigCacheKey(key, firstEnv);
		if(cache.contain(ckey)) // check cache first.
			return true;
		else{// then check the database
			AnoleConfigItemWithBLOBs aci = anoleConfigItemDao.selectByKeyAndEnv(key, firstEnv);
			// store to the cache
			if(aci != null)
				cache.asynSet(ckey, parseDPO(aci), 1000);
			return aci != null;
		}
	}
	
	private String buildConfigCacheKey(String env, String key){
		return env + key;
	}
	
	private void createConfiguration(ConfigDO cdo, String operator){
		String firstEnv = envRepo.getFirstEnv();
		//Check whether invalid configuration item exists in the database
		AnoleConfigItemWithBLOBs aci = anoleConfigItemDao.selectByKeyAndEnvWithoutStatus(cdo.getKey(), firstEnv);
		boolean invalidRecordExisted = aci != null && aci.getStatus().byteValue() == (byte)0;
		aci = formMutableDPO(cdo); 
		for(EnvDO item: envRepo.getAllEnvs()){
			String envName = item.getName();
			aci.setEnvName(envName);
			aci.setCreateTime(new Date());
			aci.setUpdateTime(new Date());
			aci.setLastOperator(operator);
			aci.setCreator(operator); 
			aci.setStatus((byte)1);
			if(invalidRecordExisted)
				anoleConfigItemDao.resetConfigItem(aci);
			else
			    anoleConfigItemDao.insert(aci);
		}
	}

	
}
