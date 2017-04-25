package org.tbwork.anole.gui.domain.model;

import java.util.Map;

import org.tbwork.anole.gui.domain.util.CommonTools;

import lombok.Data;

@Data
public class ConfigExtended{

	private String project;
	private String key;
	private int type; 
	private String desc; 
	private Map<String,String> values;
	 
	public ConfigExtended shiledValue(String env){ 
		this.values.put(env, CommonTools.NO_RIGHT_DESCRIPTION);
		return this;
	}
	
	public ConfigExtended putValue(String env, String value){ 
		this.values.put(env, value);
		return this;
	}
}
