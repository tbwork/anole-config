package org.tbwork.anole.hub.cache.impl.localcache.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tbwork.anole.hub.StaticConfiguration;
import org.tbwork.anole.hub.cache.Cache; 
import org.tbwork.anole.hub.cache.impl.localcache.LocalCache;
@Component("localCacheRecycle") 
public class LocalCacheSpaceRecycle {

	@Autowired
	private LocalCache localCache;
	
	@Scheduled(fixedDelay = StaticConfiguration.CACHE_RECYCLE_INTERVAL)
	public void run(){
		localCache.recycle();
	}
}
