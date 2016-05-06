package org.tbwork.anole.hub.repository;

public interface LockRepository {

     /**
      * Get a lock of insert operation.
     * @param key the key of the configuration item.
     * @return
     */
    public Object getInsertLock(String key);
     
     /**
      * Get a lock of update operation.
     * @param key the key of the configuration item.
     * @param env the env name
     * @return
     */
    public Object getUpdateLock(String key, String env);
	
     /**
      * Remove the lock of insert operation.
      * @param key the key of the configuration item.
      */
     public void removeInsertLock(String key);
      
     /**
      * Remove the lock of update operation.
      * @param key the key of the configuration item.
      * @param env the env name
      */
    public void removeUpdateLock(String key, String env);
}
