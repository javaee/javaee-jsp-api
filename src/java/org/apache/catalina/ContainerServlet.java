

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


/**
 * A <b>ContainerServlet</b> is a servlet that has access to Catalina
 * internal functionality, and is loaded from the Catalina class loader
 * instead of the web application class loader.  The property setter
 * methods must be called by the container whenever a new instance of
 * this servlet is put into service.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:01 $
 */

public interface ContainerServlet {


    // ------------------------------------------------------------- Properties


    /**
     * Return the Wrapper with which this Servlet is associated.
     */
    public Wrapper getWrapper();


    /**
     * Set the Wrapper with which this Servlet is associated.
     *
     * @param wrapper The new associated Wrapper
     */
    public void setWrapper(Wrapper wrapper);


}
