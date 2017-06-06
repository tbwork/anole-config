package org.tbwork.anole.subscriber.core;
 
 
import org.tbwork.anole.loader.core.Anole; 
import org.tbwork.anole.subscriber.core.impl.ChainedConfigObserver;
import org.tbwork.anole.subscriber.core.impl.SubscriberConfigManager; 
 
 
/**
 * <p> Yes! It's an all-in-one tool. You can access
 * any function of Anole here. What does old
 * man always say? Anole in hand, World in pocket! :)
 * @author Tommy.Tang
 */ 
public class AnoleClient extends Anole{ 
	  
	private static ObserverManager om = ObserverManager.instance();  
	
	static{
		cm = SubscriberConfigManager.getInstance();
	}
	
	/**
	 * <p> Used to register observers who will be notified once
	 * a configuration-change is received, before being processed. 
	 * <p><b>Usage example:</b>
	 * <pre>  
	 *   1. AnoleConfig.registerPreObserver("keyname", new ChainedConfigObserver(false) { 
	 *   2.     @Override
	 *   3.     public void process(ValueChangeDTO vcd) {
	 *   4.          vcd.getKey(); 
	 *   5.          vcd.getValue();  
	 *   6.     }
	 *   7.});
	 * </pre>
	 * 
	 * @param key the key will be observed
	 * @param observer the observer
	 */
	public static void registerPreObserver(String key, ChainedConfigObserver observer){
		om.addPreObservers(key, observer);
	}
 
	/**
	 * <p> Used to register observers who will be notified after
	 * a configuration-change is received and processed.
	 * <p><b>Usage example:</b>
	 * <pre>  
	 *   1. AnoleConfig.registerPostObserver("keyname", new ChainedConfigObserver(false) { 
	 *   2.     @Override
	 *   3.     public void process(ValueChangeDTO vcd) {
	 *   4.          vcd.getKey(); 
	 *   5.          vcd.getValue(); 
	 *   6.     }
	 *   7.});
	 * </pre>
	 * 
	 * @param key the key will be observed
	 * @param observer the observer
	 */
	public static void registerPostObserver(String key, ChainedConfigObserver observer){
		om.addPostObservers(key, observer);
	} 
	 
}
