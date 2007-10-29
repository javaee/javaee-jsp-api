/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
}
