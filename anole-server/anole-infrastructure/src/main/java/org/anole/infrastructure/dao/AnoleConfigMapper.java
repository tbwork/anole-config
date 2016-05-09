package org.anole.infrastructure.dao;

import java.util.List;

import org.anole.infrastructure.model.AnoleConfig;
import org.apache.ibatis.annotations.Param;

public interface AnoleConfigMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AnoleConfig record);

    AnoleConfig selectByPrimaryKey(Integer id);

    List<AnoleConfig> selectAll();

    int updateByPrimaryKey(AnoleConfig record);
    
    //Custom method
    AnoleConfig selectByConfigKey(@Param("key") String key);
}