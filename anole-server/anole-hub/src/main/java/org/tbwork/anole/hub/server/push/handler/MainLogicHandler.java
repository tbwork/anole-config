package org.tbwork.anole.hub.server.push.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.tbwork.anole.common.ConfigType;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType; 
import org.tbwork.anole.common.message.c_2_s.GetConfigMessage;
import org.tbwork.anole.common.message.s_2_c.ReturnValueMessage;
import org.tbwork.anole.hub.server.client.manager.BaseClientManager;
import org.tbwork.anole.hub.server.client.manager.impl.SubscriberClientManager;
import org.tbwork.anole.hub.server.client.manager.model.SubscriberUnregisterRequest;
import org.tbwork.anole.hub.server.repository.DataService;

public class MainLogicHandler  extends SimpleChannelInboundHandler<Message> {

	@Autowired
	@Qualifier("subscriberClientManager")
	private SubscriberClientManager cm;
	
	public MainLogicHandler(boolean autoRelease){
		super(autoRelease);
	}
	
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
		 	case C2S_GET_CONFIG:{ 
		 		GetConfigMessage gcMsg = (GetConfigMessage) msg;
		 		String key = gcMsg.getKey();
		 		String value = DataService.getProperty(key);
		 		ReturnValueMessage rvMsg = new ReturnValueMessage(key,value, ConfigType.STRING);
		 		ctx.writeAndFlush(rvMsg);
		 	} break;
		 	case C2S_PING_ACK:{ 
		 		cm.pincAck(clientId);
		 	} break;
		 	default:break; 
		 } 
	}
 
	
	
}
