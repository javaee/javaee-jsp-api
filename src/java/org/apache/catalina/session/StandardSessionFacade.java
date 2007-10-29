

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


package org.apache.catalina.session;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;


/**
 * Facade for the StandardSession object.
 *
 * @author Remy Maucherat
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:07 $
 */

public class StandardSessionFacade
    implements HttpSession {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new session facade.
     */
    public StandardSessionFacade(StandardSession session) {
        super();
        this.session = (HttpSession) session;
    }


    /**
     * Construct a new session facade.
     */
    public StandardSessionFacade(HttpSession session) {
        super();
        this.session = session;
    }


    // ----------------------------------------------------- Instance Variables


    /**
     * Wrapped session object.
     */
    private HttpSession session = null;


    // ---------------------------------------------------- HttpSession Methods


    public long getCreationTime() {
        return session.getCreationTime();
    }


    public String getId() {
        return session.getId();
    }


    public long getLastAccessedTime() {
        return session.getLastAccessedTime();
    }


    public ServletContext getServletContext() {
        // FIXME : Facade this object ?
        return session.getServletContext();
    }


    public void setMaxInactiveInterval(int interval) {
        session.setMaxInactiveInterval(interval);
    }


    public int getMaxInactiveInterval() {
        return session.getMaxInactiveInterval();
    }


    public HttpSessionContext getSessionContext() {
        return session.getSessionContext();
    }


    public Object getAttribute(String name) {
        return session.getAttribute(name);
    }


    public Object getValue(String name) {
        return session.getAttribute(name);
    }


    public Enumeration getAttributeNames() {
        return session.getAttributeNames();
    }


    public String[] getValueNames() {
        return session.getValueNames();
    }


    public void setAttribute(String name, Object value) {
        session.setAttribute(name, value);
    }


    public void putValue(String name, Object value) {
        session.setAttribute(name, value);
    }


    public void removeAttribute(String name) {
        session.removeAttribute(name);
    }


    public void removeValue(String name) {
        session.removeAttribute(name);
    }


    public void invalidate() {
        session.invalidate();
    }


    public boolean isNew() {
        return session.isNew();
    }


}
