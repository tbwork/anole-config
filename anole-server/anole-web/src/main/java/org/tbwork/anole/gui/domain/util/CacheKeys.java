package org.tbwork.anole.gui.domain.util;

public class CacheKeys {

	public static final String PROJECTS_CACHE_KEY = "anole_projects";
	public static final String ENVS_CACHE_KEY = "anole_envs";
	public static final String PRODUCT_LINE_CACHE_KEY = "anole_prd_lines";
	public static final String USERS_CACHE_KEY = "anole_users";
	public static final String PROJECT_CONFIGS_PREFIX = "anole_project_configs";
	
	
	public static String buildConfigsForProjectKey(String project, String env){
		StringBuilder sb = new StringBuilder();
		sb.append(PROJECT_CONFIGS_PREFIX).append(project).append(env);
		return sb.toString();
	}
}
