package org.tbwork.anole.publisher.client;

public enum AnoleClientConfig {

	BOSS_SERVER_ADDRESS("anole.client.remoteAddress","localhost"),
	BOSS_SERVER_PORT("anole.client.remoteAddress","localhost"),
	;
	private String configName;
	private Object defaultValue;
	public String configName(){return this.configName;}
	public Object defaultValue(){return this.defaultValue;}
	private AnoleClientConfig(String configName, Object defaultValue){
		this.configName = configName;
		this.defaultValue = defaultValue;
	}
	
}
