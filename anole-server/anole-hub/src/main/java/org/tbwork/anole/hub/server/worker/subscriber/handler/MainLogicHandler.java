package org.tbwork.anole.hub.server.worker.subscriber.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.tbwork.anole.loader.types.ConfigType;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.c_2_s.C2SMessage;
import org.tbwork.anole.common.message.c_2_s.subscriber._2_worker.C2WChangeNotifyAckMessage;
import org.tbwork.anole.common.message.c_2_s.subscriber._2_worker.GetConfigMessage;
import org.tbwork.anole.common.message.c_2_s.worker_2_boss.W2BChangeNotifyAckMessage;
import org.tbwork.anole.common.message.s_2_c.worker._2_subscriber.ReturnConfigMessage;
import org.tbwork.anole.hub.model.ConfigValueDO;
import org.tbwork.anole.hub.repository.ConfigRepository;
import org.tbwork.anole.hub.server.lccmanager.ILongConnectionClientManager;
import org.tbwork.anole.hub.server.lccmanager.impl.SubscriberClientManagerForWorker; 
import org.tbwork.anole.hub.server.lccmanager.model.requests.UnregisterRequest;
import org.tbwork.anole.hub.server.util.ChannelHelper;

import com.alibaba.fastjson.JSON;

import io.netty.channel.ChannelHandler.Sharable;
@Component("w4sMainLogicHandler")
@Sharable
public class MainLogicHandler  extends SimpleChannelInboundHandler<C2SMessage> {

	@Autowired
	@Qualifier("subscriberClientManager")
	private SubscriberClientManagerForWorker scm;
	
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
		 		scm.unregisterClient(new UnregisterRequest(clientId)); // remove from the registry
		 	} break;
		 	case C2S_GET_CONFIG:{
		 		ReturnConfigMessage rcm = processGetConfigMessage(msg);
		 		if(logger.isDebugEnabled())
		 			logger.debug("Configuration (key={}) is retrieved successfully, details: {}", rcm.getKey(), JSON.toJSONString(rcm)); 
		 		ChannelHelper.sendMessage(ctx, rcm); 
		 	} break;
		 	case C2S_CONFIG_CHANGE_NOTIFY_ACK_C_2_W:{
		 		C2WChangeNotifyAckMessage cnam = (C2WChangeNotifyAckMessage) msg;
		 		scm.ackChangeNotify(cnam.getClientId(), cnam.getKey(), cnam.getTimestamp());
		 	} break;
		 	case C2S_PING:{ 
		 		logger.debug("[:)] Ping request received successfully from the client ( clientId = {}).", clientId);
		 		scm.ackPing(clientId);
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
 		if(value!=null)
 			logger.info("Config (key={}, env={})'s value is {}", key, env, value); 
 		else
 			logger.info("Could not find value for the config (key={}, env={})", key, env);
		scm.setCaredKey(gcMsg.getClientId(), gcMsg.getKey());
 		return new ReturnConfigMessage(key, value, cvdo.getConfigType());  
	}
	
	
}
