package org.anole.infrastructure.dao;

import java.util.List;

import org.anole.infrastructure.model.AnoleUser;
import org.anole.infrastructure.model.AnoleUserProjectMap;
import org.apache.ibatis.annotations.Param;

public interface AnoleUserProjectMapMapper extends MybatisBaseMapper<AnoleUserProjectMap, Integer> { 
 
	Integer selectRoleByProjectKeyEnv(@Param("username")String username, @Param("project")String project, @Param("env")String env);

}