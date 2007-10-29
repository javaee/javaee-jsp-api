

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

package org.apache.naming;

/**
 * Naming MBean interface.
 * 
 * @author <a href="mailto:remm@apache.org">Remy Maucherat</a>
 * @version $Revision: 1.1.1.1 $
 */

public interface NamingServiceMBean {
    
    
    // -------------------------------------------------------------- Constants
    
    
    /**
     * Status constants.
     */
    public static final String[] states = 
    {"Stopped", "Stopping", "Starting", "Started"};
    
    
    public static final int STOPPED  = 0;
    public static final int STOPPING = 1;
    public static final int STARTING = 2;
    public static final int STARTED  = 3;
    
    
    /**
     * Component name.
     */
    public static final String NAME = "Apache JNDI Naming Service";
    
    
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = ":service=Naming";
    
    
    // ------------------------------------------------------ Interface Methods
    
    
    /**
     * Retruns the JNDI component name.
     */
    public String getName();
    
    
    /**
     * Returns the state.
     */
    public int getState();
    
    
    /**
     * Returns a String representation of the state.
     */
    public String getStateString();
    
    
    /**
     * Start the servlet container.
     */
    public void start()
        throws Exception;
    
    
    /**
     * Stop the servlet container.
     */
    public void stop();
    
    
    /**
     * Destroy servlet container (if any is running).
     */
    public void destroy();
    
    
}
