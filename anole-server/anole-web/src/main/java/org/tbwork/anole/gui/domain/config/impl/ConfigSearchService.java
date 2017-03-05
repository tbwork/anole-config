package org.tbwork.anole.gui.domain.config.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.anole.infrastructure.dao.AnoleConfigItemMapper;
import org.anole.infrastructure.dao.AnoleConfigMapper;
import org.anole.infrastructure.model.AnoleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.tbwork.anole.gui.domain.cache.Cache;
import org.tbwork.anole.gui.domain.config.IConfigSearchService;
import org.tbwork.anole.gui.domain.config.IConfigService;
import org.tbwork.anole.gui.domain.model.Config;
import org.tbwork.anole.gui.domain.model.ConfigBrief;
import org.tbwork.anole.gui.domain.model.ConfigExtended;
import org.tbwork.anole.gui.domain.model.demand.FuzzyGetConfigByKeyDemand;

import lombok.Data;

@Service
public class ConfigSearchService implements IConfigSearchService{

	private static Logger logger = LoggerFactory.getLogger(ConfigSearchService.class);
	private Map<String, SortItem> configMap = new HashMap<String, SortItem>();
	
	private Date configLastUpdatetime = new Date(0); 
	
	@Autowired
	private AnoleConfigItemMapper anoleConfigItemMapper;
	@Autowired
	private AnoleConfigMapper anoleConfigMapper;
	@Autowired
	private IConfigService configService;
	
	@Autowired 
	@Qualifier("localCache")
	private Cache lc ;
	
	@Data
	public static class SortItem{
		private String [] keyWords;
		private Config config;
	}
	
	@Data
	public static class SortItemWithScore{
		private SortItem sortItem;
		private int score;
	}
	 

	@Override
	public List<ConfigExtended> fuzzySearch(FuzzyGetConfigByKeyDemand demand) {
		demand.preCheck();
		String searchText = demand.getSearchText();
		String env = demand.getEnv();
		String operator = demand.getOperator();
		//split words
		List<ConfigExtended> result = new ArrayList<ConfigExtended> ();
		String [] words = searchText.split("\\.| ");
		List<SortItemWithScore> sortResult = new ArrayList<SortItemWithScore>();
		Set<Entry<String,SortItem>> entrySet = configMap.entrySet();
		for(Entry<String,SortItem> entry : entrySet){
			int score = 0;
			for(String word : words){
				score += matchConfig(word, entry.getValue());
			}
			if(score > 0 ){
				SortItemWithScore sortItemWithScore = new SortItemWithScore();
				sortItemWithScore.setSortItem(entry.getValue());
				sortItemWithScore.setScore(score);
				sortResult.add(sortItemWithScore);
			} 
		}
		Collections.sort(sortResult, new Comparator<SortItemWithScore>(){
			@Override
			public int compare(SortItemWithScore o1, SortItemWithScore o2) {
				return o1.getScore() - o2.getScore() > 0 ? -1 : ( o1.getScore() - o2.getScore() < 0 ? 1 : 0);
			} 
		});
		for(SortItemWithScore sortItemWithScore : sortResult){
			ConfigExtended configEx = new ConfigExtended();
			ConfigExtended 
			//result.add();
		}
		return null;
	}


	@Override
	public void fullUpdate() {
		configLastUpdatetime = new Date(0);
		deltaUpdate();
	}


	@Override
	public void deltaUpdate() {
		while(true){
			List<AnoleConfig> configs = anoleConfigMapper.selectConfigsByUpdatedTime(configLastUpdatetime);
			if(configs!=null && !configs.isEmpty()){
				for(AnoleConfig anoleConfig : configs){
					if(!configMap.containsKey(anoleConfig.getKey())){
						SortItem sortItem = new SortItem();
						sortItem.setKeyWords(anoleConfig.getKey().split("."));
						Arrays.sort(sortItem.getKeyWords());
						Config config = new Config();
						config.setDesc(anoleConfig.getDescription());
						config.setProject(anoleConfig.getProject());
						config.setType((int)anoleConfig.getType());
						config.setValues(new HashMap<String,String>());
						sortItem.setConfig(config);
						configMap.put(anoleConfig.getKey(), sortItem);
						configLastUpdatetime = config.getUpdateTime();   
					}
				}
			}
			else{
				logger.info("Loading updated configs successfully! The last config's updatetime is {}", configLastUpdatetime);
				break;
			}
		}
	}
	
	/**
	 * 使用二分搜索算法计算某个关键字是否在某个配置的关键字中：
	 * 包含（1分），不包含（0分）
	 * @param word 待匹配的字符串
	 * @return
	 */
	private int matchConfig(String word, SortItem sortItem){
		String [] words = sortItem.getKeyWords();
		int s = 0;
		int e = words.length-1;
		while(s <= e){
			int m = (s+e)/2; 
			if( word.compareTo(words[m]) == 1){ //>
				s = m + 1;
			}else if( word.compareTo(words[m]) == -1 ){
				e = m - 1;
			} 
			else{
				return 1;
			}
		}
		return 0;
	}
	

 
}
