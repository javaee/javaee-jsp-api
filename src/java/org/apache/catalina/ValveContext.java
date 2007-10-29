

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


import java.io.IOException;
import javax.servlet.ServletException;


/**
 * <p>A <b>ValveContext</b> is the mechanism by which a Valve can trigger the
 * execution of the next Valve in a Pipeline, without having to know anything
 * about the internal implementation mechanisms.  An instance of a class
 * implementing this interface is passed as a parameter to the
 * <code>Valve.invoke()</code> method of each executed Valve.</p>
 *
 * <p><strong>IMPLEMENTATION NOTE</strong>: It is up to the implementation of
 * ValveContext to ensure that simultaneous requests being processed (by
 * separate threads) through the same Pipeline do not interfere with each
 * other's flow of control.</p>
 *
 * @author Craig R. McClanahan
 * @author Gunnar Rjnning
 * @author Peter Donald
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:01 $
 */

public interface ValveContext {


    //-------------------------------------------------------------- Properties


    /**
     * Return descriptive information about this ValveContext implementation.
     */
    public String getInfo();


    //---------------------------------------------------------- Public Methods


    /**
     * Cause the <code>invoke()</code> method of the next Valve that is part of
     * the Pipeline currently being processed (if any) to be executed, passing
     * on the specified request and response objects plus this
     * <code>ValveContext</code> instance.  Exceptions thrown by a subsequently
     * executed Valve (or a Filter or Servlet at the application level) will be
     * passed on to our caller.
     *
     * If there are no more Valves to be executed, an appropriate
     * ServletException will be thrown by this ValveContext.
     *
     * @param request The request currently being processed
     * @param response The response currently being created
     *
     * @exception IOException if thrown by a subsequent Valve, Filter, or
     *  Servlet
     * @exception ServletException if thrown by a subsequent Valve, Filter,
     *  or Servlet
     * @exception ServletException if there are no further Valves configured
     *  in the Pipeline currently being processed
     */
    public void invokeNext(Request request, Response response)
        throws IOException, ServletException;


}
