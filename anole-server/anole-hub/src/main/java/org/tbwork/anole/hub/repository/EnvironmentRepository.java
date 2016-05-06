package org.tbwork.anole.hub.repository;

import java.util.List;

import org.tbwork.anole.hub.model.EnvDO;

public interface EnvironmentRepository {
 
	/**
	 * @return all environment records.
	 */
	public List<EnvDO> getAllEnvs();
	
	/**
	 * Return same value all the times except you
	 * changed your database when Anole is running 
	 * which is impossible. This function can be used 
	 * to identify whether a configuration item exists
	 *  or not because only combi-index of key and env
	 * is used in Anole. It is wasteful to create an 
	 * other specialized index for key.
	 * @return the first environment record.
	 */
	public String getFirstEnv();
	
	public void addEnv(EnvDO env, String operator);
	
}
