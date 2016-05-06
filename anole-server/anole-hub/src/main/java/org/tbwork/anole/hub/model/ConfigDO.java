package org.tbwork.anole.hub.model;

import org.tbwork.anole.common.ConfigType;

import lombok.Data;

/**
 * Mutable fields of ConfigDO:
 * <p> {@link #value}
 * <p> {@link #configType}
 * <p> {@link #description}
 * @author Tommy.Tang 
 */
@Data
public class ConfigDO { 
	private int id;
	private String key;
	//Mutable Parts
	private String value;
	private ConfigType configType;
	private String description;  
}
