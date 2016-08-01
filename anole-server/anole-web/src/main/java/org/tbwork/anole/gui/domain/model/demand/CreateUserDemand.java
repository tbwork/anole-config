package org.tbwork.anole.gui.domain.model.demand;

import lombok.Data;

@Data
public class CreateUserDemand extends BaseOperationDemand{ 
	private String username;
	private String password; 
}
