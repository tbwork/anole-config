package org.tbwork.anole.hub.server.boss._4_subscriber.handler;
 
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Component;
import org.tbwork.anole.common.enums.ClientType;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.c_2_s.CommonAuthenticationMessage;
import org.tbwork.anole.common.message.c_2_s.subscriber._2_boss.S2BCommonAuthenticationMessage;
import org.tbwork.anole.common.message.c_2_s.C2SMessage;
import org.tbwork.anole.common.message.s_2_c.AuthFailAndCloseMessage;
import org.tbwork.anole.common.message.s_2_c.boss.AssignedWorkerInfoMessage;
import org.tbwork.anole.common.message.s_2_c.boss._2_worker.RegisterClientMessage;
import org.tbwork.anole.hub.StaticConfiguration;
import org.tbwork.anole.hub.server.lccmanager.impl.WorkerClientManagerForBoss;
import org.tbwork.anole.hub.server.lccmanager.model.clients.WorkerClientSkeleton;
import org.tbwork.anole.hub.server.lccmanager.model.clients.WorkerClientSkeleton.CustomerClient;
import org.tbwork.anole.hub.server.util.ChannelHelper;
import org.tbwork.anole.hub.services.IUserService;

import com.alibaba.fastjson.JSON;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler; 
import io.netty.channel.ChannelHandler.Sharable;

@Component("b4sAuthenticationHandler")
@Sharable
public class AuthenticationHandler extends SimpleChannelInboundHandler<C2SMessage> {
 
	static final Logger logger = LoggerFactory.getLogger(AuthenticationHandler.class);
	  
	@Autowired
	private IUserService userService;
	
	@Autowired
	private WorkerClientManagerForBoss wcm;
	
	public AuthenticationHandler(){
		super(false);
	}
	
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, C2SMessage msg)
			throws Exception { 
		 if(logger.isDebugEnabled())
		     logger.debug("New message received (type = {}, clientId = {})", msg.getType(), msg.getClientId());
		 C2SMessage message = msg;
    	 MessageType msgType = message.getType(); 
    	 
    	 // authentification information from client
		 if(MessageType.C2S_COMMON_AUTH.equals(msgType))
		 { 
			 	S2BCommonAuthenticationMessage bodyMessage = (S2BCommonAuthenticationMessage) msg; 
			    ClientType clientType = bodyMessage.getClientType(); 
			    if(logger.isDebugEnabled())
		 			logger.debug("A client ({}) is attempting to connect to the cluster. Its ip is {}", clientType, ctx.channel().remoteAddress());
			    if(clientType.equals(ClientType.SUBSCRIBER) && userService.verify(bodyMessage.getUsername(), bodyMessage.getPassword(), clientType))  {
			    	AssignedWorkerInfoMessage assignResult = assignWorker(clientType, bodyMessage.getEnvironment());
			    	if(logger.isDebugEnabled())
			    		logger.debug("New worker client is assigned, its content is: {}", JSON.toJSONString(assignResult));
			    	logger.info("A new work server ({}:{}) is assigned to the subscriber client", assignResult.getIp(), assignResult.getPort());
			    	ChannelHelper.sendAndClose(ctx, assignResult); 
			    }else{
		 			// invalid connection trial, send AuthFailAndCloseMessage message and then close the connection immediately.
		 			AuthFailAndCloseMessage afcMsg = new AuthFailAndCloseMessage(); 
		 			if(!clientType.equals(ClientType.SUBSCRIBER))
		 				afcMsg.setNote(String.format("The client's type is not valid. expect %s, but get %s ", ClientType.SUBSCRIBER, clientType));
		 			else
		 				afcMsg.setNote("User identification is failed.");
		 			ChannelHelper.sendAndClose(ctx, afcMsg);
		 		} 
		 }
         ctx.fireChannelRead(msg);  
	}
 
	private AssignedWorkerInfoMessage assignWorker(final ClientType clientType, final String environment){
		AssignedWorkerInfoMessage result = new AssignedWorkerInfoMessage();
		WorkerClientSkeleton wc = null; 
		if(clientType == ClientType.SUBSCRIBER) 
			wc = wcm.selectBestWorkerForSubscriber(); 
		if(wc == null){
			result.setMessage("There is no suitable worker server yet, please try again later.");
			return result;
		}
		if(environment == null || environment.isEmpty()){
			result.setMessage("Subsciber should have a runtime enviroment.");
			return result;
		}
		//remote register a new subscriber or publisher client.
		final WorkerClientSkeleton wcLock = wc;
		Future<CustomerClient> futureResult = wcm.executeThread(new Callable<CustomerClient>() { 
			@Override
			public CustomerClient call() throws Exception {
				synchronized(wcLock){
					RegisterClientMessage rcm = new RegisterClientMessage();
					rcm.setClientType(clientType);
					rcm.setEnviroment(environment);
					ChannelHelper.sendMessage(wcLock, rcm);
					wcLock.setProcessing(true); 
					wcLock.setGiveup(false);
					while(wcLock.isProcessing() && !wcLock.isGiveup())
						wcLock.wait(); 
					if(!wcLock.isProcessing()){ //finished
						return  wcLock.getSubscriber(); 
					}
					return null;
				} 
			}
		});
		try{    
			CustomerClient resultCc = futureResult.get(StaticConfiguration.WORKER_RESPONSE_TIMEOUT*5000, TimeUnit.SECONDS); 
			if(resultCc != null){
				result.setClientId(resultCc.getClientId());
				result.setToken(resultCc.getToken());
				result.setIp(resultCc.getLanIp());
				result.setPort(resultCc.getPort());
			} 
			return result;
		}
		catch(TimeoutException e){
			logger.error("Timeout when assigning worker for client (ClientType={}).", clientType);
			wcLock.setGiveup(true);
			wcLock.notifyAll();
			return result;
		}
		catch(Exception e){
			logger.error("Assign worker for client (ClientType={}) failed. Detailed information: {}", clientType, e.getMessage()); 
			wcLock.setGiveup(true);
			return result;
		}
		finally{
			synchronized(wcLock){
				wcLock.notifyAll();
			} 
		}
	}
 
 
}