package org.tbwork.anole.hub.server.util;

import org.tbwork.anole.common.message.Message;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class ChannelHelper {

	public static void sendAndClose(final ChannelHandlerContext ctx, Object msg)
	{ 
			final ChannelFuture f = ctx.writeAndFlush(msg);  
			f.addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) {
	                assert f == future;
	                ctx.close();
	            }
			}); 
	}
	
	public static void sendMessageSync(final ChannelHandlerContext ctx, Object msg)
	{ 
			final ChannelFuture f = ctx.writeAndFlush(msg);  
			f.addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) {
	                assert f == future;
	            }
			}); 
	}
	
	public static void sendMessage(final ChannelHandlerContext ctx, Object msg)
	{ 
		    ctx.writeAndFlush(msg);   
	}
	
	
	public static boolean checkValidity(Object msg){
		return msg instanceof Message;
	}
	
}
