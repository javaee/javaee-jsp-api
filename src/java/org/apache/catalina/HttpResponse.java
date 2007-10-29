

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


import javax.servlet.http.Cookie;


/**
 * An <b>HttpResponse</b> is the Catalina-internal facade for an
 * <code>HttpServletResponse</code> that is to be produced,
 * based on the processing of a corresponding <code>HttpRequest</code>.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:01 $
 */

public interface HttpResponse
    extends Response {


    // --------------------------------------------------------- Public Methods


    /**
     * Return an array of all cookies set for this response, or
     * a zero-length array if no cookies have been set.
     */
    public Cookie[] getCookies();


    /**
     * Return the value for the specified header, or <code>null</code> if this
     * header has not been set.  If more than one value was added for this
     * name, only the first is returned; use getHeaderValues() to retrieve all
     * of them.
     *
     * @param name Header name to look up
     */
    public String getHeader(String name);


    /**
     * Return an array of all the header names set for this response, or
     * a zero-length array if no headers have been set.
     */
    public String[] getHeaderNames();


    /**
     * Return an array of all the header values associated with the
     * specified header name, or an zero-length array if there are no such
     * header values.
     *
     * @param name Header name to look up
     */
    public String[] getHeaderValues(String name);


    /**
     * Return the error message that was set with <code>sendError()</code>
     * for this Response.
     */
    public String getMessage();


    /**
     * Return the HTTP status code associated with this Response.
     */
    public int getStatus();


    /**
     * Reset this response, and specify the values for the HTTP status code
     * and corresponding message.
     *
     * @exception IllegalStateException if this response has already been
     *  committed
     */
    public void reset(int status, String message);


}
