

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


package org.apache.catalina.connector;


import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Locale;
import java.net.Socket;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.RequestDispatcher;
import org.apache.catalina.Request;
import org.apache.catalina.util.StringManager;


/**
 * Facade class that wraps a Catalina-internal <b>Request</b>
 * object.  All methods are delegated to the wrapped request.
 *
 * @author Craig R. McClanahan
 * @author Remy Maucherat
 * @author Jean-Francois Arcand
 * @version $Revision: 1.2 $ $Date: 2005/12/08 01:27:29 $
 */

public class RequestFacade implements ServletRequest {


    // ----------------------------------------------------------- Constants


    /**
     * The string manager for this package.
     */
    private static final StringManager sm =
        StringManager.getManager(Constants.Package);


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a wrapper for the specified request.
     *
     * @param request The request to be wrapped
     */
    public RequestFacade(Request request) {

        super();
        this.request = (ServletRequest) request;

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The wrapped request.
     */
    protected ServletRequest request = null;
    

    // --------------------------------------------------------- Public Methods


    /**
     * Clear facade.
     */
    public void clear() {
        request = null;
    }


    // ------------------------------------------------- ServletRequest Methods


    public Object getAttribute(String name) {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getAttribute(name);
    }


    public Enumeration getAttributeNames() {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getAttributeNames();
    }


    public String getCharacterEncoding() {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getCharacterEncoding();
    }


    public void setCharacterEncoding(String env)
        throws java.io.UnsupportedEncodingException {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        request.setCharacterEncoding(env);
    }


    public int getContentLength() {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getContentLength();
    }


    public String getContentType() {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getContentType();
    }


    public ServletInputStream getInputStream()
        throws IOException {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getInputStream();
    }


    public String getParameter(String name) {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getParameter(name);
    }


    public Enumeration getParameterNames() {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getParameterNames();
    }


    public String[] getParameterValues(String name) {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getParameterValues(name);
    }


    public Map getParameterMap() {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getParameterMap();
    }


    public String getProtocol() {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getProtocol();
    }


    public String getScheme() {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getScheme();
    }


    public String getServerName() {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getServerName();
    }


    public int getServerPort() {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getServerPort();
    }


    public BufferedReader getReader()
        throws IOException {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getReader();
    }


    public String getRemoteAddr() {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getRemoteAddr();
    }


    public String getRemoteHost() {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getRemoteHost();
    }


    public void setAttribute(String name, Object o) {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        request.setAttribute(name, o);
    }


    public void removeAttribute(String name) {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        request.removeAttribute(name);
    }


    public Locale getLocale() {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getLocale();
    }


    public Enumeration getLocales() {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getLocales();
    }


    public boolean isSecure() {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.isSecure();
    }


    public RequestDispatcher getRequestDispatcher(String path) {
        // TODO : Facade !!
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getRequestDispatcher(path);
    }


    public String getRealPath(String path) {
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getRealPath(path);
    }

    /**
     * Returns the Internet Protocol (IP) source port of the client
     * or last proxy that sent the request.
     */    
    public int getRemotePort(){
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getRemotePort();
    }


    /**
     * Returns the host name of the Internet Protocol (IP) interface on
     * which the request was received.
     */
    public String getLocalName(){
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getLocalName();
    }

    /**
     * Returns the Internet Protocol (IP) address of the interface on
     * which the request  was received.
     */       
    public String getLocalAddr(){
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getLocalAddr();
    }

    
    /**
     * Returns the Internet Protocol (IP) port number of the interface
     * on which the request was received.
     */
    public int getLocalPort(){
        // Disallow operation if the object has gone out of scope
        if (request == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        return request.getLocalPort();
    }
}
