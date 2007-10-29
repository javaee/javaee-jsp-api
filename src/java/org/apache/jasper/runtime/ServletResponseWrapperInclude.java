

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

import java.lang.IllegalStateException;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.JspWriter;

/**
 * ServletResponseWrapper used by the JSP 'include' action.
 *
 * This wrapper response object is passed to RequestDispatcher.include(), so
 * that the output of the included resource is appended to that of the
 * including page.
 *
 * @author Pierre Delisle
 */

public class ServletResponseWrapperInclude extends HttpServletResponseWrapper {

    /**
     * PrintWriter which appends to the JspWriter of the including page.
     */
    private PrintWriter printWriter;

    private JspWriter jspWriter;

    public ServletResponseWrapperInclude(ServletResponse response, 
					 JspWriter jspWriter) {
	super((HttpServletResponse)response);
	this.printWriter = new PrintWriter(jspWriter);
	this.jspWriter = jspWriter;
    }

    /**
     * Returns a wrapper around the JspWriter of the including page.
     */
    public PrintWriter getWriter() throws IOException {
	return printWriter;
    }

    public ServletOutputStream getOutputStream() throws IOException {
	throw new IllegalStateException();
    }

    /**
     * Clears the output buffer of the JspWriter associated with the including
     * page.
     */
    public void resetBuffer() {
	try {
	    jspWriter.clearBuffer();
	} catch (IOException ioe) {
	}
    }
}
