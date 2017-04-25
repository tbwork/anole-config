package org.tbwork.anole.gui.domain.util;

public class CacheKeys {

	public static final String PROJECTS_CACHE_KEY = "anole_projects";
	public static final String ENVS_CACHE_KEY = "anole_envs";
	public static final String PRODUCT_LINE_CACHE_KEY = "anole_prd_lines";
	public static final String USERS_CACHE_KEY = "anole_users";
	public static final String PROJECT_CONFIGS_PREFIX = "anole_project_configs";
	public static final String CONFIG_PERMISSION_PREFIX = "anole_user_project_permission";
	public static final String CONFIG_CACHE_KEY ="anole_config_cache";
	public static final String CONFIG_INFO_CACHE_KEY ="anole_config_cache";
	public static final String SEARCH_RESULT_KEY ="search_result_cache";
	
	public static String buildConfigsForProjectKey(String project, String env){
		StringBuilder sb = new StringBuilder();
		sb.append(PROJECT_CONFIGS_PREFIX).append("-").append(project).append(env);
		return sb.toString();
	}
	
	public static String buildPermissionCacheKey(String project, String user, String env){
		StringBuilder sb = new StringBuilder();
		sb.append(CONFIG_PERMISSION_PREFIX).append("-").append(project).append(user).append(env);
		return sb.toString();
	}
	
	public static String buildConfigInfoCacheKey(String key){
		StringBuilder sb = new StringBuilder();
		sb.append(CONFIG_INFO_CACHE_KEY).append("-").append(key);
		return sb.toString();
	}
	
	public static String buildSearchResultKey(String searchText){
		StringBuilder sb = new StringBuilder();
		sb.append(SEARCH_RESULT_KEY).append("-").append(searchText);
		return sb.toString();
	}
}
