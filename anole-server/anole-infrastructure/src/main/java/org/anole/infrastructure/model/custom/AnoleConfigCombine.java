package org.anole.infrastructure.model.custom;

public class AnoleConfigCombine { 
	private String key;
	private int type;
	private String project;
	private String value;
	private String description;
	private String envName;
	private String lastOperator;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEnvName() {
		return envName;
	}
	public void setEnvName(String envName) {
		this.envName = envName;
	}
	public String getLastOperator() {
		return lastOperator;
	}
	public void setLastOperator(String lastOperator) {
		this.lastOperator = lastOperator;
	} 
}
