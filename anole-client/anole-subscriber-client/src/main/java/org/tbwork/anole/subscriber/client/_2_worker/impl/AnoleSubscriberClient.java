package org.tbwork.anole.subscriber.client._2_worker.impl;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;  
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.c_2_s.C2SMessage;
import org.tbwork.anole.subscriber.client._2_worker.IAnoleSubscriberClient;
import org.tbwork.anole.subscriber.client._2_boss.IAnoleAuthenticationClient;
import org.tbwork.anole.subscriber.client._2_boss.impl.AnoleAuthenticationClient;
import org.tbwork.anole.subscriber.client._2_worker.ConnectionMonitor; 
import org.tbwork.anole.subscriber.client._2_worker.handler.ConfigChangeNotifyMessageHandler;
import org.tbwork.anole.subscriber.client._2_worker.handler.ExceptionHandler;
import org.tbwork.anole.subscriber.client._2_worker.handler.OtherLogicHandler; 
import org.tbwork.anole.subscriber.exceptions.AuthenticationNotReadyException;
import org.tbwork.anole.subscriber.exceptions.SocketChannelNotReadyException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption; 
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory; 

import com.google.common.base.Preconditions; 
/** 
 * @author Tommy.Tang
 */ 
@Data
public class AnoleSubscriberClient implements IAnoleSubscriberClient{

	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE) 
	private volatile boolean started; 
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE) 
	private volatile boolean connected;
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE) 
	private static final Logger logger = LoggerFactory.getLogger(AnoleSubscriberClient.class);
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE) 
	SocketChannel socketChannel = null;
    @Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE)
    private static final AnoleSubscriberClient anoleSubscriberClient = new AnoleSubscriberClient();

    private static final ConnectionMonitor lcMonitor = LongConnectionMonitor.instance();
    
    String ip = null; // assigned by the server
    int port = 0; // assigned by the server
    int clientId = 0; // assigned by the server
    int token = 0;    // assigned by the server 
    private int ping_count = 0;
    private int MAX_PING_COUNT = 5;
    private static final IAnoleAuthenticationClient aac = AnoleAuthenticationClient.instance();
    
    private AnoleSubscriberClient(){}
    
    public static AnoleSubscriberClient instance(){
    	return anoleSubscriberClient;
    } 
    
    public void addPingCount(){
    	ping_count ++;
    }
    
    public void ackPing(){
    	ping_count --;
    }
    
    public boolean canPing(){
    	return ping_count <= MAX_PING_COUNT;
    }
    
    @Override
	public void connect() {
    	
    	aac.authenticate();
    	if(ip == null || port ==0 ){
    		throw new RuntimeException("There is no suitable worker server yet, please try again later!");
    	}
    	connectToWorker();
    }
	
    
    private void connectToWorker(){
    	if(!started || !connected) //DCL-1
		{
			synchronized(AnoleSubscriberClient.class)
			{
				if(!started || !connected)//DCL-2
				{ 
					boolean flag = started; 
					executeConnect(ip, port); 
					try {
						TimeUnit.SECONDS.sleep(2);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					if(!flag )
						lcMonitor.start();
				}
			}
		} 
    }
    
    @Override
	public void close(){
		if(!started) //DCL-1
		{
			synchronized(AnoleSubscriberClient.class)
			{
				if(!started)//DCL-2
				{
					lcMonitor.stop();
					executeClose(); 
				}
			}
		} 
		
	}
	
    @Override
	public void sendMessage(C2SMessage msg)
	{ 
		sendMessageWithFuture(msg);
	}
	
    @Override
	public void sendMessageWithListeners(C2SMessage msg, ChannelFutureListener ... listeners)
	{
		ChannelFuture f = sendMessageWithFuture(msg);
		for(ChannelFutureListener item : listeners)
		    f.addListener(item);  
	} 
	
	 
	private ChannelFuture sendMessageWithFuture(C2SMessage msg){ 
		if(socketChannel != null)
		{
			if(!MessageType.C2S_COMMON_AUTH.equals(msg.getType()))
				tagMessage(msg);
			return socketChannel.writeAndFlush(msg);
		}
		throw new SocketChannelNotReadyException();
	}
	
	/**
	 * Tag each message with current clientId and token before sending.
	 */
	private void tagMessage(C2SMessage msg){
		if(clientId == 0 && token == 0)
			throw new AuthenticationNotReadyException();
		msg.setClientId(clientId);
	    msg.setToken(token);
	}
	
	private void executeConnect(String host, int port)
	{ 
		Preconditions.checkNotNull (host  , "host should be null.");
		Preconditions.checkArgument(port>0, "port should be > 0"  );
        EventLoopGroup workerGroup = new NioEventLoopGroup(); 
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(
                    		new ExceptionHandler(),
                    		new ObjectEncoder(),
                   		    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),  
                    		ConfigChangeNotifyMessageHandler.instance(),
                    		new OtherLogicHandler()
                    		);
                }
            }); 
            // Start the client.
            ChannelFuture f = b.connect(host, port).sync();  
            if (f.isSuccess()) {
            	socketChannel = (SocketChannel)f.channel(); 
            	started = true;
            	connected = true;
            	logger.info("[:)] Anole subscriber successfully connected to the remote Anolo worker server with remote host = '{}' and port = {}", host, port);			            	
            } 
        }
        catch (InterruptedException e) {
        	logger.error("[:(] Anole subscriber failed to connect to the remote Anolo hub server with remote host = '{}' and port = ", host, port);
			e.printStackTrace();
		} 
	}
	
	private void executeClose()
	{
		try {
			clientId = 0; //reset
			token = 0;//reset
			socketChannel.closeFuture().sync();
			socketChannel = null;
		} catch (InterruptedException e) {
			logger.error("[:(] Anole client (clientId = {}) failed to close. Inner message: {}", clientId, e.getMessage());
			e.printStackTrace();
		}finally{
			if(!socketChannel.isActive())
			{
				logger.info("[:)] Anole client (clientId = {}) closed successfully !", clientId);			            	
				started = false;
				connected = false;
			}
		}
	}

	@Override
	public void reconnect() {
		 this.close();
		 this.connect();
	}

	@Override
	public void setWorkerServer(String ip, int port, int clientId, int token) {
		 this.clientId = clientId;
		 this.token    = token;
		 this.ip       = ip;
		 this.port     = port;
	}

	@Override
	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}
	
}
