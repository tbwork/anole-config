package org.anole.infrastructure.dao;

import java.util.List;

import org.anole.infrastructure.model.AnoleProductLine;  

public interface AnoleProductLineMapper extends MybatisBaseMapper<AnoleProductLine, Integer>{ 

	public AnoleProductLine selectByName(String name);
	
}