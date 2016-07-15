package org.tbwork.anole.common.message.c_2_s.publisher_2_boss;

import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.c_2_s.C2SMessage;
import org.tbwork.anole.common.model.ConfigChangeDTO;

import lombok.Data;

@Data
public class ModifyConfigMessage extends C2SMessage {

	private String operator;
	private ConfigChangeDTO changeRule;
	public ModifyConfigMessage(String operator, ConfigChangeDTO changeRule){
		super(MessageType.C2S_MODIFY_CONFIG); 
		this.operator = operator;
		this.changeRule = changeRule;
	}
	
}
