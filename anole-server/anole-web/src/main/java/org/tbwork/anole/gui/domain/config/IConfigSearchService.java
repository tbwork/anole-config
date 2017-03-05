package org.tbwork.anole.gui.domain.config;

import java.util.List;

import org.tbwork.anole.gui.domain.model.ConfigBrief;
import org.tbwork.anole.gui.domain.model.ConfigExtended;
import org.tbwork.anole.gui.domain.model.demand.FuzzyGetConfigByKeyDemand;

public interface IConfigSearchService {

	public List<ConfigExtended> fuzzySearch(FuzzyGetConfigByKeyDemand demand);
	
	public void fullUpdate();
	
	public void deltaUpdate();
 
}
