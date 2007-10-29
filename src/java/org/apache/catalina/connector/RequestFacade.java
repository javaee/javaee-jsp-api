

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


/**
 * Facade class that wraps a Catalina-internal <b>Request</b>
 * object.  All methods are delegated to the wrapped request.
 *
 * @author Craig R. McClanahan
 * @author Remy Maucherat
 * @author Jean-Francois Arcand
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:04 $
 */

public class RequestFacade implements ServletRequest {


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
        return request.getAttribute(name);
    }


    public Enumeration getAttributeNames() {
        return request.getAttributeNames();
    }


    public String getCharacterEncoding() {
        return request.getCharacterEncoding();
    }


    public void setCharacterEncoding(String env)
        throws java.io.UnsupportedEncodingException {
        request.setCharacterEncoding(env);
    }


    public int getContentLength() {
        return request.getContentLength();
    }


    public String getContentType() {
        return request.getContentType();
    }


    public ServletInputStream getInputStream()
        throws IOException {
        return request.getInputStream();
    }


    public String getParameter(String name) {
        return request.getParameter(name);
    }


    public Enumeration getParameterNames() {
        return request.getParameterNames();
    }


    public String[] getParameterValues(String name) {
        return request.getParameterValues(name);
    }


    public Map getParameterMap() {
        return request.getParameterMap();
    }


    public String getProtocol() {
        return request.getProtocol();
    }


    public String getScheme() {
        return request.getScheme();
    }


    public String getServerName() {
        return request.getServerName();
    }


    public int getServerPort() {
        return request.getServerPort();
    }


    public BufferedReader getReader()
        throws IOException {
        return request.getReader();
    }


    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }


    public String getRemoteHost() {
        return request.getRemoteHost();
    }


    public void setAttribute(String name, Object o) {
        request.setAttribute(name, o);
    }


    public void removeAttribute(String name) {
        request.removeAttribute(name);
    }


    public Locale getLocale() {
        return request.getLocale();
    }


    public Enumeration getLocales() {
        return request.getLocales();
    }


    public boolean isSecure() {
        return request.isSecure();
    }


    public RequestDispatcher getRequestDispatcher(String path) {
        // TODO : Facade !!
        return request.getRequestDispatcher(path);
    }


    public String getRealPath(String path) {
        return request.getRealPath(path);
    }

    /**
     * Returns the Internet Protocol (IP) source port of the client
     * or last proxy that sent the request.
     */    
    public int getRemotePort(){
        return request.getRemotePort();
    }


    /**
     * Returns the host name of the Internet Protocol (IP) interface on
     * which the request was received.
     */
    public String getLocalName(){
        return request.getLocalName();
    }

    /**
     * Returns the Internet Protocol (IP) address of the interface on
     * which the request  was received.
     */       
    public String getLocalAddr(){
        return request.getLocalAddr();
    }

    
    /**
     * Returns the Internet Protocol (IP) port number of the interface
     * on which the request was received.
     */
    public int getLocalPort(){
        return request.getLocalPort();
    }
}
