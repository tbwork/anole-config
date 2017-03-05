package org.tbwork.anole.common.message.s_2_c.boss._2_publisher;

import org.tbwork.anole.common.enums.ClientType;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.s_2_c.boss._2_worker.RegisterClientMessage;
import org.tbwork.anole.common.model.ConfigModifyResultDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data 
public class ModifyResultMessage extends Message{

	 private ConfigModifyResultDTO changeResult;
	 
	 public ModifyResultMessage()
	 {
		 super(MessageType.S2C_MODIFY_RESULT);
	 }
	 
	 public ModifyResultMessage(ConfigModifyResultDTO configModifyResultDTO)
	 {
		 super(MessageType.S2C_MODIFY_RESULT);
		 changeResult = configModifyResultDTO;
	 }
	
}
