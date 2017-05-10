package org.tbwork.anole.common.message.s_2_c.worker._2_subscriber;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.tbwork.anole.loader.types.ConfigType;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.model.ConfigModifyDTO;
import org.tbwork.anole.common.model.ValueChangeDTO;
@Data
public class W2SConfigChangeNotifyMessage  extends Message{
	private ValueChangeDTO valueChangeDTO;
	public W2SConfigChangeNotifyMessage(){
		super(MessageType.S2C_CONFIG_CHANGE_NOTIFY_W_2_C);
	} 
	public W2SConfigChangeNotifyMessage(ValueChangeDTO valueChangeDTO){
		super(MessageType.S2C_CONFIG_CHANGE_NOTIFY_W_2_C);
		this.valueChangeDTO = valueChangeDTO;
	} 
}
