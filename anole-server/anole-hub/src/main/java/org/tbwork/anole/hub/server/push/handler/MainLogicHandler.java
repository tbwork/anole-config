package org.tbwork.anole.hub.server.push.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.tbwork.anole.common.ConfigType;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.c_2_s.C2SMessage;
import org.tbwork.anole.common.message.c_2_s.GetConfigMessage;
import org.tbwork.anole.common.message.s_2_c.ReturnConfigMessage;
import org.tbwork.anole.hub.model.ConfigValueDO;
import org.tbwork.anole.hub.repository.ConfigRepository; 
import org.tbwork.anole.hub.server.client.manager.BaseClientManager;
import org.tbwork.anole.hub.server.client.manager.impl.SubscriberClientManager;
import org.tbwork.anole.hub.server.client.manager.model.SubscriberUnregisterRequest;
import org.tbwork.anole.hub.server.util.ChannelHelper;

import io.netty.channel.ChannelHandler.Sharable;
@Component
@Sharable
public class MainLogicHandler  extends SimpleChannelInboundHandler<C2SMessage> {

	@Autowired
	@Qualifier("subscriberClientManager")
	private SubscriberClientManager cm;
	
	@Autowired
	private ConfigRepository cr;
	
	static final Logger logger = LoggerFactory.getLogger(MainLogicHandler.class);
	
	public MainLogicHandler(){
		super(true);
	}
	
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, C2SMessage msg)
			throws Exception { 
		 MessageType msgType = msg.getType();
		 int clientId = msg.getClientId();
		 int token = msg.getToken();
		 
		 switch(msgType)
		 {
		 	case C2S_EXIT_CLOSE:{ 
		 		logger.info("[:)] The client (address = {}) is closing...", ctx.channel().remoteAddress());
		 		cm.unregisterClient(new SubscriberUnregisterRequest(clientId)); // remove from the registry
		 	} break;
		 	case C2S_GET_CONFIG:{
		 		ChannelHelper.sendMessage(ctx, processGetConfigMessage(msg)); 
		 	} break;
		 	case C2S_PING:{ 
		 		logger.info("[:)] Ping request received successfully from the client ( clientId = {}).", clientId);
		 		cm.ackPing(clientId);
		 	} break;
		 	default:break; 
		 } 
	}
 
	private ReturnConfigMessage processGetConfigMessage(Message msg){ 
		GetConfigMessage gcMsg = (GetConfigMessage) msg;
 		String key = gcMsg.getKey();
 		String env = gcMsg.getEnv();
 		ConfigValueDO cvdo =   cr.retrieveConfigValueByKey(key, env);
 		String value = cvdo == null ? null : cvdo.getValue();
 		return new ReturnConfigMessage(key, value, ConfigType.STRING);  
	}
	
	
}
