package org.tbwork.anole.common.message.s_2_c;

import lombok.Data;

import org.tbwork.anole.common.ConfigType;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.model.ConfigChangeDTO;
@Data
public class ConfigChangeNotifyMessage  extends Message{
	private ConfigChangeDTO configChangeDTO;
	public ConfigChangeNotifyMessage(){
		super(MessageType.S2C_CHANGE_NOTIFY);
	} 
}
