

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
 *
 * Portions Copyright Apache Software Foundation.
 */ 
package org.apache.jasper.compiler;


import java.util.Vector;
import java.util.Hashtable;

import org.apache.jasper.JasperException;

/**
 * Repository of {page, request, session, application}-scoped beans 
 *
 * @author Mandar Raje
 */
class BeanRepository {

    private Vector sessionBeans;
    private Vector pageBeans;
    private Vector appBeans;
    private Vector requestBeans;
    private Hashtable beanTypes;
    private ClassLoader loader;
    private ErrorDispatcher errDispatcher;

    /*
     * Constructor.
     */    
    public BeanRepository(ClassLoader loader, ErrorDispatcher err) {

        this.loader = loader;
	this.errDispatcher = err;

	sessionBeans = new Vector(11);
	pageBeans = new Vector(11);
	appBeans = new Vector(11);
	requestBeans = new Vector(11);
	beanTypes = new Hashtable();
    }
        
    public void addBean(Node.UseBean n, String s, String type, String scope)
	    throws JasperException {

	if (scope == null || scope.equals("page")) {
	    pageBeans.addElement(s);	
	} else if (scope.equals("request")) {
	    requestBeans.addElement(s);
	} else if (scope.equals("session")) {
	    sessionBeans.addElement(s);
	} else if (scope.equals("application")) {
	    appBeans.addElement(s);
	} else {
	    errDispatcher.jspError(n, "jsp.error.invalid.scope", scope);
	}
	
	putBeanType(s, type);
    }
            
    public Class getBeanType(String bean) throws JasperException {
	Class clazz = null;
	try {
	    clazz = loader.loadClass ((String)beanTypes.get(bean));
	} catch (ClassNotFoundException ex) {
	    throw new JasperException (ex);
	}
	return clazz;
    }
      
    public boolean checkVariable (String bean) {
	// XXX Not sure if this is the correct way.
	// After pageContext is finalised this will change.
	return (checkPageBean(bean) || checkSessionBean(bean) ||
		checkRequestBean(bean) || checkApplicationBean(bean));
    }


    private void putBeanType(String bean, String type) {
	beanTypes.put (bean, type);
    }

    private boolean checkPageBean (String s) {
	return pageBeans.contains (s);
    }

    private boolean checkRequestBean (String s) {
	return requestBeans.contains (s);
    }

    private boolean checkSessionBean (String s) {
	return sessionBeans.contains (s);
    }

    private boolean checkApplicationBean (String s) {
	return appBeans.contains (s);
    }

}




