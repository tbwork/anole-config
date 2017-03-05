package org.tbwork.anole.gui.domain.model.demand;

import lombok.Data;

@Data
public class AuthenUserDemand{ 
	private String username;
	private String password; 
	private String project;
	private String env;
}
