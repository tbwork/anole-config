package org.tbwork.anole.publisher.client.impl;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.c_2_s.C2SMessage; 
import org.tbwork.anole.loader.core.Anole;
import org.tbwork.anole.publisher.client.AnoleClientConfig;
import org.tbwork.anole.publisher.client.ConnectionMonitor;
import org.tbwork.anole.publisher.client.IAnolePublisherClient;
import org.tbwork.anole.publisher.client.handler.AuthenticationHandler;
import org.tbwork.anole.publisher.client.handler.ExceptionHandler;
import org.tbwork.anole.publisher.client.handler.OtherLogicHandler;
import org.tbwork.anole.publisher.core.AnolePublisher;
import org.tbwork.anole.publisher.exceptions.AuthenticationNotReadyException;
import org.tbwork.anole.publisher.exceptions.SocketChannelNotReadyException;

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

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists; 
/** 
 * @author Tommy.Tang
 */  
@Data
public class AnolePublisherClient implements IAnolePublisherClient{

	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE) 
	private static final Logger logger = LoggerFactory.getLogger(AnolePublisherClient.class);
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE) 
	SocketChannel socketChannel = null;
    @Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE)
    private static final AnolePublisherClient publisher = new AnolePublisherClient();
 
    public static ExecutorService executor = Executors.newSingleThreadExecutor();
	
    
    
    
    int clientId = 0; // assigned by the server
    int token = 0;    // assigned by the server 
    
    private ConnectionMonitor lcMonitor = LongConnectionMonitor.instance();
    
    private Servers servers = new Servers();
    
    /**
     * Whether the publisher connected to the anole boss server.
     */
    private Boolean connected = false; 
    @Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE)
    private volatile Boolean connectOver = false;
    
    private final Object connectProcedureLock = new Object();
    private void initialize(){
    	String serversString = getProperty(ClientProperties.BOSS_2_PUBLISHER_SERVER_ADDRESS);
    	String [] _servers = serversString.split(",");
    	if(_servers.length < 2)
    		throw new RuntimeException("At least two boss server were specified for sake of robustness! If you obstinately want to use only one, just specify two same addresses.");
    	servers.setAddresses(Lists.newArrayList(_servers)); 
    }
    
    /**
     * Used to detect disconnection
     */
    private int ping_count = 0;
    /**
     * Used to detect disconnection
     */
    private int MAX_PING_COUNT = 5;
    
    private AnolePublisherClient(){initialize();}
    
    //Properties
    public static enum ClientProperties{ 
    	BOSS_2_PUBLISHER_SERVER_ADDRESS("anole.client.publisher.boss.address", "localhost:54321,localhost:54322"),  
    	;
    	private String name;
    	private String defaultValue;
    	
    	private ClientProperties(String name, String defaultValue){
    		this.name = name;
    		this.defaultValue = defaultValue;
    	}
    	
    }
    
    @Data
    public static class Servers{ 
    	private List<String> addresses;  
    }
    
    
    
    public static AnolePublisherClient instance(){
    	return publisher;
    } 
    
    @Override
	public void connect() {
    	if(!connected) 
  		{
  			synchronized(AnolePublisherClient.class)
  			{
  				if(!connected)
  				{  
  					executeConnect(); 
  					try {
  						TimeUnit.SECONDS.sleep(2);
  					} catch (InterruptedException e) { 
  						e.printStackTrace();
  					}
  				}
  			}
  		} 
    }
	
    
    
    @Override
	public void close(){
		if(!connected)
		{
			synchronized(AnolePublisherClient.class)
			{
				if(!connected)
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
			logger.info("Send msg {} to {}:{}", JSON.toJSONString(msg,true), socketChannel.remoteAddress().getHostName(), socketChannel.remoteAddress().getPort());
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
	
	
	private void executeConnect(){
  		if(servers == null)
  			throw new RuntimeException("Boss servers are not ready!");  
  		for(String serverString : servers.getAddresses()){
  			logger.info("Now connecting to the boss server at {} ...", serverString);
  			if(executeConnect(serverString))
  				return ;  
  		} 
  		throw new RuntimeException("No available boss server! Please make sure at least one boss is running and reachable!"); 
  	}
  	
  	/** 
  	 * @param address in the form of "ip:port"
  	 */
  	private boolean executeConnect(String address)
  	{ 
  		Preconditions.checkNotNull (address, "address should be null.");
  		Preconditions.checkArgument(address.contains(":"), "address should be in form of: ip:port.");
  		String [] ip_port = address.split(":");
  		boolean result  = executeConnect(ip_port[0], Integer.valueOf(ip_port[1]));
  		if(result)
  			logger.info("[:)] Connect to server ({}) successfully!", address);
  		else
  			logger.warn("[:(] Connect to server ({}) failed!", address);
  		return result;
  	}
  	
	
	private boolean executeConnect(String host, int port)
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
                    		new AuthenticationHandler(),
                    		new OtherLogicHandler()
                    		);
                }
            }); 
            // Start the client.
            ChannelFuture f = b.connect(host, port).sync();  
            if (f.isSuccess()) {
            	socketChannel = (SocketChannel)f.channel();   
            	Future<Void> connectResult = executor.submit(new Callable<Void>() { 
					@Override
					public Void call() throws Exception { 
						synchronized(connectProcedureLock){
							connectOver = false;
							while(!connectOver){
								connectProcedureLock.wait();
							}
							logger.debug("connected lock is released, current connected status is {}", connected);
		            	} 
						return null;
					} 
				});
            	Integer expireTime = AnolePublisher.getIntProperty("anole.publisher.startup.connection.timeout", 5);
            	Void result = null;
            	try{
            		connectResult.get(expireTime, TimeUnit.SECONDS);
            		if(connected){
            			return true;
            		}
            		return false;
            	}
            	catch(TimeoutException e ){
            		logger.warn("[:(] Timeout when Anole client connect to the remote Anolo hub server ({}:{}).", host, port, e);
            		connectOver = true;
            		socketChannel.closeFuture();
            		socketChannel = null;
            		return false;
            	} 
        	    catch (Exception e){
                  	logger.warn("[:(] Anole client failed to connect to the remote Anolo hub server ({}:{}) due to {}", host, port, e.getMessage(), e);
                  	connectOver = true;
                  	socketChannel.closeFuture();
            		socketChannel = null;
                  	return false;
        	    }
            	finally{
            		synchronized(connectProcedureLock){
            			connectProcedureLock.notifyAll();
	            	}  
            	}  
            } 
        	return false;
        }
        catch (InterruptedException e) {
        	logger.error("[:(] Anole client failed to connect to the remote Anolo hub server with remote host = '{}' and port = ", host, port);
			e.printStackTrace();
			return false;
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
	public void saveToken(int clientId, int token) {
		 this.clientId = clientId;
		 this.token = token;
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
	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	@Override
	public Boolean isConnected() {
		return connected;
	}
	
	 
	private String getProperty(ClientProperties clientProperties){
    	return Anole.getProperty(clientProperties.name, clientProperties.defaultValue);
	}
	    
    private int getIntProerty(ClientProperties clientProperties){
    	return Anole.getIntProperty(clientProperties.name, Integer.valueOf(clientProperties.defaultValue));
    }

	@Override
	public void notifyConnectOver(boolean connected) {
		 synchronized(this.connectProcedureLock){
			 this.connected = connected;
			 this.connectOver = true; 
			 this.connectProcedureLock.notifyAll(); 
		 }
		
	}
		
}
