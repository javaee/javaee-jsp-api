

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


package org.apache.naming.java;

import java.util.Hashtable;
import javax.naming.Name;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.InitialContextFactory;
import org.apache.naming.SelectorContext;
import org.apache.naming.NamingContext;
import org.apache.naming.ContextBindings;

/**
 * Context factory for the "java:" namespace.
 * <p>
 * <b>Important note</b> : This factory MUST be associated with the "java" URL
 * prefix, which can be done by either :
 * <ul>
 * <li>Adding a 
 * java.naming.factory.url.pkgs=org.apache.catalina.util.naming property
 * to the JNDI properties file</li>
 * <li>Setting an environment variable named Context.URL_PKG_PREFIXES with 
 * its value including the org.apache.catalina.util.naming package name. 
 * More detail about this can be found in the JNDI documentation : 
 * {@link javax.naming.spi.NamingManager#getURLContext(java.lang.String, java.util.Hashtable)}.</li>
 * </ul>
 * 
 * @author Remy Maucherat
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:15 $
 */

public class javaURLContextFactory
    implements ObjectFactory, InitialContextFactory {


    // ----------------------------------------------------------- Constructors


    // -------------------------------------------------------------- Constants


    public static final String MAIN = "initialContext";


    // ----------------------------------------------------- Instance Variables


    /**
     * Initial context.
     */
    protected static Context initialContext = null;


    // --------------------------------------------------------- Public Methods


    // -------------------------------------------------- ObjectFactory Methods


    /**
     * Crete a new Context's instance.
     */
    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                    Hashtable environment)
        throws NamingException {
        if ((ContextBindings.isThreadBound()) || 
            (ContextBindings.isClassLoaderBound())) {
            return new SelectorContext(environment);
        } else {
            return null;
        }
    }


    /**
     * Get a new (writable) initial context.
     */
    public Context getInitialContext(Hashtable environment)
        throws NamingException {
        if (ContextBindings.isThreadBound() || 
            (ContextBindings.isClassLoaderBound())) {
            // Redirect the request to the bound initial context
            return new SelectorContext(environment, true);
        } else {
            // If the thread is not bound, return a shared writable context
            if (initialContext == null)
                initialContext = new NamingContext(environment, MAIN);
            return initialContext;
        }
    }


}

