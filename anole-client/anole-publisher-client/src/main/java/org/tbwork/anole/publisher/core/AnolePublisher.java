package org.tbwork.anole.publisher.core;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.common.message.c_2_s.publisher_2_boss.ModifyConfigMessage;
import org.tbwork.anole.common.model.ConfigModifyResultDTO;
import org.tbwork.anole.loader.core.AnoleLocalConfig;
import org.tbwork.anole.publisher.client.IAnolePublisherClient;
import org.tbwork.anole.publisher.client.StaticClientConfig;
import org.tbwork.anole.publisher.client.impl.AnolePublisherClient;
import org.tbwork.anole.publisher.model.ConfigChangeRequest;
import org.tbwork.anole.publisher.model.ConfigChangeResponse;

import com.google.common.base.Preconditions;

/**
 * Used in the web GUI.
 * @author tommy.tang
 */
public class AnolePublisher extends AnoleLocalConfig{
	
	private static final IAnolePublisherClient client = AnolePublisherClient.instance();
	
	private static final Logger logger = LoggerFactory.getLogger(AnolePublisher.class);
	
	public static Object writeLock = new Object();
	public static volatile boolean writing;
	public static volatile ConfigModifyResultDTO operationResult;
	public static ExecutorService executor = Executors.newSingleThreadExecutor();
	
	public static ConfigChangeResponse edit(ConfigChangeRequest ccr){
		Preconditions.checkArgument(ccr.getOperator()!=null && !ccr.getOperator().isEmpty(), "Operator must be specified.");
		Preconditions.checkArgument(ccr.getConfigChangeDTO()!=null, "Configuration change object must be specified.");
		Preconditions.checkNotNull(ccr.getConfigChangeDTO().getOriConfigType(),"Original type must be specified.");
		Preconditions.checkNotNull(ccr.getConfigChangeDTO().getOrigValue(),"Original value must be specified.");
		return modify(ccr);
	}
	
	public static ConfigChangeResponse add(ConfigChangeRequest ccr){ 
		Preconditions.checkArgument(ccr.getOperator()!=null && !ccr.getOperator().isEmpty(), "Operator must be specified.");
		Preconditions.checkArgument(ccr.getConfigChangeDTO()!=null, "Configuration change object must be specified.");
		
		return modify(ccr);
	}
	
	private static ConfigChangeResponse modify(ConfigChangeRequest ccr){ 
		ConfigChangeResponse result = new ConfigChangeResponse(); 
		final ModifyConfigMessage mcm = new ModifyConfigMessage(ccr.getOperator(), ccr.getConfigChangeDTO());  
		try { 
			Future<ConfigChangeResponse> future = executor.submit(new Callable<ConfigChangeResponse>() { 
				@Override
				public ConfigChangeResponse call() throws Exception { 
					ConfigChangeResponse tempResult = new ConfigChangeResponse();
					if(!writing){
						synchronized(writeLock){
							if(!writing){
								operationResult = null;
								writing = true;
								client.sendMessage(mcm);
								while(writing) 
									writeLock.wait();  
								if(operationResult != null){
									tempResult.setSuccess(operationResult.isSuccess());
									tempResult.setErrorMessage(operationResult.getErrorMsg()); 
								}
								else{
									tempResult.setSuccess(false);
									tempResult.setErrorMessage("Operation result is not received."); 
								} 
								return tempResult;
							}
						}
					}  
					tempResult.setSuccess(false);
					tempResult.setErrorMessage("There is another operation executing. Please try again later.");
					return tempResult;
				} 
			});   
			return future.get(StaticClientConfig.WRITE_OPERATION_TIMEOUT_LIMIT, TimeUnit.SECONDS);  
		} catch(TimeoutException e){
			logger.error("Timeout Exception, {}", e.getMessage());
			result.setSuccess(false);
			result.setErrorMessage("Operation timeout! Please try again.");
		} catch (Exception e) {
			logger.error("Java Exception, {}", e.getMessage());
			result.setSuccess(false);
			result.setErrorMessage("Inner Java Exception: "+ e.getMessage());
		} finally{
			writeLock.notifyAll();
		}
		return result;
	}
}
