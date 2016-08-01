package org.tbwork.anole.hub.cache;

import org.tbwork.anole.hub.cache.impl.localcache.model.CacheItem;

/**
 * A simple local cache used to store configuration items.
 * In future version, Anole will support other cache system
 * like Redis, Memcached, etc.
 * @author Tommy.Tang
 * @see #set(String, Object)
 * @see #set(String, Object, long)
 * @see #get(String)
 * @see #remove(String)
 * @see #recycle()
 */
public interface Cache {

	/**
	 * Add or update a new cache item to the local-cache,
	 * using the default expire time;
	 * @param key the cache key
	 * @param obj the cached item
	 */
	public <T> void set(String key, T obj);
	
	/**
	 * <p>Send set command to the cache system and return 
	 * immediately, rather than waiting for the set result
	 * from cache server. 
	 * <p>Notice that this method is same to {@link #set(String, Object)}
	 * in the LocalCache system.
	 * <p>See {@link #set(String, Object)} for more functional
	 * description. 
	 * @param key the cache key
	 * @param obj the cached item
	 */
	public <T> void asynSet(String key, T obj);
	
	/**
	 * Add or update a new cache item to the local-cache,
	 * and specify an expire time. For permanent cache
	 * item, you just need to set its expireTime as 0;
	 * @param key the cache key
	 * @param obj the cached item
	 * @param expireTime the expire time
	 */
	public <T> void set(String key, T obj, long expireTime) ;
	
	/**
	 * <p>Send set command to the cache system and return 
	 * immediately, rather than waiting for the set result
	 * from cache server. 
	 * <p>Notice that this method is same to {@link #set(String, Object, long)}
	 * in the LocalCache system.
	 * <p>See {@link #set(String, Object, long)} for more
	 * functional description. 
	 * @param key the cache key
	 * @param obj the cached item
	 * @param expireTime the expire time
	 */
	public <T> void asynSet(String key, T obj, long expireTime) ;
	
	/**
	 * Retrieve a cache item by key name;
	 */
	public <T> T get(String key);
	
	/**
	 * Remove specified cache item from the cache.
	 * @param key the cache key
	 */
	public void remove(String key);
	
	/**
	 * <p>Send set command to the cache system and return 
	 * immediately, rather than waiting for the set result
	 * from cache server. 
	 * <p>Notice that this method is same to {@link #remove(String)}
	 * in the LocalCache system.
	 * <p>See {@link #remove(String)} for more functional
	 * description. 
	 * @param key the cache key
	 */
	public void asynRemove(String key);
	
	/**
	 * Return true if the cache key is in the cache now,
	 * otherwise return false;
	 * @param key the cache key 
	 */
	public boolean contain(String key);
	
}
