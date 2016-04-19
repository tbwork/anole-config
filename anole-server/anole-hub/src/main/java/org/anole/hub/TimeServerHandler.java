package org.anole.hub;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeServerHandler extends ChannelHandlerAdapter{

	    @Override
	    public void channelActive(final ChannelHandlerContext ctx) {
	        final ByteBuf time = ctx.alloc().buffer(4); 
	        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));

	        final ChannelFuture f = ctx.writeAndFlush(time); 
	        f.addListener(new ChannelFutureListener() {
	            public void operationComplete(ChannelFuture future) {
	                assert f == future;
	                ctx.close();
	            }
	        }); 
	    }

	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
	        cause.printStackTrace();
	        ctx.close();
	    }
	
}
