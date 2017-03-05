package org.tbwork.anole.hub.repository.impl;

import java.util.ArrayList;
import java.util.List;

import org.anole.infrastructure.dao.AnoleEnvironmentMapper;
import org.anole.infrastructure.model.AnoleEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.tbwork.anole.hub.repository.EnvironmentRepository;

@Repository("environmentRepository")
public class EnvironmentRepositoryImpl implements EnvironmentRepository{

	private List<AnoleEnvironment> envs; 
	
	@Autowired
	private AnoleEnvironmentMapper anoleEnvironmentMapper;
	
	@Override
	public List<AnoleEnvironment> getEnviroments() {
		if(envs != null){
			return envs;
		} 
		List<AnoleEnvironment> result = anoleEnvironmentMapper.selectAll();
		return result == null ? new ArrayList<AnoleEnvironment>() : result;
	}

}
