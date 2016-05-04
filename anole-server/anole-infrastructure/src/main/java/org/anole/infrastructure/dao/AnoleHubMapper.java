package org.anole.infrastructure.dao;

import org.anole.infrastructure.model.AnoleHub;

public interface AnoleHubMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AnoleHub record);

    int insertSelective(AnoleHub record);

    AnoleHub selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AnoleHub record);

    int updateByPrimaryKey(AnoleHub record);
}