

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


import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.catalina.Connector;
import org.apache.catalina.Context;
import org.apache.catalina.Request;
import org.apache.catalina.HttpResponse;


/**
 * Dummy response object, used for JSP precompilation.
 *
 * @author Remy Maucherat
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:02 $
 */

public class DummyResponse
    implements HttpResponse, HttpServletResponse {

    public DummyResponse() {
    }


    public void setAppCommitted(boolean appCommitted) {}
    public boolean isAppCommitted() { return false; }
    public Connector getConnector() { return null; }
    public void setConnector(Connector connector) {}
    public int getContentCount() { return -1; }
    public Context getContext() { return null; }
    public void setContext(Context context) {}
    public boolean getIncluded() { return false; }
    public void setIncluded(boolean included) {}
    public String getInfo() { return null; }
    public Request getRequest() { return null; }
    public void setRequest(Request request) {}
    public ServletResponse getResponse() { return null; }
    public OutputStream getStream() { return null; }
    public void setStream(OutputStream stream) {}
    public void setSuspended(boolean suspended) {}
    public boolean isSuspended() { return false; }
    public void setError() {}
    public boolean isError() { return false; }
    public ServletOutputStream createOutputStream() throws IOException {
        return null;
    }
    public void finishResponse() throws IOException {}
    public int getContentLength() { return -1; }
    public String getContentType() { return null; }
    public PrintWriter getReporter() { return null; }
    public void recycle() {}
    public void write(int b) throws IOException {}
    public void write(byte b[]) throws IOException {}
    public void write(byte b[], int off, int len) throws IOException {}
    public void flushBuffer() throws IOException {}
    public int getBufferSize() { return -1; }
    public String getCharacterEncoding() { return null; }
    public void setCharacterEncoding(String charEncoding) {}
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }
    public Locale getLocale() { return null; }
    public PrintWriter getWriter() throws IOException { return null; }
    public boolean isCommitted() { return false; }
    public void reset() {}
    public void resetBuffer() {}
    public void setBufferSize(int size) {}
    public void setContentLength(int length) {}
    public void setContentType(String type) {}
    public void setLocale(Locale locale) {}

    public Cookie[] getCookies() { return null; }
    public String getHeader(String name) { return null; }
    public String[] getHeaderNames() { return null; }
    public String[] getHeaderValues(String name) { return null; }
    public String getMessage() { return null; }
    public int getStatus() { return -1; }
    public void reset(int status, String message) {}
    public void addCookie(Cookie cookie) {}
    public void addDateHeader(String name, long value) {}
    public void addHeader(String name, String value) {}
    public void addIntHeader(String name, int value) {}
    public boolean containsHeader(String name) { return false; }
    public String encodeRedirectURL(String url) { return null; }
    public String encodeRedirectUrl(String url) { return null; }
    public String encodeURL(String url) { return null; }
    public String encodeUrl(String url) { return null; }
    public void sendAcknowledgement() throws IOException {}
    public void sendError(int status) throws IOException {}
    public void sendError(int status, String message) throws IOException {}
    public void sendRedirect(String location) throws IOException {}
    public void setDateHeader(String name, long value) {}
    public void setHeader(String name, String value) {}
    public void setIntHeader(String name, int value) {}
    public void setStatus(int status) {}
    public void setStatus(int status, String message) {}
    public void setDetailMessage(String msg) {}
    public String getDetailMessage() { return null; }


}
