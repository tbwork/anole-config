package org.tbwork.anole.gui.domain.model;

import java.util.Map;

import org.tbwork.anole.gui.domain.util.CommonTools;

import lombok.Data;

@Data
public class ConfigBrief {

	private String key;
	private int type;
	private String value;
	private String desc;
	private String env;
	private String lastModifier; 

	public ConfigBrief shiledValue(){ 
		this.setValue(CommonTools.NO_RIGHT_DESCRIPTION);  
		return this;
	} 
}
