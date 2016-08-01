package org.tbwork.anole.gui.domain.model.demand;

import org.tbwork.anole.common.ConfigType;

import lombok.Data;

@Data
public class ModifyConfigDemand extends BaseOperationDemand{

	private String key;
	private String origValue;
	private String destValue;
	private ConfigType oriConfigType;
	private ConfigType destConfigType;
	private String project;
	private String env;
	private long timestamp;
	
}
