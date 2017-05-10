package org.tbwork.anole.gui.domain.model.demand;

import org.tbwork.anole.loader.types.ConfigType;

import lombok.Data;

@Data
public class AddConfigDemand extends BaseOperationDemand{ 
	private String key; 
	private String destValue; 
	private Byte destConfigType;
	private String project;  
	private String description;
	private boolean allEnvEnabled;
	private String env;
	public void preCheck(){
		if(key == null || key.isEmpty())
			throw new RuntimeException("key is null or empty");
		if(destValue == null || destValue.isEmpty())
			throw new RuntimeException("destValue is null or empty");
		if(project == null || project.isEmpty())
			throw new RuntimeException("project is null or empty");
		if(destConfigType == null)
			throw new RuntimeException("destConfigType is null or empty");
		if(env == null || env.isEmpty())
			throw new RuntimeException("env is null or empty");
	}
}
