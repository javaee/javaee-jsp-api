

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


package org.apache.catalina.core;


import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Container;
import org.apache.catalina.Host;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.util.StringManager;
import org.apache.catalina.valves.ValveBase;


/**
 * Valve that implements the default basic behavior for the
 * <code>StandardEngine</code> container implementation.
 * <p>
 * <b>USAGE CONSTRAINT</b>:  This implementation is likely to be useful only
 * when processing HTTP requests.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:03 $
 */

final class StandardEngineValve
    extends ValveBase {


    // ----------------------------------------------------- Instance Variables


    /**
     * The descriptive information related to this implementation.
     */
    private static final String info =
        "org.apache.catalina.core.StandardEngineValve/1.0";


    /**
     * The string manager for this package.
     */
    private static final StringManager sm =
        StringManager.getManager(Constants.Package);


    // ------------------------------------------------------------- Properties


    /**
     * Return descriptive information about this Valve implementation.
     */
    public String getInfo() {

        return (info);

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Select the appropriate child Host to process this request,
     * based on the requested server name.  If no matching Host can
     * be found, return an appropriate HTTP error.
     *
     * @param request Request to be processed
     * @param response Response to be produced
     * @param valveContext Valve context used to forward to the next Valve
     *
     * @exception IOException if an input/output error occurred
     * @exception ServletException if a servlet error occurred
     */
    /** IASRI 4665318
    public void invoke(Request request, Response response,
                       ValveContext valveContext)
        throws IOException, ServletException {
    */
    // START OF IASRI 4665318
    public int invoke(Request request, Response response)
        throws IOException, ServletException {
    // END OF IASRI 4665318

        // Select the Host to be used for this Request
        Host host = request.getHost();
        if (host == null) {
            /* S1AS 4878272
            ((HttpServletResponse) response.getResponse()).sendError
                (HttpServletResponse.SC_BAD_REQUEST,
                 sm.getString("standardEngine.noHost",
                              request.getRequest().getServerName()));
            */
            // BEGIN S1AS 4878272
            ((HttpServletResponse) response.getResponse()).sendError
                (HttpServletResponse.SC_BAD_REQUEST);
            response.setDetailMessage(
                 sm.getString("standardEngine.noHost",
                              request.getRequest().getServerName()));
            // END S1AS 4878272
            // START OF IASRI 4665318
            // return;     
            return END_PIPELINE;
            // END OF IASRI 4665318
        }

        // START OF IASRI 4665318
        host.invoke(request, response);
        return END_PIPELINE;
        // END OF IASRI 4665318

    }

}
