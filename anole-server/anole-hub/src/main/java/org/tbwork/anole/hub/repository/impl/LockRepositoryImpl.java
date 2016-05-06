package org.tbwork.anole.hub.repository.impl;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Service;
import org.tbwork.anole.hub.StaticConfiguration;
import org.tbwork.anole.hub.cache.impl.localcache.LocalCache;
import org.tbwork.anole.hub.repository.LockRepository;

@Service
public class LockRepositoryImpl implements LockRepository{

	@Autowired
	private LocalCache lc;
	
	@Autowired
	private final Object lockOfNewLockOperation = new Object();
	
	@Override
	public Object getInsertLock(String key) {
		  String ckey = buildInsertCacheKey(key);
		  return getLock(ckey);
	}

	@Override
	public Object getUpdateLock(String key, String env) {
		String ckey = buildUpdateCacheKey(key, env);
		return getLock(ckey);
	}

	private Object getLock(String ckey){
		if(lc.get(ckey) == null){
			  synchronized(lockOfNewLockOperation){
				  if(lc.get(ckey) == null){ 
					  lc.set(ckey, new Object(), StaticConfiguration.ESTIMATED_INSERT_KEY_LIFETIME);
				  }
			  }
	    }
	    return lc.get(ckey);
	}
	
	private String buildInsertCacheKey(String key){
		return "insert" + key;
	}
	private String buildUpdateCacheKey(String key, String env){
		return "update" + env + key;
	}

	@Override
	public void removeInsertLock(String key) {
		String ckey = buildInsertCacheKey(key);
		lc.
		return null;
	}

	@Override
	public void removeUpdateLock(String key, String env) {
		// TODO Auto-generated method stub
		return null;
	}
}
