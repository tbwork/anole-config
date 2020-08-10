package org.tbwork.anole.loader.core.manager.expression;

import org.tbwork.anole.loader.core.manager.expression.impl.EqualExpressionResolver;
import org.tbwork.anole.loader.core.manager.expression.impl.TernaryExpressionResolver;
import org.tbwork.anole.loader.exceptions.OperationNotSupportedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Expression resolver's factory
 */
public class ExpressionResolverFactory {

    public static final List<ExpressionResolver> expressionResolvers = new ArrayList<>();

    static{
        expressionResolvers.add(new EqualExpressionResolver());
        expressionResolvers.add(new TernaryExpressionResolver());
    }


    /**
     * Find the first resolver that can resolve the given expression.
     *
     * @param expression the target expression
     * @return the first matched resolver
     */
    public static ExpressionResolver findSuitableExpressionResolver(String expression){
        for(ExpressionResolver expressionResolver : expressionResolvers){

            if(expressionResolver.suit(expression)){
                return expressionResolver;
            }

        }
        throw new OperationNotSupportedException("There is no supported expression resolver for the expression: " + expression);
    }

}
