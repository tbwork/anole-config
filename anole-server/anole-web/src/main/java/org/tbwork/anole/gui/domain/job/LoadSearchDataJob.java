package org.tbwork.anole.gui.domain.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tbwork.anole.gui.domain.config.IConfigSearchService;

@Component("loadSearchDataJob")
public class LoadSearchDataJob {

	private static Logger logger = LoggerFactory.getLogger(LoadSearchDataJob.class);
	
	@Autowired
	private IConfigSearchService configSearchService;

	private static final int  LOAD_SEARCH_DATA_INTERVAL = 1*60*1000; // 1min
	@Scheduled(fixedDelay = LOAD_SEARCH_DATA_INTERVAL)
	public void run(){
		logger.info("Start to load search data from database!");
		configSearchService.deltaUpdate();
		logger.info("Load search data from database successfully!");
	}
}
