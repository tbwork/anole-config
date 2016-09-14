package org.tbwork.anole.hub.client.worker.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.tbwork.anole.common.enums.ClientType;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.c_2_s.C2SMessage;
import org.tbwork.anole.common.message.c_2_s.worker_2_boss.RegisterResultMessage;
import org.tbwork.anole.common.message.s_2_c.PingAckMessage;
import org.tbwork.anole.common.message.s_2_c.boss._2_worker.RegisterClientMessage;
import org.tbwork.anole.common.message.s_2_c.worker._2_subscriber.ReturnConfigMessage;
import org.tbwork.anole.hub.StaticConfiguration;
import org.tbwork.anole.hub.client.ConnectionMonitor;
import org.tbwork.anole.hub.client.IAnoleWorkerClient;
import org.tbwork.anole.hub.client.WorkerClientConfig;
import org.tbwork.anole.hub.client.impl.LongConnectionMonitor;
import org.tbwork.anole.hub.client.worker.AnoleWorkerClient;
import org.tbwork.anole.hub.server.AnoleServer;
import org.tbwork.anole.hub.server.lccmanager.impl.SubscriberClientManagerForWorker;
import org.tbwork.anole.hub.server.lccmanager.impl.WorkerClientManagerForBoss;
import org.tbwork.anole.hub.server.lccmanager.model.requests.RegisterRequest;
import org.tbwork.anole.hub.server.lccmanager.model.response.RegisterResult;
import org.tbwork.anole.hub.server.util.ClientInfoGenerator;
import org.tbwork.anole.hub.server.util.ClientInfoGenerator.ClientInfo;
import org.tbwork.anole.hub.util.SystemUtil; 
 
@Sharable
public class OtherLogicHandler  extends SimpleChannelInboundHandler<Message>{

	public OtherLogicHandler(IAnoleWorkerClient workerClient, AnoleServer subscriberWorkerServer,  ConnectionMonitor lcMonitor, SubscriberClientManagerForWorker subscriberClientManagerForWorker){
		super(true);
		this.workerClient = workerClient;
		this.subscriberWorkerServer = subscriberWorkerServer; 
		this.lcMonitor = lcMonitor;
		this.subscriberClientManagerForWorker = subscriberClientManagerForWorker;
	}
	
	static Logger logger = LoggerFactory.getLogger(OtherLogicHandler.class);

	private ConnectionMonitor lcMonitor;
	
	private IAnoleWorkerClient workerClient; 
	  
	private AnoleServer subscriberWorkerServer;
	
	private SubscriberClientManagerForWorker subscriberClientManagerForWorker;
	
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Message msg)
			throws Exception { 
		 MessageType msgType = msg.getType(); 
		 switch(msgType)
		 {  
		 	case S2C_PING_ACK:{ 
		 		PingAckMessage paMsg = (PingAckMessage) msg; 
		 		processPingAckResponse(paMsg);
		 	} break;
		 	//register worker to the boss
		 	case S2C_REGISTER_CLIENT:{
		 		RegisterClientMessage rcm = (RegisterClientMessage) msg;
		 		ClientType clientType = rcm.getClientType();
		 		workerClient.sendMessage(generateRegisterResultMessage(clientType));
		 	} break; 
		 	default:{ 
		 	} break; 
		 }  
	} 
	
	
	private RegisterResultMessage generateRegisterResultMessage(ClientType clientType){
		RegisterResultMessage result = new RegisterResultMessage(); 
		RegisterRequest rRequest = new RegisterRequest();
		rRequest.setClientType(clientType);
		RegisterResult rResult = subscriberClientManagerForWorker.registerClient(rRequest); 
		result.setResultClientId(rResult.getClientId());;
		result.setResultToken(rResult.getToken());
		result.setResultPort(subscriberWorkerServer.getPort());
		result.setResultIp(SystemUtil.getLanIp());
		result.setResultClientType(clientType); 
		return result; 
	}
	
	private void processPingAckResponse(PingAckMessage paMsg){
		int interval = paMsg.getIntervalTime();
		if(interval > 0 && interval != WorkerClientConfig.PING_INTERVAL){
			WorkerClientConfig.PING_INTERVAL = interval ;
			WorkerClientConfig.PING_DELAY = interval;
			lcMonitor.restart();
			logger.info("Synchronize PING_INTERVAL with the server, new interval is set as {} ms", WorkerClientConfig.PING_INTERVAL);
		} 
		workerClient.ackPing();
	} 
	

}
