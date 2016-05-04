package org.anole.infrastructure.dao;

import org.anole.infrastructure.model.AnoleProject;

public interface AnoleProjectMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AnoleProject record);

    int insertSelective(AnoleProject record);

    AnoleProject selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AnoleProject record);

    int updateByPrimaryKeyWithBLOBs(AnoleProject record);

    int updateByPrimaryKey(AnoleProject record);
}