package org.tbwork.anole.hub.server.boss._4_worker.handler;

import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tbwork.anole.common.enums.ClientType;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.c_2_s.CommonAuthenticationMessage; 
import org.tbwork.anole.common.message.c_2_s.C2SMessage;
import org.tbwork.anole.common.message.s_2_c.AuthFailAndCloseMessage;
import org.tbwork.anole.common.message.s_2_c.AuthPassWithTokenMessage;
import org.tbwork.anole.common.message.s_2_c.MatchFailAndCloseMessage;
import org.tbwork.anole.hub.server.lccmanager.ILongConnectionClientManager;
import org.tbwork.anole.hub.server.lccmanager.impl.SubscriberClientManagerForWorker;
import org.tbwork.anole.hub.server.lccmanager.impl.WorkerClientManagerForBoss;
import org.tbwork.anole.hub.server.lccmanager.model.requests.RegisterRequest; 
import org.tbwork.anole.hub.server.lccmanager.model.requests.ValidateRequest;
import org.tbwork.anole.hub.server.lccmanager.model.response.RegisterResult;
import org.tbwork.anole.hub.server.util.ChannelHelper;
import org.tbwork.anole.hub.server.util.ClientInfoGenerator;
import org.tbwork.anole.hub.server.util.ClientInfoGenerator.ClientInfo; 
import org.tbwork.anole.hub.server.worker.subscriber.AnoleSubscriberManagerWorkerServer;
import org.tbwork.anole.hub.services.IUserService;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;
import io.netty.channel.ChannelHandler.Sharable;

@Component("b4wAuthenticationHandler")
@Sharable
public class AuthenticationHandler extends SimpleChannelInboundHandler<C2SMessage> {

	@Autowired
	@Qualifier("workerClientManager")
	private WorkerClientManagerForBoss wcm;

	
	@Autowired
	private IUserService userService;
	
	static final Logger logger = LoggerFactory.getLogger(AuthenticationHandler.class);
	
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
		 if(MessageType.C2S_COMMON_AUTH.equals(msgType)) // register worker
		 {
			 	CommonAuthenticationMessage bodyMessage = (CommonAuthenticationMessage) msg;
			 	String username = bodyMessage.getUsername();
			    String password = bodyMessage.getPassword();
			    ClientType clientType = ClientType.WORKER;
			    if(logger.isDebugEnabled())
		 			logger.debug("A worker is attempting to join the cluster. Its ip is {}", ctx.channel().remoteAddress());
				if(userService.verify(username, password, clientType)){
					  RegisterRequest registerRequest = new RegisterRequest((SocketChannel)ctx.channel(), null, clientType);
				      RegisterResult registerResult =  wcm.registerClient(registerRequest);
				      // send back the clientId and the access token
		 			  AuthPassWithTokenMessage aptMsg = new AuthPassWithTokenMessage(); 
		 			  aptMsg.setClientId(registerResult.getClientId());
		 			  aptMsg.setToken(registerResult.getToken());
		 			  ChannelHelper.sendMessage(ctx, aptMsg);   
			 		  logger.info("A new worker is attempting to join the cluster. Its ip is {}", ctx.channel().remoteAddress());
		 			  // Releasing the message which means no further process.
		 			  ReferenceCountUtil.release(msg);
				} 
		 		else  
		 		{   
		 			  // invalid connection trial, send AuthFailAndCloseMessage message and then close the connection immediately.
		 			  AuthFailAndCloseMessage afcMsg = new AuthFailAndCloseMessage(); 
		 			  ChannelHelper.sendAndClose(ctx, afcMsg);
		 		}
		 }
		 else 
		 {	
			    // other message must need be validated (identification) before further process.
			    if(!wcm.validate(new ValidateRequest(message.getClientId(), message.getToken())))
			    {
				      MatchFailAndCloseMessage mfcMsg = new MatchFailAndCloseMessage(); 
				      ChannelHelper.sendAndClose(ctx, mfcMsg);
			    }
		 }
		
		 // Passed the identification validation, go on processing logical staff.
         ctx.fireChannelRead(msg);  
	} 

}