package org.tbwork.anole.loader.util;

import org.tbwork.anole.loader.exceptions.ErrorSyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	 private final static char [] escapeChars = {'~','!','@','#','$'};
	 /**
	  * 
	  * @param asteriskString the fuzzy match string using '*' as the match char.
	  * @param targetString the target string.
	  * @return true if the match string can match the target string, otherwise
	  * return false;
	  */
	 public static boolean asteriskMatch(String asteriskString, String targetString){
		  String asteriskRegex = asteriskString.replace(".", "\\.").replace("*", ".*");
		  Pattern pattern = Pattern.compile(asteriskRegex);
		  Matcher matcher = pattern.matcher(targetString);
		  return matcher.matches(); 
	 }
	
	 public static String [] splitConfigLocations(String configLocationString){
		 configLocationString = configLocationString.replace("\r\n", ",");
		 return configLocationString.split("[\r|\n|,]");
	 }
	 
	 public static String [] prefixString(String [] strs, String prefix){
		List<String> result = new ArrayList<String>();
		for(String str: strs){
			result.add(prefix + str.trim());
		}
		return result.toArray(strs);
	 }
	 

	 
	 /**
	  * Remove all the blank chars from the input str.
	  * @param str the input str
	  */
	 public static String trim(String str){  
		 //return str.replaceAll("\\s", "") ;
		 return str.trim();
	 }



	 public static String [] splitString2Array(String targetString, String delimiter){
		 targetString = targetString.trim();
		return  isNullOrEmpty(targetString) ? new String[]{"*.anole"} : targetString.split(delimiter);
	 }

	 public static String replaceEscapeChars(String input){
		return input.replace("\\@", "@").replace("\\$", "$").replace("\\{", "{").replace("\\}", "}");
	 }
	 
	 public static String join(String delimiter, String ...strings){
		 if(strings.length == 0)
			 return "";
		 StringBuilder sb = new StringBuilder();  
		 sb.append(strings[0]);
		 for(int i = 1; i < strings.length; i++){
			 sb.append(delimiter).append(strings[i]);
		 }
		 return sb.toString();
	 }

	public static String concat(String ...strings){
		if(strings.length == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < strings.length; i++){
			sb.append(strings[i]);
		}
		return sb.toString();
	}
	 
	 
	 public static String join(String delimiter, List<String> strings){
		 if(strings.size() == 0)
			 return "";
		 StringBuilder sb = new StringBuilder(); 
		 sb.append(strings.get(0));
		 for(int i = 1; i < strings.size(); i++){
			 sb.append(delimiter).append(strings.get(i));
		 }
		 return sb.toString();
	 }
	 
	 public static boolean isNullOrEmpty(String inputString) {
		 return inputString == null || inputString.isEmpty();
	 }

	public static boolean isNotEmpty(String inputString) {
		return !isNullOrEmpty(inputString);
	}
	 
	 
	 /**
	  * <p> For example, <b>getRepeatCharString('a',3)</b> will 
	  * return a string value of "aaa";
	 * @param a the candidate char
	 * @param count the repeat times
	 * @return as one string.
	 */
	 public static String getRepeatCharString(char a, int count){
    	 int i = 0;
    	 StringBuilder sb = new StringBuilder();
    	 while( i ++ < count){
    	 	 sb.append(a);
    	 }
    	 return sb.toString();
     }
	 
	 public static String [] trimStrings(String ... strings) {
		 String [] result = new String[strings.length];
		 for(int i =0 ; i < strings.length; i++) {
			 result[i] = strings[i].trim();
		 }
		 return result;
	 }
	 
	 
}
