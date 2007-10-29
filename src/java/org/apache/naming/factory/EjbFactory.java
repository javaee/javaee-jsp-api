

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


package org.apache.naming.factory;

import java.util.Hashtable;
import javax.naming.Name;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.RefAddr;
import javax.naming.spi.ObjectFactory;
import org.apache.naming.EjbRef;

/**
 * Object factory for EJBs.
 * 
 * @author Remy Maucherat
 * @version $Revision: 1.2 $ $Date: 2005/12/08 01:29:06 $
 */

public class EjbFactory
    implements ObjectFactory {


    private static com.sun.org.apache.commons.logging.Log log=
        com.sun.org.apache.commons.logging.LogFactory.getLog( EjbFactory.class );

    // ----------------------------------------------------------- Constructors


    // -------------------------------------------------------------- Constants


    // ----------------------------------------------------- Instance Variables


    // --------------------------------------------------------- Public Methods


    // -------------------------------------------------- ObjectFactory Methods


    /**
     * Crete a new EJB instance.
     * 
     * @param obj The reference object describing the DataSource
     */
    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                    Hashtable environment)
        throws Exception {
        
        if (obj instanceof EjbRef) {
            Reference ref = (Reference) obj;

            // If ejb-link has been specified, resolving the link using JNDI
            RefAddr linkRefAddr = ref.get(EjbRef.LINK);
            if (linkRefAddr != null) {
                // Retrieving the EJB link
                String ejbLink = linkRefAddr.getContent().toString();
                Object beanObj = (new InitialContext()).lookup(ejbLink);
                // Load home interface and checking if bean correctly
                // implements specified home interface
                /*
                String homeClassName = ref.getClassName();
                try {
                    Class home = Class.forName(homeClassName);
                    if (home.isInstance(beanObj)) {
                        if (log.isDebugEnabled()) 
                            log.debug("Bean of type " 
                                           + beanObj.getClass().getName() 
                                           + " implements home interface " 
                                           + home.getName());
                    } else {
                        if (log.isDebugEnabled())
                            log.debug("Bean of type " 
                                           + beanObj.getClass().getName() 
                                           + " doesn't implement home interface " 
                                           + home.getName());
                        throw new NamingException
                            ("Bean of type " + beanObj.getClass().getName() 
                             + " doesn't implement home interface " 
                             + home.getName());
                    }
                } catch (ClassNotFoundException e) {
                    log.warn("Couldn't load home interface "
                                       + homeClassName, e);
                }
                */
                return beanObj;
            }
            
            ObjectFactory factory = null;
            RefAddr factoryRefAddr = ref.get(Constants.FACTORY);
            if (factoryRefAddr != null) {
                // Using the specified factory
                String factoryClassName = 
                    factoryRefAddr.getContent().toString();
                // Loading factory
                ClassLoader tcl = 
                    Thread.currentThread().getContextClassLoader();
                Class factoryClass = null;
                if (tcl != null) {
                    try {
                        factoryClass = tcl.loadClass(factoryClassName);
                    } catch(ClassNotFoundException e) {
                    }
                } else {
                    try {
                        factoryClass = Class.forName(factoryClassName);
                    } catch(ClassNotFoundException e) {
                    }
                }
                if (factoryClass != null) {
                    try {
                        factory = (ObjectFactory) factoryClass.newInstance();
                    } catch(Throwable t) {
                    }
                }
            } else {
                String javaxEjbFactoryClassName =
                    System.getProperty("javax.ejb.Factory",
                                       Constants.OPENEJB_EJB_FACTORY);
                try {
                    factory = (ObjectFactory)
                        Class.forName(javaxEjbFactoryClassName).newInstance();
                } catch(Throwable t) {
                }
            }

            if (factory != null) {
                return factory.getObjectInstance
                    (obj, name, nameCtx, environment);
            } else {
                throw new NamingException
                    ("Cannot create resource instance");
            }

        }

        return null;

    }


}

