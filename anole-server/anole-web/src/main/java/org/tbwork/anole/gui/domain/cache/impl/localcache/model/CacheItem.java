package org.tbwork.anole.gui.domain.cache.impl.localcache.model;

import lombok.Data;

@Data
public class CacheItem<T>{

	private T item;
	private long expireTime;
	private long startTime;
	
}
