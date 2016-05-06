package org.tbwork.anole.hub.cache.impl.localcache.model;

import lombok.Data;

@Data
public class CacheItem<T>{

	private T item;
	private long expireTime;
	private long startTime;
	
}
