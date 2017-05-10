package org.tbwork.anole.publisher;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.tbwork.anole.loader.types.ConfigType;
import org.tbwork.anole.common.model.ConfigModifyDTO;
import org.tbwork.anole.loader.core.AnoleLoader;
import org.tbwork.anole.loader.core.impl.AnoleClasspathLoader;
import org.tbwork.anole.publisher.client.IAnolePublisherClient;
import org.tbwork.anole.publisher.client.impl.AnolePublisherClient;
import org.tbwork.anole.publisher.core.AnolePublisher;
import org.tbwork.anole.publisher.model.ConfigChangeRequest;
import org.tbwork.anole.publisher.model.ConfigChangeResponse;

import com.alibaba.fastjson.JSON; 
/**
 * Hello world!
 *
 */
public class PublisherStart 
{
    public static void main( String[] args ) throws InterruptedException
    { 
    	AnoleLoader anoleLoader = new AnoleClasspathLoader();
    	anoleLoader.load();  
    	IAnolePublisherClient apc = AnolePublisherClient.instance();
  	    apc.connect();
  	    Scanner scan = new Scanner(System.in);
  	    while(true){
  	    	int a = scan.nextInt();
  	    	if(a == 0)
  	    		break;
  	    	System.out.println("Set config ");
  	  	    ConfigChangeRequest request = new ConfigChangeRequest();
  	  	    request.setOperator("admin");
  	  	    ConfigModifyDTO cc = new ConfigModifyDTO();
  	  	    cc.setConfigType(ConfigType.STRING);
  	  	    cc.setEnv("test");
  	  	    cc.setKey("key1");
  	  	    cc.setProject("order-main-service");
  	  	    cc.setTimestamp(System.currentTimeMillis());
  	  	    cc.setValue("value1");
  	  	    request.setConfigChangeDTO(cc);
  	  	    ConfigChangeResponse respone = AnolePublisher.add(request);
  	  	    System.out.println(JSON.toJSONString(respone));
  	    }
  	   
    }
     
}
