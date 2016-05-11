package org.tbwork.anole.hub.model;

import org.anole.infrastructure.model.AnoleConfig;
import org.tbwork.anole.common.ConfigType;
import org.tbwork.anole.hub.util.ProjectUtil;

import lombok.Data;
@Data
public class ConfigDO{  
	private String key; 
	private ConfigType configType;
	private String description;  
	private String creator;
	private String lastOpeartor;  
}
