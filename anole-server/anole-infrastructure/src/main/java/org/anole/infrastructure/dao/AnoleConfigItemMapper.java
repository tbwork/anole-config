package org.anole.infrastructure.dao;

import java.util.Date;
import java.util.List;

import org.anole.infrastructure.model.AnoleConfig;
import org.anole.infrastructure.model.AnoleConfigItem;
import org.anole.infrastructure.model.custom.AnoleConfigCombine;
import org.apache.ibatis.annotations.Param;

public interface AnoleConfigItemMapper extends MybatisBaseMapper<AnoleConfigItem, Integer> {  
	//custom interface
    AnoleConfigItem selectByConfigKeyAndEnv(@Param("key")String key, @Param("env")String env); 
    
    List<AnoleConfigItem> selectByConfigKey(@Param("key")String key); 
    
    int updateByKeyAndEnv(AnoleConfigItem record);
 
    //Custom method
    List<AnoleConfigItem> selectConfigItemsByUpdatedTime(@Param("updateTime") Date updateTime);
}