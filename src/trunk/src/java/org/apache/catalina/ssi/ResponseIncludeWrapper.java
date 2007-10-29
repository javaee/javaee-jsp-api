/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * @version $Revision: 1.2 $, $Date: 2005/04/29 01:28:02 $
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
