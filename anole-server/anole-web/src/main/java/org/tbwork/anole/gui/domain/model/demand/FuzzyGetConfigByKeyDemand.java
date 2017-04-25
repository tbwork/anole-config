package org.tbwork.anole.gui.domain.model.demand;

import com.google.common.base.Preconditions;

import lombok.Data;

@Data
public class FuzzyGetConfigByKeyDemand  extends BaseOperationDemand{
	private String searchText;
	private String env;
	
	public void preCheck(){ 
		Preconditions.checkArgument(searchText != null && !searchText.isEmpty(), "search text should not be noll or empty.");	
	}
}
