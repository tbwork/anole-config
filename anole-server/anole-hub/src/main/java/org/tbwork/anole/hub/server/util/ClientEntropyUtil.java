package org.tbwork.anole.hub.server.util;

import java.util.Set;
import java.util.Map;
import java.util.Map.Entry;

import org.tbwork.anole.common.enums.ClientType;
import org.tbwork.anole.hub.server.lccmanager.model.clients.LongConnectionClient;
import org.tbwork.anole.hub.server.lccmanager.model.clients.WorkerClient;

public class ClientEntropyUtil {

	
	public static final int MAX_INT  = Integer.MAX_VALUE; 
	
	public static WorkerClient selectBestWorker(ClientType clientType, Map<Integer, LongConnectionClient> lcMap){
		Set<Entry<Integer, LongConnectionClient>> entrySet = lcMap.entrySet();
		WorkerClient best = null;
		int currentEntropy = MAX_INT;
		for(Entry<Integer, LongConnectionClient> item: entrySet){ 
			if(best == null) best = (WorkerClient) item.getValue(); 
			else{
				WorkerClient itemWc = (WorkerClient) item.getValue();
				int tempEntropy = entropy(getOccupiedCount(itemWc, clientType), itemWc.getWeight());
				if(currentEntropy > tempEntropy){
					best = itemWc;
					currentEntropy = tempEntropy;
				}
			}
		}
		return best;
	}
	
	
	private static int getOccupiedCount(WorkerClient wc, ClientType ct){ 
		if(ct == ClientType.PUBLISHER){
			return wc.getPublisherClientCount();
		}
		else{
			return wc.getSubscriberClientCount();
		}
	}
	private static int entropy(int occupiedCount, int weight){ 
		float ratio = 1.0f/ ((float)weight);
		return (int)(ratio*((float)occupiedCount)); 
	}
	
}
