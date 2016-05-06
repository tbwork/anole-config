package org.tbwork.anole.hub.repository;

public interface LockRepository {

     public Object getInsertLock(String key);
     
     public Object getUpdateLock(String key, String env);
	
     public void removeInsertLock(String key);
     
     public void removeUpdateLock(String key, String env);
}
