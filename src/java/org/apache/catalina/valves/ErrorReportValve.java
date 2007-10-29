

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

import com.sun.org.apache.commons.beanutils.PropertyUtils;

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
 * @version $Revision: 1.16 $ $Date: 2006/08/01 18:42:43 $
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
            /*
             * Restore the previously saved response encoding only if it is
             * different from the default (ISO-8859-1). This is important so
             * that a subsequent call to ServletResponse.setLocale() has an
             * opportunity to set it so it corresponds to the resource bundle
             * locale (see 6412710)
             */
            if (responseCharEnc != null && !responseCharEnc.equals(
                    org.apache.coyote.Constants.DEFAULT_CHARACTER_ENCODING)) {
                sresp.setCharacterEncoding(responseCharEnc);
            }
            // END PWC 6254469

            ServletResponse sresponse = (ServletResponse) response;
            /* GlassFish 6386229
            if (sresponse instanceof HttpServletResponse)
                ((HttpServletResponse) sresponse).sendError
                    (HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            */
            // START GlassFish 6386229
            ((HttpServletResponse) sresponse).sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            // END GlassFish 6386229
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

        /* GlassFish 6386229
        // Do nothing on non-HTTP responses
        if (!(response instanceof HttpResponse))
            return;
        */
        HttpResponse hresponse = (HttpResponse) response;
        /* GlassFish 6386229
        if (!(response instanceof HttpServletResponse))
            return;
        */
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
            /* SJSAS 6412710
            report = sm.getString("http." + statusCode, message);
            */
            // START SJSAS 6412710
            report = sm.getString("http." + statusCode, message,
                                  hres.getLocale());
            // END SJSAS 6412710
        } catch (Throwable t) {
            ;
        }
        if (report == null)
            return;

        String errorPage = makeErrorPage(statusCode, message,
                                         throwable, rootCause,
                                         report, hres);

        // START SJSAS 6412710
        /*
         * If throwable is not null, we've already preserved any non-default
         * response encoding in postInvoke(), so that the throwable's exception
         * message can be delivered to the client without any loss of
         * information. The following call to ServletResponse.setLocale()
         * will not override the response encoding in this case.
         * For all other cases, the response encoding will be set according to
         * the resource bundle locale.
         */
        hres.setLocale(sm.getResourceBundleLocale(hres.getLocale()));
        // END SJSAS 6412710

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
                writer.write(errorPage);
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


    public static String makeErrorPage(int statusCode,
                                       String message,
                                       Throwable throwable,
                                       Throwable rootCause,
                                       String report,
                                       HttpServletResponse response) {

        // START SJSAS 6412710
        Locale responseLocale = response.getLocale();
        // END SJSAS 6412710

        StringBuffer sb = new StringBuffer();

        sb.append("<html><head><title>");
        sb.append(ServerInfo.getServerInfo()).append(" - ");
        /* 6412710
        sb.append(sm.getString("errorReportValve.errorReport"));
        */
        // START SJSAS 6412710
        sb.append(sm.getString("errorReportValve.errorReport",
                               responseLocale));
        // END SJSAS 6412710
        sb.append("</title>");
        sb.append("<style><!--");
        sb.append(org.apache.catalina.util.TomcatCSS.TOMCAT_CSS);
        sb.append("--></style> ");
        sb.append("</head><body>");
        sb.append("<h1>");
        /* SJSAS 6412710
        sb.append(sm.getString("errorReportValve.statusHeader",
                               "" + statusCode, message)).append("</h1>");
        */
        // START SJSAS 6412710
        sb.append(sm.getString("errorReportValve.statusHeader",
                               "" + statusCode, message,
                               responseLocale)).append("</h1>");
        // END SJSAS 6412710
        sb.append("<HR size=\"1\" noshade>");
        sb.append("<p><b>type</b> ");
        if (throwable != null) {
            /* SJSAS 6412710
            sb.append(sm.getString("errorReportValve.exceptionReport"));
            */
            // START SJJAS 6412710
            sb.append(sm.getString("errorReportValve.exceptionReport",
                                   responseLocale));
            // END SJSAS 6412710
        } else {
            /* SJSAS 6412710
            sb.append(sm.getString("errorReportValve.statusReport"));
            */
            // START SJSAS 6412710
            sb.append(sm.getString("errorReportValve.statusReport",
                                   responseLocale));
            // END SJSAS 6412710
        }
        sb.append("</p>");
        sb.append("<p><b>");
        /* SJSAS 6412710
        sb.append(sm.getString("errorReportValve.message"));
        */
        // START SJSAS 6412710
        sb.append(sm.getString("errorReportValve.message",
                               responseLocale));
        // END SJSAS 6412710
        sb.append("</b> <u>");
        sb.append(message).append("</u></p>");
        sb.append("<p><b>");
        /* SJSAS 6412710
        sb.append(sm.getString("errorReportValve.description"));
        */
        // START SJSAS 6412710
        sb.append(sm.getString("errorReportValve.description",
                               responseLocale));
        // END SJSAS 6412710
        sb.append("</b> <u>");
        sb.append(report);
        sb.append("</u></p>");

        if (throwable != null) {
            /* GlassFish 823
            String stackTrace = JdkCompat.getJdkCompat()
                .getPartialServletStackTrace(throwable);
            */
            sb.append("<p><b>");
            /* SJSAS 6412710
            sb.append(sm.getString("errorReportValve.exception"));
            */
            // START SJSAS 6412710
            sb.append(sm.getString("errorReportValve.exception",
                                   responseLocale));
            // END SJSAS 6412710
            sb.append("</b> <pre>");
            /* SJSAS 6387790
            sb.append(stackTrace);
            */
            /* GlassFish 823
            // START SJSAS 6387790
            sb.append(RequestUtil.filter(stackTrace));
            // END SJSAS 6387790
            */
            // START GlassFish 823
            sb.append(throwable);
            // END GlassFish 823
            sb.append("</pre></p>");

            while (rootCause != null) {
                /* GlassFish 823
                stackTrace = JdkCompat.getJdkCompat()
                    .getPartialServletStackTrace(rootCause);
                */
                sb.append("<p><b>");
                /* SJSAS 6412710
                sb.append(sm.getString("errorReportValve.rootCause"));
                */
                // START SJSAS 6412710
                sb.append(sm.getString("errorReportValve.rootCause",
                                       responseLocale));
                // END SJSAS 6412710
                sb.append("</b> <pre>");
                /* SJSAS 6387790
                sb.append(stackTrace);
                */
                /* GlassFish 823
                // START SJSAS 6387790
                sb.append(RequestUtil.filter(stackTrace));
                // END SJSAS 6387790
                */
                // START GlassFish 823
                sb.append(rootCause);
                // END GlassFish 823
                sb.append("</pre></p>");

                /* GlassFish 823
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
                */
                // START GlassFish 823
                rootCause = rootCause.getCause();
                // END GlassFish 823
            }

            sb.append("<p><b>");
            /* SJSAS 6412710
            sb.append(sm.getString("errorReportValve.note"));
            */
            // START SJSAS 6412710
            sb.append(sm.getString("errorReportValve.note",
                                   responseLocale));
            // END SJAS 6412710
            sb.append("</b> <u>");
            /* SJSAS 6412710
            sb.append(sm.getString("errorReportValve.rootCauseInLogs",
                                   ServerInfo.getServerInfo()));
            */
            // START SJSAS 6412710
            sb.append(sm.getString("errorReportValve.rootCauseInLogs",
                                   ServerInfo.getServerInfo(),
                                   responseLocale));
            // END SJSAS 6412710
            sb.append("</u></p>");

        }

        sb.append("<HR size=\"1\" noshade>");
        sb.append("<h3>").append(ServerInfo.getServerInfo()).append("</h3>");
        sb.append("</body></html>");
        return sb.toString();
    }

}
