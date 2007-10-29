

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

package org.apache.tomcat.util.net;

import java.io.*;
import java.net.*;

/* SSLImplementation:

   Abstract factory and base class for all SSL implementations.

   @author EKR
*/
abstract public class SSLImplementation {
    private static com.sun.org.apache.commons.logging.Log logger =
        com.sun.org.apache.commons.logging.LogFactory.getLog(SSLImplementation.class);

    // The default implementations in our search path
    private static final String PureTLSImplementationClass=
	"org.apache.tomcat.util.net.puretls.PureTLSImplementation";
    private static final String JSSEImplementationClass=
	"org.apache.tomcat.util.net.jsse.JSSEImplementation";
    
    private static final String[] implementations=
    {
        PureTLSImplementationClass,
        JSSEImplementationClass
    };

    public static SSLImplementation getInstance() throws ClassNotFoundException
    {
	for(int i=0;i<implementations.length;i++){
	    try {
               SSLImplementation impl=
		    getInstance(implementations[i]);
		return impl;
	    } catch (Exception e) {
		if(logger.isTraceEnabled()) 
		    logger.trace("Error creating " + implementations[i],e);
	    }
	}

	// If we can't instantiate any of these
	throw new ClassNotFoundException("Can't find any SSL implementation");
    }

    public static SSLImplementation getInstance(String className)
	throws ClassNotFoundException
    {
	if(className==null) return getInstance();

	try {
	    // Workaround for the J2SE 1.4.x classloading problem (under Solaris).
	    // Class.forName(..) fails without creating class using new.
	    // This is an ugly workaround. 
	    if( JSSEImplementationClass.equals(className) ) {
		return new org.apache.tomcat.util.net.jsse.JSSEImplementation();
	    }
	    Class clazz=Class.forName(className);
	    return (SSLImplementation)clazz.newInstance();
	} catch (Exception e){
	    if(logger.isDebugEnabled())
		logger.debug("Error loading SSL Implementation "
			     +className, e);
	    throw new ClassNotFoundException("Error loading SSL Implementation "
				      +className+ " :" +e.toString());
	}
    }

    abstract public String getImplementationName();
    abstract public ServerSocketFactory getServerSocketFactory();
    abstract public SSLSupport getSSLSupport(Socket sock);
}    
