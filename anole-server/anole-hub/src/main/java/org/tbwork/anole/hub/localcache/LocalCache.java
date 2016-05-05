package org.tbwork.anole.hub.localcache;

public interface LocalCache {

	public void set(String key, Object obj);
	
	public void set(String key, Object obj, int expireTime) ;
	
	public void remove(String key);
}
