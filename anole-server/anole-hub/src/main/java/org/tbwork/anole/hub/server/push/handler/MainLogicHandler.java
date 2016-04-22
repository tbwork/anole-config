package org.tbwork.anole.hub.server.push.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType; 
import org.tbwork.anole.hub.server.client.manager.BaseClientManager;
import org.tbwork.anole.hub.server.client.manager.model.SubscriberUnregisterRequest;

public class MainLogicHandler  extends SimpleChannelInboundHandler<Message> {

	@Autowired
	@Qualifier("subscriberClientManager")
	private BaseClientManager cm;
	
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Message msg)
			throws Exception {
		
		 MessageType msgType = msg.getType();
		 int clientId = msg.getClientId();
		 int token = msg.getToken();
		 
		 switch(msgType)
		 {
		 	case C2S_EXIT_CLOSE:{ 
		 		cm.unregisterClient(new SubscriberUnregisterRequest(clientId)); // remove from the registry
		 	} break;
		 	default:break; 
		 } 
	}

	
	
}
