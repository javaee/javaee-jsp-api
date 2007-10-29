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

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.catalina.connector.ResponseFacade;
import org.apache.catalina.deploy.ErrorPage;
import org.apache.catalina.util.RequestUtil;
import org.apache.catalina.util.ResponseUtil;
import org.apache.catalina.util.StringManager;
import org.apache.catalina.valves.ErrorReportValve;
import org.apache.coyote.tomcat5.CoyoteResponseFacade;

import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;

/**
 * Class responsible for processing the result of a RD.forward() invocation
 * before committing the response.
 *
 * If sendError() was called during RD.forward(), an attempt is made to match
 * the status code against the error pages of the RD's associated context, or
 * those of the host on which the context has been deployed.
 *
 * The response contents are then committed, to comply with SRV.8.4
 * ("The Forward Method"):
 * 
 *   Before the forward method of the RequestDispatcher interface returns, the
 *   response content must be sent and committed, and closed by the servlet
 *   container
 */
class ApplicationDispatcherForward {

    private static Log log = LogFactory.getLog(
        ApplicationDispatcherForward.class);

    private static final StringManager sm =
        StringManager.getManager(org.apache.catalina.valves.Constants.Package);


    static void commit(HttpServletRequest request,
                       HttpServletResponse response,
                       Context context,
                       Wrapper wrapper)
            throws IOException, ServletException {

        int statusCode = getStatus(response);
        if (statusCode >= 400
                && request.getAttribute(Globals.EXCEPTION_ATTR) == null) {
            boolean matchFound = status(request, response, context, wrapper,
                                        statusCode);
            if (!matchFound) {
                serveDefaultErrorPage(request, response, statusCode);
            }
        }

        try {
            PrintWriter writer = response.getWriter();
            writer.close();
        } catch (IllegalStateException e) {
            try {
                ServletOutputStream stream = response.getOutputStream();
                stream.close();
            } catch (IllegalStateException f) {
                ;
            } catch (IOException f) {
                ;
            }
        } catch (IOException e) {
            ;
        }
    }


    /*
     * Searches and processes a custom error page for the given status code.
     *
     * This method attempts to map the given status code to an error page,
     * using the mappings of the given context or those of the host on which
     * the given context has been deployed.
     *
     * If a match is found using the context mappings, the request is forwarded
     * to the error page. Otherwise, if a match is found using the host 
     * mappings, the contents of the error page are returned. If no match is
     * found, no action is taken.
     *
     * @return true if a matching error page was found, false otherwise
     */
    private static boolean status(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Context context,
                                  Wrapper wrapper,
                                  int statusCode) {

        boolean matchFound = false;

        ErrorPage errorPage = context.findErrorPage(statusCode);
        if (errorPage != null) {

            matchFound = true;

            // Prevent infinite loop
            String requestPath = (String) request.getAttribute(
                ApplicationFilterFactory.DISPATCHER_REQUEST_PATH_ATTR);
            if (requestPath == null
                    || !requestPath.equals(errorPage.getLocation())) {
                String message = RequestUtil.filter(getMessage(response));
                if (message == null) {
                    message = "";
                }
                prepareRequestForDispatch(request,
                                          wrapper,
                                          errorPage.getLocation(),
                                          statusCode,
                                          message);
                custom(request, response, errorPage, context);
            }
        } else {
            errorPage = ((StandardHost) context.getParent()).findErrorPage(
                                            statusCode);
            if (errorPage != null) {
                matchFound = true;
                try {
                    serveErrorPage(response, errorPage, statusCode);
                } catch (Exception e) {
                    log.warn("Exception processing " + errorPage, e);
                }
            }
        }

        return matchFound;
    }


    /**
     * Handles an HTTP status code or exception by forwarding control
     * to the location included in the specified errorPage object. 
     */
    private static void custom(HttpServletRequest request,
                               HttpServletResponse response,
                               ErrorPage errorPage,
                               Context context) {
        try {
            // Forward control to the specified error page
            if (response.isCommitted()) {
                /*
                 * If the target of the RD.forward() has called
                 * response.sendError(), the response will have been committed,
                 * and any attempt to RD.forward() the response to the error
                 * page will cause an IllegalStateException.
                 * Uncommit the response.
                 */
                resetResponse(response);
            }
            ServletContext servletContext = context.getServletContext();
            RequestDispatcher rd =
                servletContext.getRequestDispatcher(errorPage.getLocation());
            rd.forward(request, response);
        } catch (IllegalStateException ise) {
            log.warn("Exception processing " + errorPage, ise);
        } catch (Throwable t) {
            log.warn("Exception processing " + errorPage, t);
        }
    }


    /**
     * Adds request attributes in preparation for RD.forward().
     */
    private static void prepareRequestForDispatch(HttpServletRequest request,
                                                  Wrapper errorServlet,
                                                  String errorPageLocation,
                                                  int errorCode,
                                                  String errorMessage) {
        request.setAttribute(
            ApplicationFilterFactory.DISPATCHER_TYPE_ATTR,
            new Integer(ApplicationFilterFactory.ERROR));

        request.setAttribute(
            Globals.EXCEPTION_PAGE_ATTR,
            request.getRequestURI());

        if (errorServlet != null) {
            // Save the logical name of the servlet in which the error occurred
            request.setAttribute(Globals.SERVLET_NAME_ATTR,
                                 errorServlet.getName());
        }

        request.setAttribute(
            ApplicationFilterFactory.DISPATCHER_REQUEST_PATH_ATTR,
            errorPageLocation);

        request.setAttribute(Globals.STATUS_CODE_ATTR, new Integer(errorCode));

        request.setAttribute(Globals.ERROR_MESSAGE_ATTR, errorMessage);
    }

    /**
     * Copies the contents of the given error page to the response.
     */
    private static void serveErrorPage(HttpServletResponse response,
                                       ErrorPage errorPage,
                                       int statusCode)
            throws Exception {

        ServletOutputStream ostream = null;
        PrintWriter writer = null;
        FileReader reader = null;
        BufferedInputStream istream = null;
        IOException ioe = null;

        String message = errorPage.getReason();
        if (message != null && !response.isCommitted()) {
            response.reset();
            response.setStatus(statusCode, message);
        }
         
        try {
            ostream = response.getOutputStream();
        } catch (IllegalStateException e) {
            // If it fails, we try to get a Writer instead if we're
            // trying to serve a text file
            writer = response.getWriter();
        }

        if (writer != null) {
            reader = new FileReader(errorPage.getLocation());
            ioe = ResponseUtil.copy(reader, writer);
            try {
                reader.close();
            } catch (Throwable t) {
                ;
            }
        } else {
            istream = new BufferedInputStream(
                new FileInputStream(errorPage.getLocation()));
            ioe = ResponseUtil.copy(istream, ostream);
            try {
                istream.close();
            } catch (Throwable t) {
                ;
            }
        }

        // Rethrow any exception that may have occurred
        if (ioe != null) {
            throw ioe;
        }
    }


    /**
     * Renders the default error page.
     */
    private static void serveDefaultErrorPage(HttpServletRequest request,
                                              HttpServletResponse response,
                                              int statusCode)
            throws IOException, ServletException {

        // Do nothing on a 1xx, 2xx and 3xx status
        if (response.isCommitted() || statusCode < 400) {
            return;
        }

        String message = RequestUtil.filter(getMessage(response));
        if (message == null) {
            message = "";
        }

        // Do nothing if there is no report for the specified status code
        String report = null;
        try {
            report = sm.getString("http." + statusCode, message);
        } catch (Throwable t) {
            ;
        }
        if (report == null) {
            return;
        }

        String responseContents =
            ErrorReportValve.makeErrorPage(statusCode, message, null, null,
                                           report);
        try {
            response.setContentType("text/html");
            response.getWriter().write(responseContents);
        } catch (Throwable t) {
            log.warn("Exception sending default error page", t);
        }
    }


    private static int getStatus(ServletResponse response) {
   
        while (response instanceof ServletResponseWrapper) {
            response = ((ServletResponseWrapper) response).getResponse();
        }

        return ((CoyoteResponseFacade) response).getStatus();
    }


    private static String getMessage(ServletResponse response) {
   
        while (response instanceof ServletResponseWrapper) {
            response = ((ServletResponseWrapper) response).getResponse();
        }

        return ((CoyoteResponseFacade) response).getMessage();
    }


    private static void resetResponse(ServletResponse response) {
   
        while (response instanceof ServletResponseWrapper) {
            response = ((ServletResponseWrapper) response).getResponse();
        }

        ((CoyoteResponseFacade) response).setSuspended(false);
        ((CoyoteResponseFacade) response).setAppCommitted(false);
    }

}
