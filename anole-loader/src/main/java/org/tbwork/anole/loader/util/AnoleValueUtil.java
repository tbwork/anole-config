package org.tbwork.anole.loader.util;

import org.tbwork.anole.loader.exceptions.ErrorSyntaxException;

import java.util.ArrayList;
import java.util.List;

/**
 * Tools for processing anole config's value.
 */
public class AnoleValueUtil {




    /**
     * Search and return all variables in the input string.
     * E.g.
     * <pre>
     * 1. input : 123${21212}2${qwe1}13
     * 2. result: ["21212", "qwe1"]
     * </pre>
     * @param value the input value
     * @param key for sake of trouble-shooting.
     * @return the variables referenced by the given key
     */
    public static String [] getVariables(String value, String key){

        String [] variablesWithCloth = getVariablesWithCloth(value, key);

        List<String> resultList = new ArrayList<>();

        for(String variableWithCloth : variablesWithCloth){
            resultList.add(getVariable(variableWithCloth.trim(), key));
        }

        return resultList.toArray(new String[0]);
    }



    /**
     * Match and return all variables with cloth in the input string.
     * E.g.
     * <pre>
     * 1. input : 123${21212}2${qwe1}13
     * 2. result: ["${21212}", "${qwe1}"]
     * </pre>
     * @param value the input value
     * @param key for sake of trouble-shooting.
     * @return the variables with cloth
     */
    public static String [] getVariablesWithCloth(String value, String key){
        List<String> result = new ArrayList<String>();
        int p = 0;
        int vs = -1;
        if(value == null)
            throw new RuntimeException("There is no manual-set or default-set value for '" + key + "'.");
        while( p < value.length())
        {
            char icl = p > 0 ? value.charAt(p-1) : ' ';
            char ic = value.charAt(p);
            char icn = p < value.length()-1 ?  value.charAt(p+1) : ' ';
            if(checkHead(icl,ic, icn)){
                if( vs > -1) {
                    String message =  "Anole does not support a variable's name is the value of another variable like ${${a}}.";
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
                    String message =  "Lack of '${': an left brace '${' is needed to match the right brace'}'.";
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
     * Get variable name from the target string.
     *
     * @param str the target string.
     * @param ownerKey the owner key, for trouble-shooting.
     * @return
     */
    public static String getVariable(String str, String ownerKey){
        String resultKey =  str.substring(2, str.length()-1).trim();
        if(StringUtil.isNullOrEmpty(resultKey)){
            throw new ErrorSyntaxException(ownerKey, str + " must contains a valid variable.");
        }
        return resultKey;
    }


    /**
     * Check whether the input value contains another variable or not.
     * E.g. following example input will cause the result of true.
     * <pre>
     * 1. 123${21212}213
     * </pre>
     * @param value the input value
     * @return true if the value contains another variable or more,
     *  otherwise return false.
     */
    public static boolean containVariable(String value){
        if(StringUtil.isNullOrEmpty(value)){
            return false;
        }
        List<String> result = new ArrayList<String>();
        int p = 0;
        int vs = -1;
        while( p < value.length())
        {
            char icl = p > 0 ? value.charAt(p-1) : ' ';
            char ic = value.charAt(p);
            char icn = p < value.length()-1 ?  value.charAt(p+1) : ' ';
            if(checkHead(icl,ic, icn)){
                    vs = p;
            }
            else if( ic == '}' && icl != '\\')  {
                if(vs > -1){
                   return true;
                }
            }
            p ++;
        }
       return false;
    }


    public static boolean isExpression(String definition){
        if(definition == null){
            return false;
        }
        return definition.trim().startsWith("@@");
    }

    public static String getExpression(String definition){
        if(definition == null || !isExpression(definition)){
            // generally impossible
           throw new RuntimeException("Expression definition should not be empty.");
        }
        definition = definition.trim();
        return definition.substring(2);
    }


    private static boolean checkHead(char a, char b, char c){
        return a!='\\' && isHeadChar(b) && c == '{';
    }


    private static boolean isHeadChar(char a){
        return  a == '$';
    }





}
