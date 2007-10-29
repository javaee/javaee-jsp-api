

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
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import org.apache.catalina.Response;
import org.apache.catalina.connector.ResponseFacade;
import org.apache.catalina.util.StringManager;


/**
 * Wrapper around a <code>javax.servlet.ServletResponse</code>
 * that transforms an application response object (which might be the original
 * one passed to a servlet, or might be based on the 2.3
 * <code>javax.servlet.ServletResponseWrapper</code> class)
 * back into an internal <code>org.apache.catalina.Response</code>.
 * <p>
 * <strong>WARNING</strong>:  Due to Java's lack of support for multiple
 * inheritance, all of the logic in <code>ApplicationResponse</code> is
 * duplicated in <code>ApplicationHttpResponse</code>.  Make sure that you
 * keep these two classes in synchronization when making changes!
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.2 $ $Date: 2005/09/12 23:29:01 $
 */

/** START OF PWC 4858179
class ApplicationResponse extends ServletResponseWrapper {
**/
// START OF PWC 4858179
public class ApplicationResponse extends ServletResponseWrapper {
// END OF PWC 4858179


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new wrapped response around the specified servlet response.
     *
     * @param response The servlet response being wrapped
     */
    public ApplicationResponse(ServletResponse response) {

        this(response, false);

    }


    /**
     * Construct a new wrapped response around the specified servlet response.
     *
     * @param response The servlet response being wrapped
     * @param included <code>true</code> if this response is being processed
     *  by a <code>RequestDispatcher.include()</code> call
     */
    public ApplicationResponse(ServletResponse response, boolean included) {

        super(response);
        setIncluded(included);

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * Is this wrapped response the subject of an <code>include()</code>
     * call?
     */
    protected boolean included = false;


    /**
     * The string manager for this package.
     */
    protected static final StringManager sm =
        StringManager.getManager(Constants.Package);


    // ------------------------------------------------ ServletResponse Methods


    /**
     * Disallow <code>reset()</code> calls on a included response.
     *
     * @exception IllegalStateException if the response has already
     *  been committed
     */
    public void reset() {

        // If already committed, the wrapped response will throw ISE
        if (!included || getResponse().isCommitted())
            getResponse().reset();

    }


    /**
     * Disallow <code>setContentLength()</code> calls on an included response.
     *
     * @param len The new content length
     */
    public void setContentLength(int len) {

        if (!included)
            getResponse().setContentLength(len);

    }


    /**
     * Disallow <code>setContentType()</code> calls on an included response.
     *
     * @param type The new content type
     */
    public void setContentType(String type) {

        if (!included)
            getResponse().setContentType(type);

    }


    /**
     * Ignore <code>setLocale()</code> calls on an included response.
     *
     * @param loc The new locale
     */
    public void setLocale(Locale loc) {
        if (!included)
            getResponse().setLocale(loc);
    }


    /**
     * Ignore <code>setBufferSize()</code> calls on an included response.
     *
     * @param size The buffer size
     */
    public void setBufferSize(int size) {
        if (!included)
            getResponse().setBufferSize(size);
    }


    // ----------------------------------------- ServletResponseWrapper Methods


    /**
     * Set the response that we are wrapping.
     *
     * @param response The new wrapped response
     */
    public void setResponse(ServletResponse response) {

        super.setResponse(response);

    }


    // -------------------------------------------------------- Package Methods

    // START OF PWC 4858179
    /**
     * Return the included flag for this response.
     */
    public boolean isIncluded() {

        return (this.included);

    }
    
    /**
    public boolean isIncluded() {

        return (this.included);

    }
     */
    // END OF PWC 4858179


    /**
     * Set the included flag for this response.
     *
     * @param included The new included flag
     */
    void setIncluded(boolean included) {

        this.included = included;

    }


}
