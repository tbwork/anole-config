package org.tbwork.anole.common.message.c_2_s.worker_2_boss;

import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.c_2_s.C2SMessage;

public class WorkerAuthenticationMessage extends C2SMessage {

	 public WorkerAuthenticationMessage()
	 {
		 super(MessageType.C2S_WORKER_REGISTER);
	 } 
}
