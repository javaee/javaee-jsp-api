/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 *
 * You can obtain a copy of the license at
 * glassfish/bootstrap/legal/CDDLv1.0.txt or
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * HEADER in each file and include the License file at
 * glassfish/bootstrap/legal/CDDLv1.0.txt.  If applicable,
 * add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your
 * own identifying information: Portions Copyright [yyyy]
 * [name of copyright owner]
 *
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */package org.apache.jasper.runtime;

/**
 * Implements javax.servlet.jsp.JspApplication
 */

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspApplicationContext;

import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ELContextListener;
import javax.el.ELContextEvent;

import org.apache.jasper.Constants;

public class JspApplicationContextImpl implements JspApplicationContext {

    public JspApplicationContextImpl(ServletContext context) {
        this.context = context;
    }

    public void addELResolver(ELResolver resolver) {
        if ("true".equals(context.getAttribute(Constants.FIRST_REQUEST_SEEN))) {
            throw new IllegalStateException("Attempt to invoke addELResolver "
                + "after the application has already received a request");
        }

        elResolvers.add(0, resolver);
    }

    public ExpressionFactory getExpressionFactory() {
        return expressionFactory;
    }

    public void addELContextListener(ELContextListener listener) {
        listeners.add(listener);
    }

    protected ELContext createELContext(ELResolver resolver) {

        ELContext elContext = new ELContextImpl(resolver);

        // Notify the listeners
        Iterator iter = listeners.iterator();
        while (iter.hasNext()) {
            ELContextListener elcl = (ELContextListener) iter.next();
            elcl.contextCreated(new ELContextEvent(elContext));
        }
        return elContext;
    }

    protected static JspApplicationContextImpl findJspApplicationContext(ServletContext context) {

        JspApplicationContextImpl jaContext =
            (JspApplicationContextImpl)map.get(context);
        if (jaContext == null) {
            jaContext = new JspApplicationContextImpl(context);
            map.put(context, jaContext);
        }
        return jaContext;
    }

    protected Iterator getELResolvers() {
        return elResolvers.iterator();
    }

    private static ExpressionFactory createExpressionFactoryImpl(String name) {
        ExpressionFactory exprFact = null;
        try {
            exprFact = (ExpressionFactory) Class.forName(name).newInstance();
        } catch (Throwable t) {
        }
        return exprFact;
    }

    static ExpressionFactory expressionFactory;

    private static final String SUN_EF =
        new String("com.sun.el.ExpressionFactoryImpl");
    private static final String COMMONS_EF =
        new String("org.apache.commons.el.ExpressionFactoryImpl");

    static {
        expressionFactory = createExpressionFactoryImpl(COMMONS_EF);
        if (expressionFactory == null) {
            expressionFactory = createExpressionFactoryImpl(SUN_EF);
        }
    }

    private static HashMap map = new HashMap();
    private ArrayList elResolvers = new ArrayList();
    private ArrayList listeners = new ArrayList();
    private ServletContext context;
}

