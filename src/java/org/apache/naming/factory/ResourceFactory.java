

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
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.RefAddr;
import javax.naming.spi.ObjectFactory;
import org.apache.naming.ResourceRef;

/**
 * Object factory for Resources.
 * 
 * @author Remy Maucherat
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:15 $
 */

public class ResourceFactory
    implements ObjectFactory {


    // ----------------------------------------------------------- Constructors


    // -------------------------------------------------------------- Constants


    // ----------------------------------------------------- Instance Variables


    // --------------------------------------------------------- Public Methods


    // -------------------------------------------------- ObjectFactory Methods


    /**
     * Crete a new DataSource instance.
     * 
     * @param obj The reference object describing the DataSource
     */
    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                    Hashtable environment)
        throws Exception {
        
        if (obj instanceof ResourceRef) {
            Reference ref = (Reference) obj;
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
                        throw new NamingException(
                            "Could not create resource factory, ClassNotFoundException:" +
                            e.getMessage());
                    }
                } else {
                    try {
                        factoryClass = Class.forName(factoryClassName);
                    } catch(ClassNotFoundException e) {
                        throw new NamingException(
                            "Could not create resource factory, ClassNotFoundException:" +
                            e.getMessage());
                    }
                }
                if (factoryClass != null) {
                    try {
                        factory = (ObjectFactory) factoryClass.newInstance();
                    } catch(Throwable t) {
                        if( t instanceof NamingException)
                            throw (NamingException)t;
                        throw new NamingException(
                            "Could not create resource factory instance, " +
                            t.getMessage());
                    }
                }
            } else {
                if (ref.getClassName().equals("javax.sql.DataSource")) {
                    String javaxSqlDataSourceFactoryClassName =
                        System.getProperty("javax.sql.DataSource.Factory",
                                           Constants.DBCP_DATASOURCE_FACTORY);
                    try {
                        factory = (ObjectFactory) 
                            Class.forName(javaxSqlDataSourceFactoryClassName)
                            .newInstance();
                    } catch(Throwable t) {

                    }
                } else if (ref.getClassName().equals("javax.mail.Session")) {
                    String javaxMailSessionFactoryClassName =
                        System.getProperty("javax.mail.Session.Factory",
                                           "org.apache.naming.factory.MailSessionFactory");
                    try {
                        factory = (ObjectFactory) 
                            Class.forName(javaxMailSessionFactoryClassName)
                            .newInstance();
                    } catch(Throwable t) {
                    }
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

