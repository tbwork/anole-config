package org.anole.infrastructure.dao;

import java.util.List;
import org.anole.infrastructure.model.AnoleProject;

public interface AnoleProjectMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AnoleProject record);

    AnoleProject selectByPrimaryKey(Integer id);

    List<AnoleProject> selectAll();

    int updateByPrimaryKey(AnoleProject record);
}