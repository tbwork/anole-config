package org.tbwork.anole.gui.domain.model.demand;

import lombok.Data;

@Data
public class AssignPermissionDemand extends BaseOperationDemand{

	private String username;
	private String projectName;
	private String env;
	private String role;
	
}
