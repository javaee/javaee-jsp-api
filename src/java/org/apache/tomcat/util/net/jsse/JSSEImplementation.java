

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

package org.apache.tomcat.util.net.jsse;

import org.apache.tomcat.util.compat.JdkCompat;
import org.apache.tomcat.util.net.SSLImplementation;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.ServerSocketFactory;
import java.io.*;
import java.net.*;

/* JSSEImplementation:

   Concrete implementation class for JSSE

   @author EKR
*/
        
public class JSSEImplementation extends SSLImplementation
{
    static final String JSSE14Factory = 
        "org.apache.tomcat.util.net.jsse.JSSE14Factory";
    static final String JSSE13Factory = 
        "org.apache.tomcat.util.net.jsse.JSSE13Support";
    static final String SSLSocketClass = "javax.net.ssl.SSLSocket";

    static org.apache.commons.logging.Log logger = 
        org.apache.commons.logging.LogFactory.getLog(JSSEImplementation.class);

    private JSSEFactory factory;

    public JSSEImplementation() throws ClassNotFoundException {
        // Check to see if JSSE is floating around somewhere
        Class.forName(SSLSocketClass);
        if( JdkCompat.isJava14() ) {
            try {
                Class factcl = Class.forName(JSSE14Factory);
                factory = (JSSEFactory)factcl.newInstance();
            } catch(Exception ex) {
                factory = new JSSE13Factory();
                if(logger.isDebugEnabled()) {
                    logger.debug("Error getting factory: " + JSSE14Factory, ex);
                }
            }
        } else {
            factory = new JSSE13Factory();
        }
    }


    public String getImplementationName(){
      return "JSSE";
    }
      
    public ServerSocketFactory getServerSocketFactory()  {
        ServerSocketFactory ssf = factory.getSocketFactory();
        return ssf;
    } 

    public SSLSupport getSSLSupport(Socket s) {
        SSLSupport ssls = factory.getSSLSupport(s);
        return ssls;
    }
}
