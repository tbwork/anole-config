package org.anole.infrastructure.dao;

import java.util.List;
import org.anole.infrastructure.model.AnoleEnvironment;

public interface AnoleEnvironmentMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AnoleEnvironment record);

    AnoleEnvironment selectByPrimaryKey(Integer id);

    List<AnoleEnvironment> selectAll();

    int updateByPrimaryKey(AnoleEnvironment record);
}