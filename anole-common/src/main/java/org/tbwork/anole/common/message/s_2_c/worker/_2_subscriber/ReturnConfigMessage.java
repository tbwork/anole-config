package org.tbwork.anole.common.message.s_2_c.worker._2_subscriber;

import lombok.Data;

import org.tbwork.anole.loader.types.ConfigType;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;

@Data
public class ReturnConfigMessage extends Message{

	private String key;
	private String value;
	private ConfigType valueType;
	public ReturnConfigMessage(){
		super(MessageType.S2C_RETURN_CONFIG);
	} 
	public ReturnConfigMessage(String key, String value, ConfigType valueType){ 
		super(MessageType.S2C_RETURN_CONFIG);
		this.key = key;
		this.value = value;
		this.valueType = valueType;
	}
	
}
