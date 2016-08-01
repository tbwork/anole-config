package org.anole.infrastructure.dao;

import java.util.List; 

import org.anole.infrastructure.model.AnoleUser;

public interface AnoleUserMapper extends MybatisBaseMapper<AnoleUser, Integer> {  

	AnoleUser selectByUsername(String username);
}