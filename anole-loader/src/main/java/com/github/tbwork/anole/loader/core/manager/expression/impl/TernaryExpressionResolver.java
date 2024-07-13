package com.github.tbwork.anole.loader.core.manager.expression.impl;

import com.github.tbwork.anole.loader.exceptions.ErrorSyntaxException;
import com.github.tbwork.anole.loader.core.manager.expression.ExpressionResolver;

public class TernaryExpressionResolver implements ExpressionResolver {


    /**
     * Process the equal expression like "a==b"
     */
    private EqualExpressionResolver equalExpressionResolver;


    public TernaryExpressionResolver(){
        equalExpressionResolver = new EqualExpressionResolver();
    }

    @Override
    public boolean suit(String expression) {
        return expression.contains("?") && expression.contains(":");
    }

    @Override
    public String resolve(String key, String expression) {
        return parseThreeElementExpression(key, expression);
    }

    private String parseThreeElementExpression(String key, String value){

        String [] firstElements = value.split("\\?");
        if(firstElements.length == 2){
            String firstElement = firstElements[0].trim();
            boolean booleanExpressionResult = judgeTrue(key, firstElement);
            String [] secondELements = firstElements[1].split(":");
            if(secondELements.length == 2){
                String secondElement = secondELements[0];
                String thirdElement = secondELements[1];
                if(booleanExpressionResult){
                    return secondElement;
                }
                else {
                    return thirdElement;
                }
            }
        }

        String message = String.format("The right three-element-expression should be '${boolean_value} ? ${true_result} : ${false_result}' or '${value_1}==${value_2} ? ${true_result} : ${false_result}' while yours is '%s'", value);
        throw new ErrorSyntaxException(key, message);

    }

    private boolean judgeTrue(String key, String expression){

        expression = expression.trim();

        if(equalExpressionResolver.suit(expression)){
            expression = equalExpressionResolver.resolve(key, expression);
        }

        if("true".equals(expression)){
            return true;
        }
        if("false".equals(expression)){
            return false;
        }

        String message = String.format("Can not calculate a boolean value according to the expression: %s ", expression);
        throw new ErrorSyntaxException(key, message);
    }
}
