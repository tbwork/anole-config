package org.tbwork.anole.hub.model;

import org.tbwork.anole.common.ConfigType;

import lombok.Data;

@Data
public class ConfigDO {

	private String value;
	private ConfigType configType;
	
}
