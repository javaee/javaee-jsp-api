

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


package org.apache.catalina;

import org.apache.catalina.core.StandardServer;


/**
 * <p><strong>ServerFactory</strong> allows the registration of the
 * (singleton) <code>Server</code> instance for this JVM, so that it
 * can be accessed independently of any existing reference to the
 * component hierarchy.  This is important for administration tools
 * that are built around the internal component implementation classes.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:01 $
 */

public class ServerFactory {


    // ------------------------------------------------------- Static Variables


    /**
     * The singleton <code>Server</code> instance for this JVM.
     */
    private static Server server = null;


    // --------------------------------------------------------- Public Methods


    /**
     * Return the singleton <code>Server</code> instance for this JVM.
     */
    public static Server getServer() {
        if( server==null )
            server=new StandardServer();
        return (server);

    }


    /**
     * Set the singleton <code>Server</code> instance for this JVM.  This
     * method must <strong>only</strong> be called from a constructor of
     * the (singleton) <code>Server</code> instance that is created for
     * this execution of Catalina.
     *
     * @param theServer The new singleton instance
     */
    public static void setServer(Server theServer) {

        if (server == null)
            server = theServer;

    }


}
