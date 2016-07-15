package org.tbwork.anole.hub.server.boss._4_publisher.handler;

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
import org.tbwork.anole.common.message.c_2_s.publisher_2_boss.ModifyConfigMessage;
import org.tbwork.anole.common.message.c_2_s.subscriber._2_worker.GetConfigMessage;
import org.tbwork.anole.common.message.s_2_c.boss._2_publisher.ModifyResultMessage;
import org.tbwork.anole.common.message.s_2_c.worker._2_subscriber.ReturnConfigMessage;
import org.tbwork.anole.common.model.ConfigChangeDTO;
import org.tbwork.anole.hub.model.ConfigValueDO;
import org.tbwork.anole.hub.repository.ConfigRepository;
import org.tbwork.anole.hub.server.lccmanager.ILongConnectionClientManager;
import org.tbwork.anole.hub.server.lccmanager.impl.PublisherClientManagerForBoss;
import org.tbwork.anole.hub.server.lccmanager.impl.SubscriberClientManagerForWorker; 
import org.tbwork.anole.hub.server.lccmanager.model.requests.UnregisterRequest;
import org.tbwork.anole.hub.server.util.ChannelHelper;

import io.netty.channel.ChannelHandler.Sharable;
@Component
@Sharable
public class MainLogicHandler  extends SimpleChannelInboundHandler<C2SMessage> {

	@Autowired
	@Qualifier("publisherClientManager")
	private PublisherClientManagerForBoss pcm;
	
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
		 		pcm.unregisterClient(new UnregisterRequest(clientId)); // remove from the registry
		 	} break;
		 	case C2S_MODIFY_CONFIG:{
		 		ChannelHelper.sendMessage(ctx, processModifyConfigMessage(msg)); 
		 	} break;
		 	case C2S_PING:{ 
		 		logger.info("[:)] Ping request received successfully from the client ( clientId = {}).", clientId);
		 		pcm.ackPing(clientId);
		 	} break;
		 	default:break; 
		 } 
	}
 
	private ModifyResultMessage processModifyConfigMessage(Message msg){ 
		ModifyResultMessage result = new ModifyResultMessage();
		ModifyConfigMessage mcMsg = (ModifyConfigMessage) msg;
 		String operator = mcMsg.getOperator(); 
 		ConfigChangeDTO changeRule = mcMsg.getChangeRule();  
 		return new ModifyResultMessage(pcm.motifyConfig(operator, changeRule));  
	}
	
	
}
