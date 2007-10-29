/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Copyright 1999-2001,2004 The Apache Software Foundation.
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


package org.apache.catalina.valves;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.compat.JdkCompat;

import org.apache.catalina.Container;
import org.apache.catalina.Globals;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.HttpResponse;
import org.apache.catalina.Logger;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Valve;
import org.apache.catalina.util.RequestUtil;
import org.apache.catalina.util.ServerInfo;
import org.apache.catalina.util.StringManager;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * <p>Implementation of a Valve that outputs HTML error pages.</p>
 *
 * <p>This Valve should be attached at the Host level, although it will work
 * if attached to a Context.</p>
 *
 * <p>HTML code from the Cocoon 2 project.</p>
 *
 * @author Remy Maucherat
 * @author Craig R. McClanahan
 * @author <a href="mailto:nicolaken@supereva.it">Nicola Ken Barozzi</a> Aisa
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @version $Revision: 1.2 $ $Date: 2005/07/26 01:56:30 $
 */

public class ErrorReportValve
    extends ValveBase {


    // ----------------------------------------------------- Instance Variables


    /**
     * The debugging detail level for this component.
     */
    private int debug = 0;


    /**
     * The descriptive information related to this implementation.
     */
    private static final String info =
        "org.apache.catalina.valves.ErrorReportValve/1.0";


    /**
     * The StringManager for this package.
     */
    protected static final StringManager sm =
        StringManager.getManager(Constants.Package);


    // ------------------------------------------------------------- Properties


    /**
     * Return descriptive information about this Valve implementation.
     */
    public String getInfo() {

        return (info);

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Invoke the next Valve in the sequence. When the invoke returns, check
     * the response state, and output an error report is necessary.
     *
     * @param request The servlet request to be processed
     * @param response The servlet response to be created
     * @param context The valve context used to invoke the next valve
     *  in the current processing pipeline
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
     /** IASRI 4665318
     public void invoke(Request request, Response response,
                        ValveContext context)
         throws IOException, ServletException {
     */
     // START OF IASRI 4665318
     public int invoke(Request request, Response response)
         throws IOException, ServletException {
     // END OF IASRI 4665318

        // Perform the request
        // START OF IASRI 4665318
        // context.invokeNext(request, response);
        return INVOKE_NEXT;

     }

     public void postInvoke(Request request, Response response)
         throws IOException, ServletException {
     // END OF IASRI 4665318

        ServletRequest sreq = (ServletRequest) request;
        Throwable throwable =
            (Throwable) sreq.getAttribute(Globals.EXCEPTION_ATTR);

        ServletResponse sresp = (ServletResponse) response;
        if (sresp.isCommitted()) {
            return;
        }

        if (throwable != null) {

            // The response is an error
            response.setError();

            // START PWC 6254469
            // Save (and later restore) the response encoding, because the
            // following call to reset() will reset it to the default
            // encoding (ISO-8859-1).
            String responseCharEnc = sresp.getCharacterEncoding();
            // END PWC 6254469

            // Reset the response (if possible)
            try {
                sresp.reset();
            } catch (IllegalStateException e) {
                ;
            }

            // START PWC 6254469
            // Restore the previously saved encoding
            sresp.setCharacterEncoding(responseCharEnc);
            // END PWC 6254469

            ServletResponse sresponse = (ServletResponse) response;
            if (sresponse instanceof HttpServletResponse)
                ((HttpServletResponse) sresponse).sendError
                    (HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        }

        response.setSuspended(false);

        try {
            report(request, response, throwable);
        } catch (Throwable tt) {
            ;
        }

    }


    /**
     * Return a String rendering of this object.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("ErrorReportValve[");
        sb.append(container.getName());
        sb.append("]");
        return (sb.toString());

    }


    // ------------------------------------------------------ Protected Methods


    /**
     * Prints out an error report.
     *
     * @param request The request being processed
     * @param response The response being generated
     * @param exception The exception that occurred (which possibly wraps
     *  a root cause exception
     */
    protected void report(Request request, Response response,
                          Throwable throwable)
        throws IOException {

        // Do nothing on non-HTTP responses
        if (!(response instanceof HttpResponse))
            return;
        HttpResponse hresponse = (HttpResponse) response;
        if (!(response instanceof HttpServletResponse))
            return;
        HttpServletResponse hres = (HttpServletResponse) response;
        int statusCode = hresponse.getStatus();

        // Do nothing on a 1xx, 2xx and 3xx status
        // Do nothing if anything has been written already
        if (statusCode < 400 || (response.getContentCount() > 0))
            return;

        Throwable rootCause = null;

        if (throwable != null) {

            if (throwable instanceof ServletException)
                rootCause = ((ServletException) throwable).getRootCause();

        }

        String message = RequestUtil.filter(hresponse.getMessage());
        /* S1AS 4878272
        if (message == null)
            message = "";
        */
        // BEGIN S1AS 4878272
        if (message == null) {
            message = RequestUtil.filter(hresponse.getDetailMessage());
            if (message == null) {
                message = "";
            }
        }
        // END S1AS 4878272

        // Do nothing if there is no report for the specified status code
        String report = null;
        try {
            report = sm.getString("http." + statusCode, message);
        } catch (Throwable t) {
            ;
        }
        if (report == null)
            return;

        StringBuffer sb = new StringBuffer();

        sb.append("<html><head><title>");
        sb.append(ServerInfo.getServerInfo()).append(" - ");
        sb.append(sm.getString("errorReportValve.errorReport"));
        sb.append("</title>");
        sb.append("<style><!--");
        sb.append(org.apache.catalina.util.TomcatCSS.TOMCAT_CSS);
        sb.append("--></style> ");
        sb.append("</head><body>");
        sb.append("<h1>");
        sb.append(sm.getString("errorReportValve.statusHeader",
                               "" + statusCode, message)).append("</h1>");
        sb.append("<HR size=\"1\" noshade>");
        sb.append("<p><b>type</b> ");
        if (throwable != null) {
            sb.append(sm.getString("errorReportValve.exceptionReport"));
        } else {
            sb.append(sm.getString("errorReportValve.statusReport"));
        }
        sb.append("</p>");
        sb.append("<p><b>");
        sb.append(sm.getString("errorReportValve.message"));
        sb.append("</b> <u>");
        sb.append(message).append("</u></p>");
        sb.append("<p><b>");
        sb.append(sm.getString("errorReportValve.description"));
        sb.append("</b> <u>");
        sb.append(report);
        sb.append("</u></p>");

        if (throwable != null) {

            String stackTrace = JdkCompat.getJdkCompat()
                .getPartialServletStackTrace(throwable);
            sb.append("<p><b>");
            sb.append(sm.getString("errorReportValve.exception"));
            sb.append("</b> <pre>");
            sb.append(stackTrace);
            sb.append("</pre></p>");

            while (rootCause != null) {
                stackTrace = JdkCompat.getJdkCompat()
                    .getPartialServletStackTrace(rootCause);
                sb.append("<p><b>");
                sb.append(sm.getString("errorReportValve.rootCause"));
                sb.append("</b> <pre>");
                sb.append(stackTrace);
                sb.append("</pre></p>");
                // In case root cause is somehow heavily nested
                try {
                    rootCause = (Throwable)PropertyUtils.getProperty
                                                (rootCause, "rootCause");
                } catch (ClassCastException e) {
                    rootCause = null;
                } catch (IllegalAccessException e) {
                    rootCause = null;
                } catch (NoSuchMethodException e) {
                    rootCause = null;
                } catch (java.lang.reflect.InvocationTargetException e) {
                    rootCause = null;
                }
            }

            sb.append("<p><b>");
            sb.append(sm.getString("errorReportValve.note"));
            sb.append("</b> <u>");
            sb.append(sm.getString("errorReportValve.rootCauseInLogs",
                                   ServerInfo.getServerInfo()));
            sb.append("</u></p>");

        }

        sb.append("<HR size=\"1\" noshade>");
        sb.append("<h3>").append(ServerInfo.getServerInfo()).append("</h3>");
        sb.append("</body></html>");

        /* PWC 6254469
        // set the charset part of content type before getting the writer
        */
        try {
            hres.setContentType("text/html");
            /* PWC 6254469
            hres.setCharacterEncoding("UTF-8");
            */
        } catch (Throwable t) {
            if (debug >= 1)
                log("status.setContentType", t);
        }

        try {
            Writer writer = response.getReporter();
            if (writer != null) {
                // If writer is null, it's an indication that the response has
                // been hard committed already, which should never happen
                writer.write(sb.toString());
            }
        } catch (IOException e) {
            ;
        } catch (IllegalStateException e) {
            ;
        }

    }


    /**
     * Log a message on the Logger associated with our Container (if any).
     *
     * @param message Message to be logged
     */
    protected void log(String message) {

        Logger logger = container.getLogger();
        if (logger != null)
            logger.log(this.toString() + ": " + message);
        else
            System.out.println(this.toString() + ": " + message);

    }


    /**
     * Log a message on the Logger associated with our Container (if any).
     *
     * @param message Message to be logged
     * @param throwable Associated exception
     */
    protected void log(String message, Throwable throwable) {

        Logger logger = container.getLogger();
        if (logger != null)
            logger.log(this.toString() + ": " + message, throwable);
        else {
            System.out.println(this.toString() + ": " + message);
            throwable.printStackTrace(System.out);
        }

    }


}
