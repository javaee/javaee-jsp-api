

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


package org.apache.catalina.util;


import java.io.InputStream;
import java.util.Properties;


/**
 * Simple utility module to make it easy to plug in the server identifier
 * when integrating Tomcat.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.2 $ $Date: 2005/08/04 19:59:59 $
 */

public class ServerInfo {


    // ------------------------------------------------------- Static Variables


    /**
     * The server information String with which we identify ourselves.
     */
    private static String serverInfo = null;

    static {

        // BEGIN S1AS 5022949
        /*
        try {
            InputStream is = ServerInfo.class.getResourceAsStream
                ("/org/apache/catalina/util/ServerInfo.properties");
            Properties props = new Properties();
            props.load(is);
            is.close();
            serverInfo = props.getProperty("server.info");
        } catch (Throwable t) {
            ;
        }
        if (serverInfo == null)
            serverInfo = "Apache Tomcat";
        */
        // END S1AS 5022949
    }


    // --------------------------------------------------------- Public Methods


    // START PWC 5022949
    public static void setServerInfo(String info) {
        serverInfo = info;
    }
    // END PWC 5022949

    /**
     * Return the server identification for this version of Tomcat.
     */
    public static String getServerInfo() {

        return (serverInfo);

    }


}
