package org.anole.infrastructure.dao;

import java.io.Serializable;
import java.util.List;
 

public interface MybatisBaseMapper<T, PK extends Serializable> { 

    int deleteByPrimaryKey(PK id);

	int insert(T record);

	T selectByPrimaryKey(PK id);

	List<T> selectAll();

	int updateByPrimaryKey(T record); 

}