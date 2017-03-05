package org.anole.infrastructure.dao;

import java.util.Date;
import java.util.List;

import org.anole.infrastructure.model.AnoleConfig;
import org.anole.infrastructure.model.AnoleConfigItem;
import org.apache.ibatis.annotations.Param;

public interface AnoleConfigMapper extends MybatisBaseMapper<AnoleConfig, Integer>{ 
	 
	//Custom method
    AnoleConfig selectByConfigKey(@Param("key") String key);
    
	//Custom method
    List<AnoleConfig> selectConfigsByUpdatedTime(@Param("updateTime") Date updateTime);
}