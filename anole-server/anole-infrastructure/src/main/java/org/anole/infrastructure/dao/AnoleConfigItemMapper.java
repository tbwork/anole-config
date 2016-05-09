package org.anole.infrastructure.dao;

import java.util.List;
import org.anole.infrastructure.model.AnoleConfigItem;

public interface AnoleConfigItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AnoleConfigItem record);

    AnoleConfigItem selectByPrimaryKey(Integer id);

    List<AnoleConfigItem> selectAll();

    int updateByPrimaryKey(AnoleConfigItem record);
}