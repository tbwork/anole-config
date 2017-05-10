package org.tbwork.anole.gui.domain.model.demand;

import org.tbwork.anole.loader.types.ConfigType;

import lombok.Data;

@Data
public class ModifyConfigDemand extends BaseOperationDemand{
	private String key; 
	private String value; 
	private Integer configType;
	private String project;
	private String env;
	private String description;
	private boolean allEnvEnabled; 
	
	public void preCheck(){
		if( key == null || key.isEmpty() )
			throw new RuntimeException("key is null or empty");
		if( value == null || value.isEmpty() )
			throw new RuntimeException("value is null or empty");
		if( project == null || project.isEmpty() )
			throw new RuntimeException("project is null or empty");
		if( configType == null )
			throw new RuntimeException("destConfigType is null or empty");
		if( env == null || env.isEmpty() )
			throw new RuntimeException("env is null or empty");
	}
}
