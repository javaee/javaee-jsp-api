

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
package org.apache.coyote.tomcat5;

/**
 * Constants.
 *
 * @author Remy Maucherat
 */
public final class Constants {


    // -------------------------------------------------------------- Constants


    public static final String Package = "org.apache.coyote.tomcat5";
    public static final int DEFAULT_CONNECTION_LINGER = -1;
    public static final int DEFAULT_CONNECTION_TIMEOUT = 60000;
    public static final int DEFAULT_CONNECTION_UPLOAD_TIMEOUT = 300000;
    public static final int DEFAULT_SERVER_SOCKET_TIMEOUT = 0;

    public static final int PROCESSOR_IDLE = 0;
    public static final int PROCESSOR_ACTIVE = 1;

    /**
     * Default header names.
     */
    public static final String AUTHORIZATION_HEADER = "authorization";

    /**
     * SSL Certificate Request Attributite.
     */
    public static final String SSL_CERTIFICATE_ATTR = "org.apache.coyote.request.X509Certificate";

    /**
     * Security flag.
     */
    public static final boolean SECURITY = 
        (System.getSecurityManager() != null);

    
    // S1AS 4703023
    public static final int DEFAULT_MAX_DISPATCH_DEPTH = 20;

    
    // START SJSAS 6328909
    /**
     * The default response-type
     */
    public final static String DEFAULT_RESPONSE_TYPE = 
            "text/html; charset=iso-8859-1";


    /**
     * The forced response-type
     */
    public final static String FORCED_RESPONSE_TYPE = 
           "text/html; charset=iso-8859-1";
    // END SJSAS 6328909


    // START SJSAS 6337561
    public final static String PROXY_JROUTE = "proxy-jroute";
    // END SJSAS 6337561

    // START SJSAS 6346226
    public final static String JROUTE_COOKIE = "JROUTE";
    // END SJSAS 6346226
    
}
