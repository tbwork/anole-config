package org.anole.infrastructure.dao;

import java.util.List;

import org.anole.infrastructure.model.AnoleConfigItem;
import org.apache.ibatis.annotations.Param;

public interface AnoleConfigItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AnoleConfigItem record);

    AnoleConfigItem selectByPrimaryKey(Integer id);

    List<AnoleConfigItem> selectAll();

    int updateByPrimaryKey(AnoleConfigItem record);
    
    //custom interface
    AnoleConfigItem selectByConfigKeyAndEnv(@Param("key")String key, @Param("env")String env); 
    
    int updateByKeyAndEnv(AnoleConfigItem record);

}