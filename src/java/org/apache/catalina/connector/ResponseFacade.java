

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


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import org.apache.catalina.Response;


/**
 * Facade class that wraps a Catalina-internal <b>Response</b>
 * object.  All methods are delegated to the wrapped response.
 *
 * @author Remy Maucherat
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:04 $
 */

public class ResponseFacade implements ServletResponse {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a wrapper for the specified response.
     *
     * @param response The response to be wrapped
     */
    public ResponseFacade(Response response) {
        this.resp = response;
        this.response = (ServletResponse) response;
    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The wrapped response.
     */
    protected ServletResponse response = null;


    /**
     * The wrapped response.
     */
    protected Response resp = null;


    // --------------------------------------------------------- Public Methods


    /**
     * Clear facade.
     */
    public void clear() {
        response = null;
        resp = null;
    }


    public void finish() {

        resp.setSuspended(true);

    }


    public boolean isFinished() {

        return resp.isSuspended();

    }


    // ------------------------------------------------ ServletResponse Methods


    public String getCharacterEncoding() {
        return response.getCharacterEncoding();
    }


    public ServletOutputStream getOutputStream()
        throws IOException {

        //        if (isFinished())
        //            throw new IllegalStateException
        //                (/*sm.getString("responseFacade.finished")*/);

        ServletOutputStream sos = response.getOutputStream();
        if (isFinished())
            resp.setSuspended(true);
        return (sos);

    }


    public PrintWriter getWriter()
        throws IOException {

        //        if (isFinished())
        //            throw new IllegalStateException
        //                (/*sm.getString("responseFacade.finished")*/);

        PrintWriter writer = response.getWriter();
        if (isFinished())
            resp.setSuspended(true);
        return (writer);

    }


    public void setContentLength(int len) {

        if (isCommitted())
            return;

        response.setContentLength(len);

    }

    public void setCharacterEncoding(String charset) {

        if (isCommitted())
            return;

        response.setCharacterEncoding(charset);

    }


    public void setContentType(String type) {

        if (isCommitted())
            return;

        response.setContentType(type);

    }

    public String getContentType() {

        return response.getContentType();

    }

    public void setBufferSize(int size) {

        if (isCommitted())
            throw new IllegalStateException
                (/*sm.getString("responseBase.reset.ise")*/);

        response.setBufferSize(size);

    }


    public int getBufferSize() {
        return response.getBufferSize();
    }


    public void flushBuffer()
        throws IOException {

        if (isFinished())
            //            throw new IllegalStateException
            //                (/*sm.getString("responseFacade.finished")*/);
            return;

        resp.setAppCommitted(true);

        try {
            response.flushBuffer();
        } catch(IOException ioe) {
            // An IOException on a write is almost always due to
            // the remote client aborting the request.  Wrap this
            // so that it can be handled better by the error dispatcher.
            throw new ClientAbortException(ioe);
        }
    }


    public void resetBuffer() {

        if (isCommitted())
            throw new IllegalStateException
                (/*sm.getString("responseBase.reset.ise")*/);

        response.resetBuffer();

    }


    public boolean isCommitted() {
        return (resp.isAppCommitted());
    }


    public void reset() {

        if (isCommitted())
            throw new IllegalStateException
                (/*sm.getString("responseBase.reset.ise")*/);

        response.reset();

    }


    public void setLocale(Locale loc) {

        if (isCommitted())
            return;

        response.setLocale(loc);
    }


    public Locale getLocale() {
        return response.getLocale();
    }


}
