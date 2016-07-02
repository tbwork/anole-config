package org.tbwork.anole.hub.client.worker;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.c_2_s.C2SMessage;
import org.tbwork.anole.hub.client.AnoleClient;
import org.tbwork.anole.hub.client.ConnectionMonitor;
import org.tbwork.anole.hub.client.impl.LongConnectionMonitor;
import org.tbwork.anole.hub.client.worker.handler.AuthenticationHandler;
import org.tbwork.anole.hub.client.worker.handler.ExceptionHandler;
import org.tbwork.anole.hub.client.worker.handler.OtherLogicHandler;
import org.tbwork.anole.hub.exceptions.AuthenticationNotReadyException;
import org.tbwork.anole.hub.exceptions.SocketChannelNotReadyException;
import org.tbwork.anole.loader.core.AnoleLocalConfig;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

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
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Service("worker")
public class AnoleWorkerClient implements AnoleClient{ 
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE) 
	private volatile boolean started; 
	private volatile boolean connected;
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE) 
	private static final Logger logger = LoggerFactory.getLogger(AnoleWorkerClient.class);
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE) 
	SocketChannel socketChannel = null; 
	
    private static final ConnectionMonitor lcMonitor = LongConnectionMonitor.instance();
	
	int clientId = 0; // assigned by the server
    int token = 0;    // assigned by the server 
	
    Servers servers;
    
    @PostConstruct
    private void initialize(){
    	String serversString = getProperty(ClientProperties.BOSS_2_WOKRER_SERVER_ADDRESS);
    	String [] _servers = serversString.split(",");
    	if(_servers.length < 2)
    		throw new RuntimeException("At least two boss server were specified for sake of robustness! If you obstinately want to use only one, just specify two same addresses.");
    	servers.setAddresses(Lists.newArrayList(_servers)); 
    }
    //Properties
    public static enum ClientProperties{ 
    	BOSS_2_WOKRER_SERVER_ADDRESS("anole.client.worker.boss.address", "localhost:54321,localhost:54322"),  
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
    
    @Override
  	public void connect() {
  		if(!started || !connected) //DCL-1
  		{
  			synchronized(AnoleWorkerClient.class)
  			{
  				if(!started || !connected)//DCL-2
  				{ 
  					boolean flag = started; 
  					executeConnect(); 
  					try {
  						TimeUnit.SECONDS.sleep(AnoleLocalConfig.getIntProperty("anole.client.connect.delay", 2));
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
  			synchronized(AnoleWorkerClient.class)
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
  	
   
  	private void executeConnect(){
  		if(servers == null)
  			throw new RuntimeException("Boss servers are not ready!");  
  		for(String serverString : servers.getAddresses()){
  			if(executeConnect(serverString))
  				return ;
  		} 
  		throw new RuntimeException("No available boss server! Please make sure al least one boss is running and reachable!"); 
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
              	started = true;
              	connected = true;
                return true;
              }  
              return false;
          }
          catch (InterruptedException e) {  
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
  	public void reconnect() {
  		 this.close();
  		 this.connect();
  	}

  	@Override
  	public void saveToken(int clientId, int token) {
  		 this.clientId = clientId;
  		 this.token = token;
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
	 
	private String getProperty(ClientProperties clientProperties){
    	return AnoleLocalConfig.getProperty(clientProperties.name, clientProperties.defaultValue);
	}
	    
    private int getIntProerty(ClientProperties clientProperties){
    	return AnoleLocalConfig.getIntProperty(clientProperties.name, Integer.valueOf(clientProperties.defaultValue));
    }
	
}
