package org.tbwork.anole.loader.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tbwork.anole.loader.exceptions.ErrorSyntaxException;

import com.google.common.collect.Lists;

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
			result.add(prefix + str);
		}
		return result.toArray(strs);
	 }
	 
	 /**
	  * Check whether the input value contains another variable or not.
	  * E.g. following example input will cause the result of true. 
	  * <pre>
	  * 1. 123!{21212}213
	  * </pre>
	  * @param value the input value
	  * @param key for sake of trouble-shotting.
	  * @return true if the value contains another variable or more,
	  *  otherwise return false.
	  */
	 public static boolean checkContainVariable(String value, String key){
		 return getVariables(value, key).length > 0;
	 }
	 
	 /**
	  * Match and return all variables in the input string.
	  * E.g.
	  * <pre>
	  * 1. input : 123!{21212}2#{qwe1}13
	  * 2. result: ["!{21212}", "#{qwe1}"]
	  * </pre>
	  * @param value the input value
	  * @param key for sake of trouble-shooting.
	  * @return true if the value contains another variable or more,
	  *  otherwise return false.
	  */
	 public static String [] getVariables(String value, String key){
		 List<String> result = new ArrayList<String>();
		 int p = 0;
		 int vs = -1;
		 if(value == null)
			 throw new RuntimeException("There is no manual-set or default-set value for " + key + ".");
		 while( p < value.length())
		 {
			 char icl = p > 0 ? value.charAt(p-1) : ' ';
			 char ic = value.charAt(p);
			 char icn = p < value.length()-1 ?  value.charAt(p+1) : ' ';
			 if(checkHead(icl,ic, icn)){
				 if( vs > -1) {
					 String message =  "Anole does not support a variable's name is the value of another variable like #{#{a}}.";
					 throw new ErrorSyntaxException(key, message);
				 }
				 else
				     vs = p; 
			 } 
			 else if( ic == '}' && icl != '\\')  {
				 if(vs > -1){
					 result.add(value.substring(vs,p+1));
					 vs = -1;
				 }
				 else{
					 String message =  "Lack of '#{': an left brace '#{' is needed to match the right brace'}'.";
					 throw new ErrorSyntaxException(key, message);
				 }
			 } 	 
			 p ++;
		 }
		 if(vs > -1){
			 String message =  "Lack of '}': an right brace '}' is needed to match the left brace'{'.";
			 throw new ErrorSyntaxException(key, message);
		 }  
		 return result.toArray( (new String[0]));
	 }
	 
	 
	 /**
	  * Remove all the blank chars from the input str.
	  * @param str the input str
	  */
	 public static String trim(String str){  
		 return str.replaceAll("\\s", "") ;
	 }
	  
	 
	 public static String getVariable(String str){
		  return str.substring(2, str.length()-1).trim();
	 }

	 
	 private static boolean checkHead(char a, char b, char c){
		return a!='\\' && isHeadChar(b) && c == '{';
	 }
	 
	 private static boolean isHeadChar(char a){
		 return a == '~' || a == '!' || a == '@' || a == '#' || a == '$'; 
	 }
	 
	 public static String replaceEscapeChars(String input){
		return input.replace("\\~", "~").replace("\\!", "!").replace("\\@", "@").replace("\\#", "#").replace("\\$", "$").replace("\\{", "{").replace("\\}", "}");
	 }
	 
	 public static void main(String[] args) { 
//		 for(String item : getVariables("#{\\#\\{ip}:!{port}", "key")){
//			 System.out.println(item);
//		 } 
		// System.out.println(replaceEscapeChars("\\@\\{\\}"));
		 System.out.println(asteriskMatch("/BOOT-INF/classes/*.properties","BOOT-INF/classes/env-dev.properties")); 
		
	 }
}
