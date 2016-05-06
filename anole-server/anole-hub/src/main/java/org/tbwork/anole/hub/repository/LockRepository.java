package org.tbwork.anole.hub.repository;

public interface LockRepository {

     public Object getInsertLock(String key);
     
     public Object getUpdateLock(String key, String env);
	
     public Object removeInsertLock(String key);
     
     public Object removeUpdateLock(String key, String env);
}
