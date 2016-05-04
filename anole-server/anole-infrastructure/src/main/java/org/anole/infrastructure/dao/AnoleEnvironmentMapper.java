package org.anole.infrastructure.dao;

import org.anole.infrastructure.model.AnoleEnvironment;

public interface AnoleEnvironmentMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AnoleEnvironment record);

    int insertSelective(AnoleEnvironment record);

    AnoleEnvironment selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AnoleEnvironment record);

    int updateByPrimaryKeyWithBLOBs(AnoleEnvironment record);

    int updateByPrimaryKey(AnoleEnvironment record);
}