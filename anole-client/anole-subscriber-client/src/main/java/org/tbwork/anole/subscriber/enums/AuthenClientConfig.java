package org.tbwork.anole.subscriber.enums;

public enum AuthenClientConfig {
 
	BOSS_SERVER_ADDRESS("anole.client.remoteAddress","localhost"),
	BOSS_SERVER_PORT("anole.client.remoteAddress","localhost"),
	;
	private String configName;
	private Object defaultValue;
	public String configName(){return this.configName;}
	public Object defaultValue(){return this.defaultValue;}
	private AuthenClientConfig(String configName, Object defaultValue){
		this.configName = configName;
		this.defaultValue = defaultValue;
	}
}
