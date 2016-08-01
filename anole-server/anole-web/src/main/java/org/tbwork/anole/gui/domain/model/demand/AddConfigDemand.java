package org.tbwork.anole.gui.domain.model.demand;

import org.tbwork.anole.common.ConfigType;

import lombok.Data;

@Data
public class AddConfigDemand extends BaseOperationDemand{ 
	private String key; 
	private String destValue; 
	private ConfigType destConfigType;
	private String project; 
	private long timestamp;
}
