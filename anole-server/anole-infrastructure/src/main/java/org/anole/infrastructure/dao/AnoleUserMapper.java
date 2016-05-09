package org.anole.infrastructure.dao;

import java.util.List;
import org.anole.infrastructure.model.AnoleUser;

public interface AnoleUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AnoleUser record);

    AnoleUser selectByPrimaryKey(Integer id);

    List<AnoleUser> selectAll();

    int updateByPrimaryKey(AnoleUser record);
}