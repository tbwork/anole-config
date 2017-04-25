package org.tbwork.anole.common.message.s_2_c.boss._2_worker;

import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.model.ConfigModifyDTO;
import org.tbwork.anole.common.model.ValueChangeDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class B2WConfigChangeNotifyMessage  extends Message{

	private ValueChangeDTO valueChangeDTO;
	public B2WConfigChangeNotifyMessage(){
		super(MessageType.S2C_CONFIG_CHANGE_NOTIFY_B_2_W);
	} 
	public B2WConfigChangeNotifyMessage(ValueChangeDTO valueChangeDTO){
		super(MessageType.S2C_CONFIG_CHANGE_NOTIFY_B_2_W);
		this.valueChangeDTO = valueChangeDTO;
	} 
}
