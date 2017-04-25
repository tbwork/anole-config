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
import org.tbwork.anole.gui.domain.model.ConfigInfo;
import org.tbwork.anole.gui.domain.model.ConfigBrief;
import org.tbwork.anole.gui.domain.model.ConfigExtended;
import org.tbwork.anole.gui.domain.model.demand.FuzzyGetConfigByKeyDemand;
import org.tbwork.anole.gui.domain.permission.IPermissionService;
import org.tbwork.anole.gui.domain.project.IProjectService;
import org.tbwork.anole.gui.domain.util.CacheKeys;

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
	private IPermissionService pers;
	@Autowired
	private IProjectService projectService;
	private static int limit = 50;
	
	@Autowired 
	@Qualifier("localCache")
	private Cache lc ;
	
	@Data
	public static class SortItem{
		private String [] keyWords;
		private ConfigInfo configInfo;
	}
	
	@Data
	public static class SortItemWithScore{
		private SortItem sortItem;
		private int score;
	}
	 
	private List<SortItemWithScore> fuzzySearchWholeConfigs(String searchText ){ 
		String cacheKey = CacheKeys.buildSearchResultKey(searchText);
		List<SortItemWithScore> result = lc.get(cacheKey);
		if(result != null) return result;
		String [] words = searchText.split("\\.| ");
		result = new ArrayList<SortItemWithScore>();
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
				result.add(sortItemWithScore);
			}
		}
		Collections.sort(result, new Comparator<SortItemWithScore>(){
			@Override
			public int compare(SortItemWithScore o1, SortItemWithScore o2) {
				return o1.getScore() - o2.getScore() > 0 ? -1 : ( o1.getScore() - o2.getScore() < 0 ? 1 : 0);
			} 
		}); 
		result =  result.size() > limit ? result.subList(0, limit) : result;
		lc.set(cacheKey, result, 5*60*1000); // cache for 5 mins
		return result;
	}

	private void validateEnvironment(String environment, List<String > envs){ 
		for(String env : envs){
			if(env.equals(environment)) {
				return;
			}
		}
		throw new RuntimeException("Environmnent is not existed.");
	}
	@Override
	public List<ConfigExtended> fuzzySearch(FuzzyGetConfigByKeyDemand demand) {
		demand.preCheck();
		String searchText = demand.getSearchText(); 
		String operator = demand.getOperator();
		String environment = demand.getEnv();
		List<SortItemWithScore> searchResult = fuzzySearchWholeConfigs(searchText);
		List<ConfigExtended> result = new ArrayList<ConfigExtended>();
		for( SortItemWithScore item : searchResult ){
			ConfigInfo configInfo = item.getSortItem().getConfigInfo(); 
			List<String> envs = projectService.getEnvs();
			ConfigExtended configExtended = new ConfigExtended();
			configExtended.setDesc(configInfo.getDesc()); 
			configExtended.setKey(configInfo.getKey());
			configExtended.setProject(configInfo.getProject());
			configExtended.setType(configInfo.getType());
			Map<String, String> values = new HashMap<String, String>();
			configExtended.setValues(values);
			if(environment == null || environment.isEmpty()){  
				for(String env :envs){ 
					setValueForConfigExtended(configExtended, configInfo.getProject(), operator, env);
				} 
			}
			else{
				validateEnvironment(environment, envs);
				setValueForConfigExtended(configExtended, configInfo.getProject(), operator, environment);
			}
			result.add(configExtended);
		} 
		return result;
	} 
	
	
	private void setValueForConfigExtended(ConfigExtended configExtended, String project, String operator, String environment){
		int permission = pers.getUserRole(project, operator, environment);
		if(permission == 0){
			configExtended.shiledValue(environment);
		}
		else{
			ConfigBrief configBrief = configService.getConfigByKeyAndEnv(configExtended.getKey(), environment);
			configExtended.putValue(environment, configBrief.getValue());
		} 
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
						sortItem.setKeyWords(anoleConfig.getKey().split("\\."));
						Arrays.sort(sortItem.getKeyWords());
						ConfigInfo configInfo = new ConfigInfo();
						configInfo.setKey(anoleConfig.getKey());
						configInfo.setDesc(anoleConfig.getDescription());
						configInfo.setProject(anoleConfig.getProject());
						configInfo.setType((int)anoleConfig.getType()); 
						sortItem.setConfigInfo(configInfo);
						configMap.put(anoleConfig.getKey(), sortItem);
						configLastUpdatetime = anoleConfig.getUpdateTime();   
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
			if(words[m].startsWith(word))
				return 1;
			if( word.compareTo(words[m]) > 0){ //>
				s = m + 1;
			}else if( word.compareTo(words[m]) < 0 ){
				e = m - 1;
			}
		}
		return 0;
	}
	 
 
}
