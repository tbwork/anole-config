package org.anole.infrastructure.dao;

import java.util.List;
import org.anole.infrastructure.model.AnoleHub;

public interface AnoleHubMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AnoleHub record);

    AnoleHub selectByPrimaryKey(Integer id);

    List<AnoleHub> selectAll();

    int updateByPrimaryKey(AnoleHub record);
}