

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

package org.apache.catalina.ssi;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * A HttpServletResponseWrapper, used from <code>SSIServletExternalResolver</code>
 *
 * @author Bip Thelin
 * @version $Revision: 1.1.1.1 $, $Date: 2005/05/27 22:55:07 $
 */
public class ResponseIncludeWrapper extends HttpServletResponseWrapper {

    /**
     * Our ServletOutputStream
     */
    protected ServletOutputStream originalServletOutputStream;
    protected ServletOutputStream servletOutputStream;
    protected PrintWriter printWriter;

    /**
     * Initialize our wrapper with the current HttpServletResponse
     * and ServletOutputStream.
     *
     * @param res The HttpServletResponse to use
     * @param out The ServletOutputStream' to use
     */
    public ResponseIncludeWrapper(HttpServletResponse res,
                                  ServletOutputStream originalServletOutputStream) {
        super(res);
        this.originalServletOutputStream = originalServletOutputStream;
    }

    /**
     * Flush the servletOutputStream or printWriter ( only one will be non-null )
     *
     * This must be called after a requestDispatcher.include, since we can't assume that
     * the included servlet flushed its stream.
     */
    public void flushOutputStreamOrWriter() throws IOException {
	if ( servletOutputStream != null ) {
	    servletOutputStream.flush();
	}
	if ( printWriter != null ) {
	    printWriter.flush();
	}
    }

    /**
     * Return a printwriter, throws and exception if a
     * OutputStream already been returned.
     *
     * @return a PrintWriter object
     * @exception java.io.IOException if the outputstream already been called
     */
    public PrintWriter getWriter() throws java.io.IOException {
        if ( servletOutputStream == null ) {
	    if ( printWriter == null ) {
		printWriter = new PrintWriter( originalServletOutputStream );
	    }
            return printWriter;
	}
	throw new IllegalStateException();
    }

    /**
     * Return a OutputStream, throws and exception if a
     * printwriter already been returned.
     *
     * @return a OutputStream object
     * @exception java.io.IOException if the printwriter already been called
     */
    public ServletOutputStream getOutputStream() throws java.io.IOException {
        if ( printWriter == null ) {
	    if ( servletOutputStream == null ) {
		servletOutputStream = originalServletOutputStream;
	    }
	    return servletOutputStream;
	}
	throw new IllegalStateException();
    }
}
