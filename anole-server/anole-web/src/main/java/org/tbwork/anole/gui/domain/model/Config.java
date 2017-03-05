package org.tbwork.anole.gui.domain.model;

import java.util.Map;

import lombok.Data;

@Data
public class Config {

	private String project;
	private Integer type;
	private String desc;
	private Map<String, String> values;
	
}
