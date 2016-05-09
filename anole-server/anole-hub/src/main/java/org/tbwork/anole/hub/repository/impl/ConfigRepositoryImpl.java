package org.tbwork.anole.hub.repository.impl;

import java.util.Date;

import org.anole.infrastructure.dao.AnoleConfigItemMapper; 
import org.anole.infrastructure.dao.AnoleConfigMapper;
import org.anole.infrastructure.model.AnoleConfig;
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
	private AnoleConfigMapper anoleConfigDao;
	
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
			AnoleConfig aci = anoleConfigDao.selectByConfigKey(key);
			// store to the cache
			if(aci != null)
				cache.asynSet(ckey, dpo2dmo(aci), 1000);
			return aci != null;
		}
	}
	
	private String buildConfigCacheKey(String env, String key){
		return env + key;
	}
	
	/**
	 * The caller must guarantee that only one thread is 
	 * running this method at certain moment.
	 */
	private void createConfiguration(ConfigDO cdo, String operator){
		AnoleConfig config = dmo2dpo(cdo);
	}

	
	private ConfigDO dpo2dmo(AnoleConfig dpo){
		ConfigDO dmo = new ConfigDO();  
		dmo.setConfigType(ConfigType.configType(dpo.getType()));
		dmo.setCreator(dpo.getCreator());
		dmo.setDescription(dpo.getDescription());
		dmo.setId(dpo.getId());
		dmo.setKey(dpo.getKey());
		dmo.setLastOpeartor(dpo.getLastOperator()); 
		return dmo;
	}
	
	private AnoleConfig dmo2dpo(ConfigDO cdo){
		AnoleConfig dpo = new AnoleConfig();
		dpo.setDescription(cdo.getDescription());
		dpo.setKey(cdo.getKey());
		dpo.setProject(project);
	}
	
	private String getPorjectName(String key){
		
		
		
	}
	
}
