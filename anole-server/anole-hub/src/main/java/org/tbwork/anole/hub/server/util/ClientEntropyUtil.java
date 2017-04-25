package org.tbwork.anole.hub.server.util;

import java.util.Set;
import java.util.Map;
import java.util.Map.Entry;

import org.tbwork.anole.common.enums.ClientType;
import org.tbwork.anole.hub.server.lccmanager.model.clients.LongConnectionClientSkeleton;
import org.tbwork.anole.hub.server.lccmanager.model.clients.WorkerClientSkeleton;

public class ClientEntropyUtil {

	
	public static final int MAX_INT  = Integer.MAX_VALUE; 
	
	public static WorkerClientSkeleton selectBestWorker(ClientType clientType, Map<Integer, LongConnectionClientSkeleton> lcMap){
		Set<Entry<Integer, LongConnectionClientSkeleton>> entrySet = lcMap.entrySet();
		WorkerClientSkeleton best = null;
		int currentEntropy = MAX_INT;
		for(Entry<Integer, LongConnectionClientSkeleton> item: entrySet){ 
			if(best == null) best = (WorkerClientSkeleton) item.getValue(); 
			else{
				WorkerClientSkeleton itemWc = (WorkerClientSkeleton) item.getValue();
				int tempEntropy = entropy(itemWc.getSubscriberClientCount(), itemWc.getWeight());
				if(currentEntropy > tempEntropy){
					best = itemWc;
					currentEntropy = tempEntropy;
				}
			}
		}
		return best;
	}
	 
	private static int entropy(int occupiedCount, int weight){ 
		float ratio = 1.0f/ ((float)weight);
		return (int)(ratio*((float)occupiedCount)); 
	}
	
}
