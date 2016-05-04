package org.anole.infrastructure.dao;

import org.anole.infrastructure.model.AnoleUserProjectMap;

public interface AnoleUserProjectMapMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AnoleUserProjectMap record);

    int insertSelective(AnoleUserProjectMap record);

    AnoleUserProjectMap selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AnoleUserProjectMap record);

    int updateByPrimaryKey(AnoleUserProjectMap record);
}