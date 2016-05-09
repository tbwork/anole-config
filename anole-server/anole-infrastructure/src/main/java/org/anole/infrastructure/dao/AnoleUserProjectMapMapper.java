package org.anole.infrastructure.dao;

import java.util.List;
import org.anole.infrastructure.model.AnoleUserProjectMap;

public interface AnoleUserProjectMapMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AnoleUserProjectMap record);

    AnoleUserProjectMap selectByPrimaryKey(Integer id);

    List<AnoleUserProjectMap> selectAll();

    int updateByPrimaryKey(AnoleUserProjectMap record);
}