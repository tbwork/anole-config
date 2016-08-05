package org.tbwork.anole.hub.server.boss._4_publisher.handler;

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
import org.tbwork.anole.hub.server.lccmanager.impl.PublisherClientManagerForBoss;
import org.tbwork.anole.hub.server.lccmanager.impl.SubscriberClientManagerForWorker;
import org.tbwork.anole.hub.server.lccmanager.model.requests.RegisterParameter;
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

@Component("b4pAuthenticationHandler")
@Sharable
public class AuthenticationHandler extends SimpleChannelInboundHandler<C2SMessage> {

	@Autowired
	@Qualifier("publisherClientManager")
	private PublisherClientManagerForBoss pcm;

	static final Logger logger = LoggerFactory.getLogger(AuthenticationHandler.class);
	
	@Autowired
	private IUserService userService;
	
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
    	 
    	 // authentication information from client
		 if(MessageType.C2S_COMMON_AUTH.equals(msgType))
		 { 
			    CommonAuthenticationMessage bodyMessage = (CommonAuthenticationMessage) msg;
			    String username = bodyMessage.getUsername();
			    String password = bodyMessage.getPassword();
			    ClientType clientType = bodyMessage.getClientType(); 
			    if(logger.isDebugEnabled())
		 			logger.debug("A publish client ({}) is attempting to connect to the boss. Its ip is {}", clientType, ctx.channel().remoteAddress());
			    if(userService.verify(bodyMessage.getUsername(), bodyMessage.getPassword(), clientType)){   
			    	RegisterResult registerResult = pcm.registerClient(new RegisterRequest((SocketChannel)ctx.channel(), new RegisterParameter(), clientType));
			    	AuthPassWithTokenMessage authPassMessage = new AuthPassWithTokenMessage();
			    	authPassMessage.setClientId(registerResult.getClientId());
			    	authPassMessage.setToken(registerResult.getToken());
			    	ChannelHelper.sendMessage(ctx, authPassMessage);
			    }
		 		else{
		 			// invalid connection trial, send AuthFailAndCloseMessage message and then close the connection immediately.
		 			AuthFailAndCloseMessage afcMsg = new AuthFailAndCloseMessage(); 
		 			ChannelHelper.sendAndClose(ctx, afcMsg);
		 		} 
		 } 
		 else if(!pcm.validate(new ValidateRequest(message.getClientId(), message.getToken())))
	     {     // other message must need be validated (identification) before further process.
			   MatchFailAndCloseMessage mfcMsg = new MatchFailAndCloseMessage(); 
			   ChannelHelper.sendAndClose(ctx, mfcMsg);
	     }
	         
		 // Passed the identification validation, go on processing logical staff.
         ctx.fireChannelRead(msg);  
	}
 
}