package org.tbwork.anole.gui.domain.cache.impl.localcache.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tbwork.anole.gui.domain.cache.impl.localcache.LocalCache; 
@Component("localCacheRecycle") 
public class LocalCacheSpaceRecycle {

	@Autowired
	private LocalCache localCache;
	private static final int  CACHE_RECYCLE_INTERVAL = 1000; // 1s
	@Scheduled(fixedDelay = CACHE_RECYCLE_INTERVAL)
	public void run(){
		localCache.recycle();
	}
}
