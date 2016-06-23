package org.tbwork.anole.hub.server.boss.authenticator.handler;

import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.c_2_s.CustomerAuthenticationMessage;
import org.tbwork.anole.common.message.c_2_s.C2SMessage;
import org.tbwork.anole.common.message.s_2_c.AuthFailAndCloseMessage;
import org.tbwork.anole.common.message.s_2_c.AuthPassWithTokenMessage;
import org.tbwork.anole.common.message.s_2_c.MatchFailAndCloseMessage;
import org.tbwork.anole.hub.server.boss.authenticator.validator.IClientUserValidateService;
import org.tbwork.anole.hub.server.lccmanager.ILongConnectionClientManager;
import org.tbwork.anole.hub.server.lccmanager.impl.SubscriberClientManager;
import org.tbwork.anole.hub.server.lccmanager.model.requests.RegisterRequest; 
import org.tbwork.anole.hub.server.lccmanager.model.requests.ValidateRequest;
import org.tbwork.anole.hub.server.lccmanager.model.requests.params.CustomerRegisterParameter;
import org.tbwork.anole.hub.server.lccmanager.model.response.RegisterResult;
import org.tbwork.anole.hub.server.util.ChannelHelper;
import org.tbwork.anole.hub.server.util.ClientInfoGenerator;
import org.tbwork.anole.hub.server.util.ClientInfoGenerator.ClientInfo;
import org.tbwork.anole.hub.server.util.ClientInfoGenerator.ClientType;
import org.tbwork.anole.hub.server.worker.subscriber.AnoleSubscriberServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;
import io.netty.channel.ChannelHandler.Sharable;

@Component
@Sharable
public class AuthenticationHandler extends SimpleChannelInboundHandler<C2SMessage> {
 
	static final Logger logger = LoggerFactory.getLogger(AuthenticationHandler.class);
	
	@Autowired
	private IClientUserValidateService clientUserValidateService;
	
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
		 if(MessageType.C2S_CUSTOMER_AUTH.equals(msgType))
		 {
			    CustomerAuthenticationMessage bodyMessage = (CustomerAuthenticationMessage) msg;
			    
			    if(clientUserValidateService.validateUser(bodyMessage.getUsername(), bodyMessage.getPassword())){
			    	
			    	//Choose a valid worker
			    	
			    	//Get login pair from the worker
			    	
			    	//return to the client.
			    	
			    } 
		 		else  
		 		{   
		 			// invalid connection trial, send AuthFailAndCloseMessage message and then close the connection immediately.
		 			AuthFailAndCloseMessage afcMsg = new AuthFailAndCloseMessage(); 
		 			ChannelHelper.sendAndClose(ctx, afcMsg);
		 		}
		 }
         ctx.fireChannelRead(msg);  
	}
 
}