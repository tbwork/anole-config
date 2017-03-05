package org.tbwork.anole.gui.domain.model.demand;

import org.tbwork.anole.common.ConfigType;

import lombok.Data;

@Data
public class ModifyConfigDemand extends BaseOperationDemand{
	private String key; 
	private String value; 
	private ConfigType configType;
	private String project;
	private String env;
	private String description;
}
