package org.anole.infrastructure.dao;

import org.anole.infrastructure.model.AnoleConfigItem;
import org.anole.infrastructure.model.AnoleConfigItemWithBLOBs;
import org.apache.ibatis.annotations.Param;

public interface AnoleConfigItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AnoleConfigItemWithBLOBs record);

    int insertSelective(AnoleConfigItemWithBLOBs record);

    AnoleConfigItemWithBLOBs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AnoleConfigItemWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(AnoleConfigItemWithBLOBs record);

    int updateByPrimaryKey(AnoleConfigItem record);
    
    //custom method
    AnoleConfigItemWithBLOBs selectByKeyAndEnv(@Param("key")String key, @Param("env")String env);
    
    AnoleConfigItemWithBLOBs selectByKeyAndEnvWithoutStatus(@Param("key")String key, @Param("env")String env);
    
    int resetConfigItem(AnoleConfigItemWithBLOBs record);
    
}