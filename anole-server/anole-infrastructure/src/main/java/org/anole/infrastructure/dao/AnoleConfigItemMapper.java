package org.anole.infrastructure.dao;

import java.util.List;

import org.anole.infrastructure.model.AnoleConfigItem;
import org.apache.ibatis.annotations.Param;

public interface AnoleConfigItemMapper extends MybatisBaseMapper<AnoleConfigItem, Integer> {  
    
    //custom interface
    AnoleConfigItem selectByConfigKeyAndEnv(@Param("key")String key, @Param("env")String env); 
    
    int updateByKeyAndEnv(AnoleConfigItem record);

}