package org.tbwork.anole.subscriber.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType; 
import org.tbwork.anole.common.message.c_2_s.PingAckMessage;
import org.tbwork.anole.common.message.s_2_c.ReturnValueMessage;
import org.tbwork.anole.subscriber.client.AnoleSubscriberClient;

public class MainLogicHandler  extends SimpleChannelInboundHandler<Message>{

	@Autowired
	private  AnoleSubscriberClient anoleSubscriberClient;
	
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Message msg)
			throws Exception {
		 MessageType msgType = msg.getType(); 
		 switch(msgType)
		 {  
		 	case S2C_PING:{ 
		 		anoleSubscriberClient.sendMessage(new PingAckMessage());
		 	} break;
		 	case S2C_RETURN_VALUE:{ 
		 		ReturnValueMessage rvMsg = (ReturnValueMessage) msg;
		 		
		 	} break;
		 	  
		 	default:{
		 		
		 		
		 	} break; 
		 }  
	}
	
	private void readConfigMsg

}
