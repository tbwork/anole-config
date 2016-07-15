package org.tbwork.anole.common.message.s_2_c.worker._2_subscriber;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.tbwork.anole.common.ConfigType;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.model.ConfigChangeDTO;
@Data
@AllArgsConstructor
public class W2CConfigChangeNotifyMessage  extends Message{
	private ConfigChangeDTO configChangeDTO;
	public W2CConfigChangeNotifyMessage(){
		super(MessageType.S2C_CONFIG_CHANGE_NOTIFY_W_2_C);
	} 
}
