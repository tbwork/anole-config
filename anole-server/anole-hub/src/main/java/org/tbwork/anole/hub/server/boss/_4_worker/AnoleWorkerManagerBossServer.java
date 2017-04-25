package org.tbwork.anole.hub.server.boss._4_worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.tbwork.anole.hub.server.AnoleServer;
import org.tbwork.anole.hub.server.boss._4_worker.handler.AuthenticationHandler;
import org.tbwork.anole.hub.server.boss._4_worker.handler.ExceptionHandler;
import org.tbwork.anole.hub.server.boss._4_worker.handler.MainLogicHandler;
import org.tbwork.anole.hub.server.boss._4_worker.handler.NewConnectionHandler;
import org.tbwork.anole.hub.server.worker.subscriber.AnoleSubscriberManagerWorkerServer;

import com.google.common.base.Preconditions;

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

/**
 * Anole worker-manager server manages all the workers.
 * When an Anole client attempts to connect to an Anole
 * worker server, it connects to the boss server first,
 * then the boss server arranges a worker server to it
 * by a response which contains essential information
 * of connecting a worker server. 
 * @author tommy.tang
 */
@Service
public class AnoleWorkerManagerBossServer implements AnoleServer{

	private int port;
	
	volatile boolean started;
	static final Logger logger = LoggerFactory.getLogger(AnoleWorkerManagerBossServer.class);
	Channel channel = null;
	EventLoopGroup bossGroup = null;
	EventLoopGroup workerGroup = null;  
	@Autowired
	@Qualifier("b4wAuthenticationHandler")
	AuthenticationHandler authenticationHandler;
	
	@Autowired
	@Qualifier("b4wMainLogicHandler")
	MainLogicHandler mainLogicHandler;
	
	@Autowired
	@Qualifier("b4wNewConnectionHandler")
	NewConnectionHandler newConnectionHandler;
	
	@Autowired
	@Qualifier("b4wExceptionHandler")
	ExceptionHandler lowLevelExceptionHandler;
	
	@Override
	public void start(int port) {
		if(!started) //DCL-1
		{
			synchronized(AnoleWorkerManagerBossServer.class)
			{
				if(!started)//DCL-2
				{
					executeStart(port); 
				}
			}
		} 
	}

	@Override
	public void close() {
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

	@Override
	public int getPort() {
		return port;
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
             if(f.isSuccess())
             {    
            	 channel = f.channel();
            	 logger.info("[:)] Anole boss server for worker started succesfully at local address (port = {}) !", port);
            	 started = true;
             }
			 
        }catch(InterruptedException e){ 
        	logger.error("[:(] Anole boss server for worker failed to start at port {}!", port);
			e.printStackTrace();
        }  
	}
	
	private void executeClose(){ 
		try {
			channel.disconnect();
			channel.close(); 
		} catch (Exception e) {
			logger.error("[:(] Anole boss server for worker failed to close. Inner message: {}", e.getMessage());
			e.printStackTrace();
		}finally{ 
			if(!channel.isActive())
			{
				logger.info("[:)] Anole boss server for worker closed successfully !");		
				workerGroup.shutdownGracefully();
		        bossGroup.shutdownGracefully();
				started = false;
			}
		} 
	}

}
