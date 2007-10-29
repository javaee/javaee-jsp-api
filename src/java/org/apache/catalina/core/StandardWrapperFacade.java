

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


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletResponse;


/**
 * Facade for the <b>StandardWrapper</b> object.
 *
 * @author Remy Maucharat
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:03 $
 */

public final class StandardWrapperFacade
    implements ServletConfig {


    // ----------------------------------------------------------- Constructors


    /**
     * Create a new facede around a StandardWrapper.
     */
    public StandardWrapperFacade(StandardWrapper config) {

        super();
        this.config = (ServletConfig) config;

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * Wrapped config.
     */
    private ServletConfig config = null;


    // -------------------------------------------------- ServletConfig Methods


    public String getServletName() {
        return config.getServletName();
    }


    public ServletContext getServletContext() {
        ServletContext theContext = config.getServletContext();
        if ((theContext != null) &&
            (theContext instanceof ApplicationContext))
            theContext = ((ApplicationContext) theContext).getFacade();
        return (theContext);
    }


    public String getInitParameter(String name) {
        return config.getInitParameter(name);
    }


    public Enumeration getInitParameterNames() {
        return config.getInitParameterNames();
    }


}
