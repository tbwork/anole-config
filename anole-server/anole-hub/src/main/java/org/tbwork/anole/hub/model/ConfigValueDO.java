package org.tbwork.anole.hub.model;

import org.tbwork.anole.loader.types.ConfigType;

import lombok.Data;

@Data
public class ConfigValueDO { 
	private String key;
	private String env;
	private String value;
	private String lastOperator; 
	private ConfigType configType;
	
}
