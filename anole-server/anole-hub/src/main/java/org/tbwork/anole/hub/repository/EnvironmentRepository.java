package org.tbwork.anole.hub.repository;

import java.util.List;

import org.tbwork.anole.hub.model.EnvDO;

public interface EnvironmentRepository {
 
	public List<EnvDO> getAllEnvs();
	
	public String getAnyoneEnv();
	
	public void addEnv(EnvDO env, String operator);
	
}
