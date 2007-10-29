package org.apache.jasper.runtime;

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

public class JspApplicationContextImpl implements JspApplicationContext {

    public void addELResolver(ELResolver resolver) {
        // TODO: Check for IllegalStateException
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
            jaContext = new JspApplicationContextImpl();
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
}

