package org.tbwork.anole.hub.model;

import org.tbwork.anole.common.ConfigType;

import lombok.Data;
@Data
public class ConfigDO { 
	private int id;
	private String key; 
	private ConfigType configType;
	private String description;  
	private String creator;
	private String lastOpeartor;
}
