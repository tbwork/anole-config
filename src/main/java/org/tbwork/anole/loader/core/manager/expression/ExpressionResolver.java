package org.tbwork.anole.loader.core.manager.expression;

/**
 * Resolve the expression and calculate the result.
 */
public interface ExpressionResolver {


    /**
     * Whether this resolver can resolve the expression, or not.
     * @param expression
     * @return true if this resolver could resolve the expression.
     */
    boolean suit(String expression);

    /**
     * Resolve the expression and return the result value as a string.
     *
     * @param key for sake of trouble-shooting
     * @param expression the expression
     * @return the result value
     */
    String resolve(String key, String expression);

}
