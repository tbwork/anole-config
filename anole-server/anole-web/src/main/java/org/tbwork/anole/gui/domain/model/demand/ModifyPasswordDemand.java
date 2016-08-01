package org.tbwork.anole.gui.domain.model.demand;

import lombok.Data;

@Data
public class ModifyPasswordDemand extends BaseOperationDemand{
	private String username;
	private String password; 
}
