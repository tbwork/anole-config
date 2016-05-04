package org.anole.infrastructure.dao;

import org.anole.infrastructure.model.AnoleUser;

public interface AnoleUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AnoleUser record);

    int insertSelective(AnoleUser record);

    AnoleUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AnoleUser record);

    int updateByPrimaryKey(AnoleUser record);
}