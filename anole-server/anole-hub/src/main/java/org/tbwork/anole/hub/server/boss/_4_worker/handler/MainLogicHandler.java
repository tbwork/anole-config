package org.tbwork.anole.hub.server.boss._4_worker.handler;

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
import org.tbwork.anole.common.message.c_2_s.subscriber._2_worker.GetConfigMessage;
import org.tbwork.anole.common.message.c_2_s.worker_2_boss.RegisterResultMessage;
import org.tbwork.anole.common.message.c_2_s.worker_2_boss.W2BChangeNotifyAckMessage;
import org.tbwork.anole.common.message.c_2_s.worker_2_boss.WorkerPingMessage;
import org.tbwork.anole.common.message.s_2_c.worker._2_subscriber.ReturnConfigMessage;
import org.tbwork.anole.hub.model.ConfigValueDO;
import org.tbwork.anole.hub.repository.ConfigRepository;
import org.tbwork.anole.hub.server.lccmanager.ILongConnectionClientManager;
import org.tbwork.anole.hub.server.lccmanager.impl.SubscriberClientManagerForWorker;
import org.tbwork.anole.hub.server.lccmanager.impl.WorkerClientManagerForBoss;
import org.tbwork.anole.hub.server.lccmanager.model.requests.UnregisterRequest;
import org.tbwork.anole.hub.server.util.ChannelHelper;

import io.netty.channel.ChannelHandler.Sharable;
@Component("b4wMainLogicHandler")
@Sharable
public class MainLogicHandler  extends SimpleChannelInboundHandler<C2SMessage> {

	@Autowired
	@Qualifier("workerClientManager")
	private WorkerClientManagerForBoss wcm;
	
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
		 		wcm.unregisterClient(new UnregisterRequest(clientId)); // remove from the registry
		 	} break;
		 	case C2S_REGISTER_RESULT:{
		 		RegisterResultMessage rrm = (RegisterResultMessage)msg;
		 		wcm.setRegisterResult(rrm.getClientId(), rrm.getResultClientId(), rrm.getResultToken(), rrm.getResultIp(), rrm.getResultPort(), rrm.getResultClientType());
		 	} break;
		 	case C2S_CONFIG_CHANGE_NOTIFY_ACK_W_2_B:{
		 		W2BChangeNotifyAckMessage cnam = (W2BChangeNotifyAckMessage) msg;
		 		wcm.ackChangeNotify(cnam.getClientId(), cnam.getKey(), cnam.getTimestamp());
		 	} break;
		 	case C2S_PING:{ 
		 		logger.debug("[:)] Ping request received successfully from the client ( clientId = {}).", clientId); 
		 		WorkerPingMessage wpm = (WorkerPingMessage) msg;
		 		wcm.updateStatus(clientId, wpm.getSubscriberClientCount(), wpm.getWeight());
		 		wcm.ackPing(clientId); 
		 	} break;
		 	default:break; 
		 } 
	}
	
	
}
