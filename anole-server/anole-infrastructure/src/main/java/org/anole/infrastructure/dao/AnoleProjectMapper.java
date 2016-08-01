package org.anole.infrastructure.dao;

import java.util.List;

import org.anole.infrastructure.model.AnoleProductLine;
import org.anole.infrastructure.model.AnoleProject;

public interface AnoleProjectMapper  extends MybatisBaseMapper<AnoleProject, Integer> { 
	
	public int countProject(String name);
	
	public AnoleProject selectByName(String name);
}