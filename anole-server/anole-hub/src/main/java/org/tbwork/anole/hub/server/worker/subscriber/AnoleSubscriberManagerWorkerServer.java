package org.tbwork.anole.hub.server.worker.subscriber;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.tbwork.anole.hub.TimeEncoder;
import org.tbwork.anole.hub.TimeServerHandler;
import org.tbwork.anole.hub.server.AnoleServer;
import org.tbwork.anole.hub.server.worker.subscriber.handler.AuthenticationHandler;
import org.tbwork.anole.hub.server.worker.subscriber.handler.ExceptionHandler;
import org.tbwork.anole.hub.server.worker.subscriber.handler.MainLogicHandler;
import org.tbwork.anole.hub.server.worker.subscriber.handler.NewConnectionHandler;

import com.google.common.base.Preconditions;


/**
 * An subscriber server manages all the subscriber clients.
 * A subscriber client can establish a long connection 
 * with the AnolePushServer, and then continuously receive
 * messages pushed by the server.The management of clients is
 * maintained by the server which means the client do not
 * worry about the waste of connection caused by the network
 * problem or omitting to call "close()" method.
 * @author Tommy.Tang
 */ 
@Service("subscriberWorkerServer")
public class AnoleSubscriberManagerWorkerServer implements AnoleServer{

	volatile boolean started;
	static final Logger logger = LoggerFactory.getLogger(AnoleSubscriberManagerWorkerServer.class);
	Channel channel = null;
	EventLoopGroup bossGroup = null;
	EventLoopGroup workerGroup = null; 
	
	private int port; 
	
	@Autowired
	@Qualifier("w4sAuthenticationHandler")
	AuthenticationHandler authenticationHandler;
	
	@Autowired
	@Qualifier("w4sMainLogicHandler")
	MainLogicHandler mainLogicHandler;
	
	@Autowired
	@Qualifier("w4sNewConnectionHandler")
	NewConnectionHandler newConnectionHandler;
	
	@Autowired
	@Qualifier("w4sExceptionHandler")
	ExceptionHandler lowLevelExceptionHandler;
	
	@Override
	public void start(int port){
		if(!started) //DCL-1
		{
			synchronized(AnoleSubscriberManagerWorkerServer.class)
			{
				if(!started)//DCL-2
				{
					executeStart(port); 
				}
			}
		} 
	}
	
	@Override
	public void close()
	{
		if(started) //DCL-1
		{
			synchronized(AnoleSubscriberManagerWorkerServer.class)
			{
				if(started)//DCL-2
				{
					executeClose(); 
				}
			}
		} 
	}
	private void executeStart(int port){
		Preconditions.checkArgument(port>0, "port should be > 0");
		this.port = port;
		bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline().addLast(  
                    		 lowLevelExceptionHandler,
                    		 new ObjectEncoder(),
                    		 newConnectionHandler, 
                    		 new ObjectDecoder(ClassResolvers.cacheDisabled(null)), 
                    		 authenticationHandler, 
                    		 mainLogicHandler
                    		 );
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)      
             .childOption(ChannelOption.SO_KEEPALIVE, true); 

             // Bind and start to accept incoming connections. 
			 ChannelFuture f = b.bind(port).sync(); 
             if(f.isSuccess()){    
            	 channel = f.channel();
            	 logger.info("[:)] Anole worker server started succesfully at local address (port = {}) !", port);
            	 started = true;
             } 
        }catch(InterruptedException e){ 
        	logger.error("[:(] Anole worker server failed to start at port {}!", port);
			e.printStackTrace();
        }  
	}
	
	private void executeClose(){ 
		try {
			channel.disconnect();
			channel.close(); 
		} catch (Exception e) {
			logger.error("[:(] Anole worker server failed to close. Inner message: {}", e.getMessage());
			e.printStackTrace();
		}finally{ 
			if(!channel.isActive())
			{
				logger.info("[:)] Anole worker server closed successfully !");		
				workerGroup.shutdownGracefully();
		        bossGroup.shutdownGracefully();
				started = false;
			}
		} 
	} 
	
	@Override
	public int getPort() {
		return port;
	} 
	
}
