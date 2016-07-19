package org.tbwork.anole.common.model;

import lombok.Data;

import org.tbwork.anole.common.ConfigType;


@Data
public class ConfigModifyDTO {

	private String key;
	private String origValue;
	private String destValue;
	private ConfigType oriConfigType;
	private ConfigType destConfigType;
	private String project;
	private String env;
	private long timestamp;
	
}
