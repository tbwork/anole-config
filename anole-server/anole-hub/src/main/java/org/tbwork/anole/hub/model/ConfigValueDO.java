package org.tbwork.anole.hub.model;

import lombok.Data;

@Data
public class ConfigValueDO { 
	private String key;
	private String env;
	private String value;
	private String lastOperator; 
	
}
