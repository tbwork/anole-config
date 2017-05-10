package org.tbwork.anole.common.model;

import java.io.Serializable;
import java.util.Set;

import org.tbwork.anole.loader.types.ConfigType;

import lombok.Data;

@Data
public class ValueChangeDTO implements Serializable{

	private String key; 
	private String value;
	private ConfigType configType;
	private String env;
	/**
	 * Used to identify the latest change which will be sent to the clients.
	 */
	private long timestamp;
	
	public ValueChangeDTO(ConfigModifyDTO cmd, String env){
		this.key = cmd.getKey();
		this.value = cmd.getValue();
		this.timestamp = cmd.getTimestamp();
		this.configType = cmd.getConfigType(); 
		this.env = env;
	}
}
