

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
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.naming.NamingException;

import org.apache.tomcat.util.buf.CharChunk;
import org.apache.tomcat.util.buf.MessageBytes;

import org.apache.naming.ContextBindings;
import org.apache.naming.resources.DirContextURLStreamHandler;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.Logger;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Wrapper;
import org.apache.catalina.util.RequestUtil;
import org.apache.catalina.util.StringManager;
import org.apache.catalina.valves.ValveBase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.tomcat.util.log.SystemLogHandler;

/**
 * Valve that implements the default basic behavior for the
 * <code>StandardContext</code> container implementation.
 * <p>
 * <b>USAGE CONSTRAINT</b>:  This implementation is likely to be useful only
 * when processing HTTP requests.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.6 $ $Date: 2005/12/08 01:27:35 $
 */

final class StandardContextValve
    extends ValveBase {


    // ----------------------------------------------------- Instance Variables


    /**
     * The descriptive information related to this implementation.
     */
    private static final String info =
        "org.apache.catalina.core.StandardContextValve/1.0";


    /**
     * The string manager for this package.
     */
    private static final StringManager sm =
        StringManager.getManager(Constants.Package);


    private static Log log = LogFactory.getLog(StandardContextValve.class);


    // ------------------------------------------------------------- Properties


    /**
     * Return descriptive information about this Valve implementation.
     */
    public String getInfo() {

        return (info);

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Select the appropriate child Wrapper to process this request,
     * based on the specified request URI.  If no matching Wrapper can
     * be found, return an appropriate HTTP error.
     *
     * @param request Request to be processed
     * @param response Response to be produced
     * @param valveContext Valve context used to forward to the next Valve
     *
     * @exception IOException if an input/output error occurred
     * @exception ServletException if a servlet error occurred
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

        // Disallow any direct access to resources under WEB-INF or META-INF
        HttpRequest hreq = (HttpRequest) request;
        MessageBytes requestPathMB = hreq.getRequestPathMB();
        if ((requestPathMB.startsWithIgnoreCase("/META-INF/", 0))
            || (requestPathMB.equalsIgnoreCase("/META-INF"))
            || (requestPathMB.startsWithIgnoreCase("/WEB-INF/", 0))
            || (requestPathMB.equalsIgnoreCase("/WEB-INF"))) {
            String requestURI = hreq.getDecodedRequestURI();
            notFound(requestURI, (HttpServletResponse) response.getResponse());
            return END_PIPELINE;
        }

        // Wait if we are reloading
        while (((StandardContext) container).getPaused()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                ;
            }
        }

        // START GlassFish 247
        /*
        // Select the Wrapper to be used for this Request
        Wrapper wrapper = request.getWrapper();
        if (wrapper == null) {
            String requestURI = hreq.getDecodedRequestURI();
            notFound(requestURI, (HttpServletResponse) response.getResponse());
            return END_PIPELINE;
        }
        */
        // END GlassFish 247
        
        // Normal request processing
        if (((StandardContext) container).getSwallowOutput()) {
            try {
                SystemLogHandler.startCapture();
                return invokeInternal(wrapper, request, response);
            } finally {
                String log = SystemLogHandler.stopCapture();
                if (log != null && log.length() > 0) {
                    container.getLogger().log(log);
                }
            }
        } else {
            return invokeInternal(wrapper, request, response);
        }

    }


    // -------------------------------------------------------- Private Methods


    /**
     * Call invoke.
     */
    private int invokeInternal(Wrapper wrapper, Request request, 
                                Response response)
        throws IOException, ServletException {

        Object instances[] = 
            ((Context) container).getApplicationEventListeners();

        ServletRequestEvent event = null;

        if ((instances != null) 
                && (instances.length > 0)) {
            event = new ServletRequestEvent
                (((StandardContext) container).getServletContext(), 
                 request.getRequest());
            // create pre-service event
            for (int i = 0; i < instances.length; i++) {
                if (instances[i] == null)
                    continue;
                if (!(instances[i] instanceof ServletRequestListener))
                    continue;
                ServletRequestListener listener =
                    (ServletRequestListener) instances[i];
                // START SJSAS 6329662
                ((StandardContext) container).fireContainerEvent(
                    "beforeRequestInitialized", listener);
                // END SJSAS 6329662
                try {
                    listener.requestInitialized(event);
                } catch (Throwable t) {
                    log(sm.getString(
                        "standardContextValve.requestListener.requestInit",
                        instances[i].getClass().getName()),
                        t);
                    ServletRequest sreq = request.getRequest();
                    sreq.setAttribute(Globals.EXCEPTION_ATTR,t);
                    // START OF IASRI 4665318
                    // return;
                    return END_PIPELINE;
                    // END OF IASRI 4665318
                // START SJSAS 6329662
                } finally {
                    ((StandardContext) container).fireContainerEvent(
                        "afterRequestInitialized", listener);
                // END SJSAS 6329662
                }
            }
        }

        // START OF IASRI 4665318
        wrapper.getPipeline().invoke(request, response);
        return END_PIPELINE;
        // END OF IASRI 4665318

   } 

    public void postInvoke(Request request, Response response)
        throws IOException, ServletException {
    // END OF IASRI 4665318
        Object instances[] = 
            ((Context) container).getApplicationEventListeners();

        ServletRequestEvent event = new ServletRequestEvent
                    (((StandardContext) container).getServletContext(), 
                     request.getRequest());

        if ((instances !=null ) &&
                (instances.length > 0)) {
            // create post-service event
            for (int i = 0; i < instances.length; i++) {
                if (instances[i] == null)
                    continue;
                if (!(instances[i] instanceof ServletRequestListener))
                    continue;
                ServletRequestListener listener =
                    (ServletRequestListener) instances[i];
                // START SJSAS 6329662
                ((StandardContext) container).fireContainerEvent(
                    "beforeRequestDestroyed", listener);
                // END SJSAS 6329662
                try {
                    listener.requestDestroyed(event);
                } catch (Throwable t) {
                    log(sm.getString(
                        "standardContextValve.requestListener.requestDestroyed",
                        instances[i].getClass().getName()),
                        t);
                    ServletRequest sreq = request.getRequest();
                    sreq.setAttribute(Globals.EXCEPTION_ATTR,t);
                // START SJSAS 6329662
                } finally {
                    ((StandardContext) container).fireContainerEvent(
                        "afterRequestDestroyed", listener);
                // END SJSAS 6329662
                }
            }
        }

    }


    /**
     * Report a "bad request" error for the specified resource.  FIXME:  We
     * should really be using the error reporting settings for this web
     * application, but currently that code runs at the wrapper level rather
     * than the context level.
     *
     * @param requestURI The request URI for the requested resource
     * @param response The response we are creating
     */
    private void badRequest(String requestURI, HttpServletResponse response) {

        try {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, requestURI);
        } catch (IllegalStateException e) {
            ;
        } catch (IOException e) {
            ;
        }

    }
    
    
    /**
     * Report a "forbidden" error for the specified resource. 
     *
     * @param requestURI The request URI for the requested resource
     * @param response The response we are creating
     */
    private void forbidden(String requestURI, HttpServletResponse response) {

        try {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, requestURI);
        } catch (IllegalStateException e) {
            ;
        } catch (IOException e) {
            ;
        }

    }


    /**
     * Report a "not found" error for the specified resource.  FIXME:  We
     * should really be using the error reporting settings for this web
     * application, but currently that code runs at the wrapper level rather
     * than the context level.
     *
     * @param requestURI The request URI for the requested resource
     * @param response The response we are creating
     */
    private void notFound(String requestURI, HttpServletResponse response) {

        try {
            /* IASRI 4878272
            response.sendError(HttpServletResponse.SC_NOT_FOUND, requestURI);
            */
            // BEGIN IASRI 4878272
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            // END IASRI 4878272
        } catch (IllegalStateException e) {
            ;
        } catch (IOException e) {
            ;
        }

    }


    /**
     * Log a message on the Logger associated with our Container (if any)
     *
     * @param message Message to be logged
     */
    private void log(String message) {

        Logger logger = null;
        if (container != null)
            logger = container.getLogger();
        if (logger != null)
            logger.log("StandardContextValve[" + container.getName() + "]: "
                       + message);
        else {
            String containerName = null;
            if (container != null)
                containerName = container.getName();
            System.out.println("StandardContextValve[" + containerName
                               + "]: " + message);
        }

    }


    /**
     * Log a message on the Logger associated with our Container (if any)
     *
     * @param message Message to be logged
     * @param throwable Associated exception
     */
    private void log(String message, Throwable throwable) {

        Logger logger = null;
        if (container != null)
            logger = container.getLogger();
        if (logger != null)
            logger.log("StandardContextValve[" + container.getName() + "]: "
                       + message, throwable);
        else {
            String containerName = null;
            if (container != null)
                containerName = container.getName();
            System.out.println("StandardContextValve[" + containerName
                               + "]: " + message);
            System.out.println("" + throwable);
            throwable.printStackTrace(System.out);
        }

    }


}
