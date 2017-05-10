package org.tbwork.anole.common.model;

import lombok.Data;

import java.io.Serializable;

import org.tbwork.anole.loader.types.ConfigType;
   
@Data
public class ConfigModifyDTO implements Serializable{
	private String key; 
	private String value; 
	private ConfigType configType;
	private String project;
	private String env;
	private String description;
	/**
	 * Used to identify the latest change which will be sent to the clients.
	 */
	private long timestamp;
	private boolean createNew;
}
