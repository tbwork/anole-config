package com.github.tbwork.anole.loader.core.manager.expression.impl;

import com.github.tbwork.anole.loader.exceptions.ErrorSyntaxException;
import com.github.tbwork.anole.loader.core.manager.expression.ExpressionResolver;

/**
 * To resolve the expressions like "a==b", "true", "false" and so on.
 */
public class EqualExpressionResolver implements ExpressionResolver {


    @Override
    public boolean suit(String expression) {
        return expression.contains("==") && !expression.contains("?");
    }

    @Override
    public String resolve(String key, String expression) {

        String [] subExpressions = expression.trim().split("==");
        if(subExpressions.length == 2){
            if(subExpressions[0].trim().equals(subExpressions[1].trim())){
                return "true";
            }
            else{
                return "false";
            }
        }
        String message = String.format("The right equal-expression should be '${value_1}==${value_2}' while yours is '%s'", expression);
        throw new ErrorSyntaxException(key, message);
    }


}
