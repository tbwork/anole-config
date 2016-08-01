package org.tbwork.anole.gui.domain.model.demand;

import lombok.Data;

@Data
public class DeleteConfigDemand extends BaseOperationDemand{

	private String project;
	private String key;
	
}
