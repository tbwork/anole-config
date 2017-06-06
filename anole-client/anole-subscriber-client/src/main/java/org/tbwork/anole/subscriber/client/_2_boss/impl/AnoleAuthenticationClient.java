package org.tbwork.anole.subscriber.client._2_boss.impl;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.net.ConnectException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;  
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.c_2_s.C2SMessage; 
import org.tbwork.anole.loader.core.Anole;
import org.tbwork.anole.subscriber.client._2_boss.IAnoleAuthenticationClient;
import org.tbwork.anole.subscriber.client._2_boss.handler.AuthenticationHandler;
import org.tbwork.anole.subscriber.client._2_boss.handler.ExceptionHandler;
import org.tbwork.anole.subscriber.client._2_boss.handler.OtherLogicHandler; 
import org.tbwork.anole.subscriber.exceptions.AuthenticationNotReadyException;
import org.tbwork.anole.subscriber.exceptions.SocketChannelNotReadyException;
import org.tbwork.anole.subscriber.util.GlobalConfig;

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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists; 
/** 
 * @author Tommy.Tang
 */ 
@Data
public class AnoleAuthenticationClient implements IAnoleAuthenticationClient{
 
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE) 
	private volatile boolean started; 
	private volatile boolean connected;
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE) 
	private static final Logger logger = LoggerFactory.getLogger(AnoleAuthenticationClient.class);
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE) 
	SocketChannel socketChannel = null;
    @Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE)
    private static final AnoleAuthenticationClient anoleAuthenticationClient = new AnoleAuthenticationClient();
 
    
    int clientId = 0; // assigned by the server
    int token = 0;    // assigned by the server 
     
    
    @Data
    public static class Servers{ 
    	private List<String> addresses;  
    }
    
    private Servers servers;

    public static final Object lock = new Object();
    public static volatile boolean authenticating = false;
    private AnoleAuthenticationClient(){
    	
    }
    
    public static AnoleAuthenticationClient instance(){
    	return anoleAuthenticationClient;
    } 
      
    private ExecutorService es = Executors.newSingleThreadExecutor();
    private ExecutorService es2 = Executors.newSingleThreadExecutor();
    
    
    //Properties
    public static enum ClientProperties{ 
    	BOSS_2_WOKRER_SERVER_ADDRESS("anole.client.subscriber.boss.address", "localhost:54323,localhost:54324"),  
    	;
    	private String name;
    	private String defaultValue; 
    	private ClientProperties(String name, String defaultValue){
    		this.name = name;
    		this.defaultValue = defaultValue;
    	} 
    } 
    
    
    @Override
	public void authenticate() {
		try{
			Future<Integer> future = es.submit(new Callable<Integer>() { 
				@Override
				public Integer call() throws Exception { 
					if(!authenticating){
			    		synchronized(lock){
			    			if(!authenticating){
			    				authenticating = true;
			    				asynConnect();
			    				while(authenticating)
			    					lock.wait(); 
			    			}
			    		} 	
			    	} 
					return 0; 
				} 
	    	});
	    	
	    	future.get(GlobalConfig.AUTHENTICATION_TIMEOUT_LIMIT, TimeUnit.MILLISECONDS);
		}
		catch(TimeoutException e){
			logger.error("Authentication is timeout. Please try later.", e.getMessage());
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error("Authentication failed due to a an inner exception. Details: {}", e.getMessage());
		}
		finally{
			synchronized(lock){
				authenticating = false;
				lock.notifyAll();
			} 
		}
    } 
    
    
    private void connect(){
    	if(!started || !connected) //DCL-1
  		{
  			synchronized(AnoleAuthenticationClient.class)
  			{
  				if(!started || !connected)//DCL-2
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
	
    private void asynConnect(){
    	es2.submit(new Callable<Integer>() { 
			@Override
			public Integer call() throws Exception {
				connect();
				return 0;
			}
		});
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
	
    private void initialized(){
    	String serversString = getProperty(ClientProperties.BOSS_2_WOKRER_SERVER_ADDRESS);
    	String [] _servers = serversString.split(",");
    	if(_servers.length < 2)
    		throw new RuntimeException("At least two boss server were specified for sake of robustness! If you obstinately want to use only one, just specify two same addresses.");
    	servers = new Servers();
    	servers.setAddresses(Lists.newArrayList(_servers));  
    }
	
  	private void executeConnect(){
  		if(servers == null){
  			initialized();
  		}  
  		for(String serverString : servers.getAddresses()){
  			if(executeConnect(serverString))
  				return ;
  		} 
  		logger.error("No available boss server! Please make sure at least one boss is running and reachable!");
  	//	throw new RuntimeException("No available boss server! Please make sure at least one boss is running and reachable!"); 
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
  		logger.info("Connecting to the boss server at {}:{}", host, port);
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
            	socketChannel = (SocketChannel) f.channel(); 
            	started = true;
            	connected = true;
            	logger.debug("[:)] Anole client successfully connected to the server with remote host = '{}' and port = {}", host, port);			            	
            	return true;
            }
            else
            	return false;
        }
        catch (InterruptedException e) {
        	logger.error("[:(] Anole client failed connect to the server with remote host = '{}' and port = ", host, port);
			e.printStackTrace();
			return false;
		}
        catch(Exception e){
        	logger.error("[:(] Connecting to the boss server ({}:{}) failed because of {}. Please check the boss server address and try again. ", host, port, e.getMessage());
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
				started = false;
				connected = false;
			}
		}
	}
 

	@Override
	public void saveToken(int clientId, int token) {
		 this.clientId = clientId;
		 this.token = token;
	}
	
	
	private String getProperty(ClientProperties clientProperties){
    	return Anole.getProperty(clientProperties.name, clientProperties.defaultValue);
	}
	    
    private int getIntProerty(ClientProperties clientProperties){
    	return Anole.getIntProperty(clientProperties.name, Integer.valueOf(clientProperties.defaultValue));
    }
	
}
