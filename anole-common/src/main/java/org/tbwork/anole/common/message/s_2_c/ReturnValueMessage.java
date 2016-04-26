package org.tbwork.anole.common.message.s_2_c;

import lombok.Data;

import org.tbwork.anole.common.ConfigType;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;

@Data
public class ReturnValueMessage extends Message{

	private String key;
	private String value;
	private ConfigType valueType;
	public ReturnValueMessage(){
		super(MessageType.S2C_RETURN_VALUE);
	} 
	public ReturnValueMessage(String key, String value, ConfigType valueType){ 
		this.key = key;
		this.value = value;
		this.valueType = valueType;
	}
	
}
