package org.tbwork.anole.common.message.c_2_s.publisher_2_boss;

import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.c_2_s.C2SMessage;
import org.tbwork.anole.common.model.ConfigModifyDTO;

import lombok.Data;

@Data
public class ModifyConfigMessage extends C2SMessage {

	private String operator;
	private ConfigModifyDTO changeRule;
	public ModifyConfigMessage(String operator, ConfigModifyDTO changeRule){
		super(MessageType.C2S_MODIFY_CONFIG); 
		this.operator = operator;
		this.changeRule = changeRule;
	}
	
}
