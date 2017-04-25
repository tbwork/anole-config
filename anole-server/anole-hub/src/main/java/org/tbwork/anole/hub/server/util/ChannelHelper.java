package org.tbwork.anole.hub.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.hub.server.lccmanager.model.clients.LongConnectionClientSkeleton;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class ChannelHelper {

	private static final Logger logger = LoggerFactory.getLogger(ChannelHelper.class);
	public static void sendAndClose(final ChannelHandlerContext ctx, Message msg)
	{ 
			final ChannelFuture f = writeAndFlush(ctx, msg);
			f.addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) {
					assert future.isSuccess();
	                ctx.close();
	            }
			}); 
	}
	
	public static String getIp(final LongConnectionClientSkeleton client){
		return client.getSocketChannel().remoteAddress().getAddress().getHostAddress();
	}
	
	public static void sendMessageSync(final ChannelHandlerContext ctx, Message msg)
	{ 
			final ChannelFuture f =  writeAndFlush(ctx, msg); 
			f.addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) {
					assert future.isSuccess();
	            }
			}); 
	}
	
	public static void sendMessage(final ChannelHandlerContext ctx, Message msg){ 
		writeAndFlush(ctx, msg);
	}
	 
	
	public static void sendMessageSync(final LongConnectionClientSkeleton lcc, Message msg)
	{ 
			final ChannelFuture f =  writeAndFlush(lcc, msg); 
			f.addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) {
	                assert future.isSuccess();
	            }
			}); 
	}
	
	public static void sendMessage(final LongConnectionClientSkeleton ctx, Message msg){ 
		writeAndFlush(ctx, msg);
	}

	
	public static boolean checkValidity(Object msg){
		return msg instanceof Message;
	}
	
	
	private static ChannelFuture writeAndFlush(final LongConnectionClientSkeleton sc, Message msg){
		try{
			if(sc != null && !sc.maxPromiseCount() && sc.getSocketChannel()!=null){ 
				ChannelFuture result =  sc.getSocketChannel().writeAndFlush(msg);
				if(logger.isDebugEnabled())
					logger.debug("New message of type {} is sent to '{}', content is {} ", msg.getType().toString(), sc.getSocketChannel().remoteAddress(), msg.toString());
				return result;
			} 
			else if( sc == null )
				logger.error("The subscriber client is not existed.");
			else if (sc.getSocketChannel() == null || sc.maxPromiseCount())
				logger.error("The subscriber client is already disconnected.");
		}catch (Exception e){
			 logger.error("Failed to send message to the subscriber client (ip = {}).", sc.getSocketChannel().remoteAddress());
		}
		return null; 
	}
	
    private static ChannelFuture writeAndFlush(final ChannelHandlerContext ctx, Message msg){
    	try{
			if(ctx != null){ 
				ChannelFuture result =  ctx.writeAndFlush(msg);
				if(logger.isDebugEnabled())
					logger.debug("New message of type {} is sent to '{}', content is {} ", msg.getType().toString(), ctx.channel().remoteAddress(), msg.toString());
				return result;
			} 
			else //if( ctx == null )
			    logger.error("The subscriber client is not existed."); 
		}catch (Exception e){
			 logger.error("Failed to send message to the subscriber client (ip = {}).", ctx.channel().remoteAddress());
		}
		return null; 
	}
}
