package org.tbwork.anole.hub;

import java.net.SocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class TimeServerHandler extends ChannelHandlerAdapter{

	
		@Override
	    public void connect(
	            ChannelHandlerContext ctx,
	            SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
			
			System.out.println("Connected...");
	        //ctx.connect(remoteAddress, localAddress, promise);
	    }
		
		
	    @Override
	    public void channelActive(final ChannelHandlerContext ctx) {
	    	System.out.println("Active...");
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
