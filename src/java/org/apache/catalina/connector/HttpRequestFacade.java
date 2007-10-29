

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


import java.security.Principal;
import java.util.Locale;
import java.util.Enumeration;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.session.StandardSessionFacade;


/**
 * Facade class that wraps a Catalina-internal <b>HttpRequest</b>
 * object.  All methods are delegated to the wrapped request.
 *
 * @author Remy Maucherat
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:04 $
 */

public final class HttpRequestFacade
    extends RequestFacade
    implements HttpServletRequest {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a wrapper for the specified request.
     *
     * @param request The request to be wrapped
     */
    public HttpRequestFacade(HttpRequest request) {
        super(request);
    }


    // --------------------------------------------- HttpServletRequest Methods


    public String getAuthType() {
        return ((HttpServletRequest) request).getAuthType();
    }


    public Cookie[] getCookies() {
        return ((HttpServletRequest) request).getCookies();
    }


    public long getDateHeader(String name) {
        return ((HttpServletRequest) request).getDateHeader(name);
    }


    public String getHeader(String name) {
        return ((HttpServletRequest) request).getHeader(name);
    }


    public Enumeration getHeaders(String name) {
        return ((HttpServletRequest) request).getHeaders(name);
    }


    public Enumeration getHeaderNames() {
        return ((HttpServletRequest) request).getHeaderNames();
    }


    public int getIntHeader(String name) {
        return ((HttpServletRequest) request).getIntHeader(name);
    }


    public String getMethod() {
        return ((HttpServletRequest) request).getMethod();
    }


    public String getPathInfo() {
        return ((HttpServletRequest) request).getPathInfo();
    }


    public String getPathTranslated() {
        return ((HttpServletRequest) request).getPathTranslated();
    }


    public String getContextPath() {
        return ((HttpServletRequest) request).getContextPath();
    }


    public String getQueryString() {
        return ((HttpServletRequest) request).getQueryString();
    }


    public String getRemoteUser() {
        return ((HttpServletRequest) request).getRemoteUser();
    }


    public boolean isUserInRole(String role) {
        return ((HttpServletRequest) request).isUserInRole(role);
    }


    public java.security.Principal getUserPrincipal() {
        return ((HttpServletRequest) request).getUserPrincipal();
    }


    public String getRequestedSessionId() {
        return ((HttpServletRequest) request).getRequestedSessionId();
    }


    public String getRequestURI() {
        return ((HttpServletRequest) request).getRequestURI();
    }


    public StringBuffer getRequestURL() {
        return ((HttpServletRequest) request).getRequestURL();
    }


    public String getServletPath() {
        return ((HttpServletRequest) request).getServletPath();
    }


    public HttpSession getSession(boolean create) {
        HttpSession session =
            ((HttpServletRequest) request).getSession(create);
        if (session == null)
            return null;
        else
            return new StandardSessionFacade(session);
    }


    public HttpSession getSession() {
        return getSession(true);
    }


    public boolean isRequestedSessionIdValid() {
        return ((HttpServletRequest) request).isRequestedSessionIdValid();
    }


    public boolean isRequestedSessionIdFromCookie() {
        return ((HttpServletRequest) request).isRequestedSessionIdFromCookie();
    }


    public boolean isRequestedSessionIdFromURL() {
        return ((HttpServletRequest) request).isRequestedSessionIdFromURL();
    }


    public boolean isRequestedSessionIdFromUrl() {
        return ((HttpServletRequest) request).isRequestedSessionIdFromURL();
    }


}
