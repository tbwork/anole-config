package org.tbwork.anole.gui.domain.model.demand;

import lombok.Data;

@Data
public class BaseOperationDemand{ 
	private String operator; 
	public void preCheck(){
		if(operator == null || operator.isEmpty())
			throw new RuntimeException("operator is null or empty");
	}
}
