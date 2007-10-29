

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

package org.apache.jasper.runtime;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

import org.apache.jasper.JasperException;
import org.apache.jasper.compiler.Localizer;

/**
 * This is the super class of all JSP-generated servlets.
 *
 * @author Anil K. Vijendran
 */
public abstract class HttpJspBase 
    extends HttpServlet 
    implements HttpJspPage 
{

    private static com.sun.org.apache.commons.logging.Log log=
        com.sun.org.apache.commons.logging.LogFactory.getLog( HttpJspBase.class );

    static {
        if( JspFactory.getDefaultFactory() == null ) {
            JspFactoryImpl factory = new JspFactoryImpl();
            if( System.getSecurityManager() != null ) {
                String basePackage = "org.apache.jasper.";
                try {
                    factory.getClass().getClassLoader().loadClass( basePackage +
                                                                   "runtime.JspFactoryImpl$PrivilegedGetPageContext");
                    factory.getClass().getClassLoader().loadClass( basePackage +
                                                                   "runtime.JspFactoryImpl$PrivilegedReleasePageContext");
                    factory.getClass().getClassLoader().loadClass( basePackage +
                                                                   "runtime.JspRuntimeLibrary");
                    factory.getClass().getClassLoader().loadClass( basePackage +
                                                                   "runtime.JspRuntimeLibrary$PrivilegedIntrospectHelper");
                    factory.getClass().getClassLoader().loadClass( basePackage +
                                                                   "runtime.ServletResponseWrapperInclude");
                    factory.getClass().getClassLoader().loadClass( basePackage +
                                                                   "servlet.JspServletWrapper");
                } catch (ClassNotFoundException ex) {
                    log.error("Jasper JspRuntimeContext preload of class failed: " +
                                       ex.getMessage(), ex);
                }
            }
            JspFactory.setDefaultFactory(factory);
        }
    }

    protected HttpJspBase() {
    }

    public final void init(ServletConfig config) 
	throws ServletException 
    {
        super.init(config);
	jspInit();
        _jspInit();
    }
    
    public String getServletInfo() {
	return Localizer.getMessage("jsp.engine.info");
    }

    public final void destroy() {
	jspDestroy();
	_jspDestroy();
    }

    /**
     * Entry point into service.
     */
    public final void service(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException 
    {
        _jspService(request, response);
    }
    
    public void jspInit() {
    }

    public void _jspInit() {
    }

    public void jspDestroy() {
    }

    protected void _jspDestroy() {
    }

    public abstract void _jspService(HttpServletRequest request, 
				     HttpServletResponse response) 
	throws ServletException, IOException;
}
