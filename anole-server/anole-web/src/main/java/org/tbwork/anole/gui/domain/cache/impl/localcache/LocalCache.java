package org.tbwork.anole.gui.domain.cache.impl.localcache;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.tbwork.anole.gui.domain.cache.Cache;
import org.tbwork.anole.gui.domain.cache.impl.localcache.model.CacheItem; 

@Service("localCache")
public class LocalCache implements Cache{

	public static final Map<String, CacheItem> lc = new ConcurrentHashMap<String, CacheItem>();  
  
	private static final long defaultExpireTime = 5*60*1000;
	@Override
	public <T> void set(String key, T obj) {
		set(key, obj, defaultExpireTime); 
	}

	@Override
	public <T> void set(String key, T obj, long expireTime) {
		CacheItem item = null;
		if(lc.containsKey(key)){
			item = lc.get(key); 
		}
		if(item == null)
			item = new CacheItem();
		item.setItem(obj); 
		item.setExpireTime(expireTime);
		item.setStartTime(System.currentTimeMillis());
		lc.put(key, item);
	}

	@Override
	public void remove(String key) {
		lc.remove(key);
	}

	@Override
	public <T> T get(String key) {
		CacheItem item = lc.get(key);
		if(item != null){
			item.setStartTime(System.currentTimeMillis());
			return (T) item.getItem();
		} 
		return null;
	} 
	 
	public void recycle() {
		Set<Entry<String, CacheItem>> entrySet = lc.entrySet();
		for(Entry<String, CacheItem> item : entrySet){
			CacheItem ci = item.getValue();
			if(ci == null || (ci != null && ci.getExpireTime() > 0 && System.currentTimeMillis() >= (ci.getStartTime() + ci.getExpireTime())))
				lc.remove(item.getKey()); 
		} 
	}

	@Override
	public <T> void asynSet(String key, T obj) {
		set(key, obj);
	}

	@Override
	public <T> void asynSet(String key, T obj, long expireTime) { 
		set(key, obj, expireTime);
	}

	@Override
	public void asynRemove(String key) {
		remove(key);
	}

	@Override
	public boolean contain(String key) { 
		return lc.containsKey(key);
	} 
}
