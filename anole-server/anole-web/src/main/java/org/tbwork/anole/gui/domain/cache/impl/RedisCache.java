package org.tbwork.anole.gui.domain.cache.impl;

import org.tbwork.anole.gui.domain.cache.Cache;

/**
 * Not implemented yet. :)
 * @author Tommy.Tang
 */
public class RedisCache implements Cache{

	@Override
	public <T> void set(String key, T obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> void set(String key, T obj, long expireTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> T get(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> void asynSet(String key, T obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> void asynSet(String key, T obj, long expireTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void asynRemove(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean contain(String key) {
		// TODO Auto-generated method stub
		return false;
	}

}
