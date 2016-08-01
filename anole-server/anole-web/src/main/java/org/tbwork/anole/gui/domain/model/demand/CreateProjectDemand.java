package org.tbwork.anole.gui.domain.model.demand;

import lombok.Data;

@Data
public class CreateProjectDemand extends BaseOperationDemand{ 
	private String productLineName;
	private String projectName;
}
