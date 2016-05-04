package org.anole.infrastructure.dao;

import org.anole.infrastructure.model.AnoleConfigItem;
import org.anole.infrastructure.model.AnoleConfigItemWithBLOBs;

public interface AnoleConfigItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AnoleConfigItemWithBLOBs record);

    int insertSelective(AnoleConfigItemWithBLOBs record);

    AnoleConfigItemWithBLOBs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AnoleConfigItemWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(AnoleConfigItemWithBLOBs record);

    int updateByPrimaryKey(AnoleConfigItem record);
}