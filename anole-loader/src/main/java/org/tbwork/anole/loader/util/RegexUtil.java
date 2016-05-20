package org.tbwork.anole.loader.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

	
	 public static boolean asteriskMatch(String asteriskString, String targetString){
		  String asteriskRegex = asteriskString.replace("*", ".*");
		  Pattern pattern = Pattern.compile(asteriskRegex);
		  Matcher matcher = pattern.matcher(targetString);
		  return matcher.matches(); 
	 }
	
	 public static String [] splitConfigLocations(String configLocationString){
		 configLocationString = configLocationString.replace("\r\n", ",");
		 return configLocationString.split("[\r|\n|,]");
	 }
}
