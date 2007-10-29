/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */ 

package org.apache.jasper.runtime;

import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ValueExpression;
import javax.el.ExpressionFactory;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.Expression;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.FunctionMapper;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;

/**
 * <p>This is the implementation of ExpreesioEvaluator
 * using implementation of JSP2.1.
 * 
 * @author Kin-man Chung
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author: ja120114 $
 **/

public class ExpressionEvaluatorImpl extends ExpressionEvaluator
{
    private PageContext pageContext;

    //-------------------------------------
    /**
     * Constructor
     **/
    public ExpressionEvaluatorImpl (PageContext pageContext) {
        this.pageContext = pageContext;
    }
  
    //-------------------------------------
    public Expression parseExpression(String expression,
                                      Class expectedType,
                                      FunctionMapper fMapper )
            throws ELException {

        ExpressionFactory fac = JspApplicationContextImpl.expressionFactory;
        javax.el.ValueExpression expr;
        ELContextImpl elContext = new ELContextImpl(null);
        javax.el.FunctionMapper fm = new FunctionMapperWrapper(fMapper);
        elContext.setFunctionMapper(fm);
        try {
            expr = fac.createValueExpression(
                           elContext,
                           expression, expectedType);
        } catch (javax.el.ELException ex) {
            throw new ELException(ex);
        }
        return new ExpressionImpl(expr, pageContext);
    }

     public Object evaluate(String expression,
                            Class expectedType,
                            VariableResolver vResolver,
                            FunctionMapper fMapper )
                throws ELException {

        ELContextImpl elContext;
        if (vResolver instanceof VariableResolverImpl) {
            elContext = (ELContextImpl) pageContext.getELContext();
        }
        else {
            // The provided variable Resolver is a custom resolver,
            // wrap it with a ELResolver 
            elContext = new ELContextImpl(new ELResolverWrapper(vResolver));
        }

        javax.el.FunctionMapper fm = new FunctionMapperWrapper(fMapper);
        elContext.setFunctionMapper(fm);
        ExpressionFactory fac = JspApplicationContextImpl.expressionFactory;
        Object value;
        try {
            ValueExpression expr = fac.createValueExpression(
                                 elContext,
                                 expression,
                                 expectedType);
            value = expr.getValue(elContext);
        } catch (javax.el.ELException ex) {
            throw new ELException(ex);
        }
        return value;
    }

    static private class ExpressionImpl extends Expression {

        private ValueExpression valueExpr;
        private PageContext pageContext;

        ExpressionImpl(ValueExpression valueExpr,
                       PageContext pageContext) {
            this.valueExpr = valueExpr;
            this.pageContext = pageContext;
        }

        public Object evaluate(VariableResolver vResolver) throws ELException {

            ELContext elContext;
            if (vResolver instanceof VariableResolverImpl) {
                elContext = pageContext.getELContext();
            }
            else {
                // The provided variable Resolver is a custom resolver,
                // wrap it with a ELResolver 
                elContext = new ELContextImpl(new ELResolverWrapper(vResolver));
            }
            try {
                return valueExpr.getValue(elContext);
            } catch (javax.el.ELException ex) {
                throw new ELException(ex);
            }
        }
    }

    private static class FunctionMapperWrapper
        extends javax.el.FunctionMapper {

        private FunctionMapper mapper;

        FunctionMapperWrapper(FunctionMapper mapper) {
            this.mapper = mapper;
        }

        public java.lang.reflect.Method resolveFunction(String prefix,
                                                        String localName) {
            return mapper.resolveFunction(prefix, localName);
        }
    }

    private static class ELResolverWrapper extends ELResolver {
        private VariableResolver vResolver;

        ELResolverWrapper(VariableResolver vResolver) {
            this.vResolver = vResolver;
        }

        public Object getValue(ELContext context,
                               Object base,
                               Object property)
                throws javax.el.ELException {
            if (base == null) {
                context.setPropertyResolved(true);
                try {
                    return vResolver.resolveVariable(property.toString());
                } catch (ELException ex) {
                    throw new javax.el.ELException(ex);
                }
            }
            return null;
        }

        public Class getType(ELContext context,
                             Object base,
                             Object property)
                throws javax.el.ELException {
            return null;
        }

        public void setValue(ELContext context,
                             Object base,
                             Object property,
                             Object value)
                throws javax.el.ELException {
        }

        public boolean isReadOnly(ELContext context,
                                  Object base,
                                  Object property)
                throws javax.el.ELException {
            return false;
        }

        public Iterator getFeatureDescriptors(ELContext context,
                                              Object base) {
            return null;
        }

        public Class getCommonPropertyType(ELContext context,
                                           Object base) {
            return null;
        }
    }
}
