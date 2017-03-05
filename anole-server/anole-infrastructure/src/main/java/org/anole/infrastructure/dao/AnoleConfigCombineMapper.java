package org.anole.infrastructure.dao;

import java.util.Date;
import java.util.List;

import org.anole.infrastructure.model.AnoleConfig;
import org.anole.infrastructure.model.AnoleConfigItem;
import org.anole.infrastructure.model.custom.AnoleConfigCombine;
import org.apache.ibatis.annotations.Param;

public interface AnoleConfigCombineMapper{ 
	 
	//Custom method
    List<AnoleConfigCombine> selectConfigsByProjectAndEnv(@Param("project") String project, @Param("env") String env);

    int deleteConfigByKey(String key);
    

}