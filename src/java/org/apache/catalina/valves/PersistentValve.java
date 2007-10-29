

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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Manager;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Session;
import org.apache.catalina.util.StringManager;
import org.apache.catalina.valves.ValveBase;
import org.apache.catalina.Logger;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.Store;
import org.apache.catalina.session.PersistentManager;
import org.apache.catalina.session.ManagerBase;
import org.apache.coyote.tomcat5.CoyoteRequest;

/**
 * Valve that implements the default basic behavior for the
 * <code>StandardHost</code> container implementation.
 * <p>
 * <b>USAGE CONSTRAINT</b>: To work correctly it requires a  PersistentManager.
 *
 * @author Jean-Frederic Clere
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:09 $
 */

public class PersistentValve
    extends ValveBase {


    // ----------------------------------------------------- Instance Variables


    /**
     * The descriptive information related to this implementation.
     */
    private static final String info =
        "org.apache.catalina.valves.PersistentValve/1.0";


    /**
     * The string manager for this package.
     */
    private static final StringManager sm =
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
     * Select the appropriate child Context to process this request,
     * based on the specified request URI.  If no matching Context can
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

        // Select the Context to be used for this Request
        StandardHost host = (StandardHost) getContainer();
        Context context = request.getContext();
        if (context == null) {
            ((HttpServletResponse) response.getResponse()).sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                 sm.getString("standardHost.noContext"));
            // START OF IASRI 4665318
            return END_PIPELINE;
            // END OF IASRI 4665318
        }

        // Bind the context CL to the current thread
        Thread.currentThread().setContextClassLoader
            (context.getLoader().getClassLoader());

        // Update the session last access time for our session (if any)
        HttpServletRequest hreq = (HttpServletRequest) request.getRequest();
        String sessionId = hreq.getRequestedSessionId();
        Manager manager = context.getManager();
        if (sessionId != null && manager != null) {
            if (manager instanceof PersistentManager) {
                Store store = ((PersistentManager) manager).getStore();
                if (store != null) {
                    Session session = null;
                    try {
                        session = store.load(sessionId);
                    } catch (Exception e) {
                        log("deserializeError");
                    }
                    if (session != null) {
                        if (!session.isValid() ||
                            isSessionStale(session, System.currentTimeMillis())) {
                            log("session swapped in is invalid or expired");
                            session.expire();
                            store.remove(sessionId);
                        } else {
                            session.setManager(manager);
                            // session.setId(sessionId); Only if new ???
                            manager.add(session);
                            // ((StandardSession)session).activate();
                            session.access();
                        }
                    }
                }
            }
        }
        log("sessionId: " + sessionId);

        // Ask the next valve to process the request.
        // START OF IASRI 4665318
        // context.invokeNext(request, response);
        // return;
        return INVOKE_NEXT;
    }


    public void postInvoke(Request request, Response response)
                                    throws IOException, ServletException{
        Context context = request.getContext();
        if (context == null) {
            ((HttpServletResponse) response.getResponse()).sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                 sm.getString("standardHost.noContext"));
            return;
        }

        // Bind the context CL to the current thread
        Thread.currentThread().setContextClassLoader
            (context.getLoader().getClassLoader());

        // Update the session last access time for our session (if any)
        HttpServletRequest hreq = (HttpServletRequest) request.getRequest();
        String sessionId = hreq.getRequestedSessionId();
        Manager manager = context.getManager();
        // END OF IASRI 4665318

        // Read the sessionid after the response.
        // HttpSession hsess = hreq.getSession(false);
        String newsessionId = null;
        if (request instanceof CoyoteRequest) {
            Session sess;
            try {
                sess = ((CoyoteRequest) request).getSessionInternal();
            } catch (Exception ex) {
                sess = null;
            }
            if (sess!=null) {
                newsessionId = sess.getIdInternal();
            }
        } else {
            HttpSession hsess;
            try {
                hsess = hreq.getSession();
            } catch (Exception ex) {
                hsess = null;
            }
            if (hsess!=null) {
                newsessionId = hsess.getId();
            }
        }

        log("newsessionId: " + newsessionId);
        if (newsessionId!=null) {
            /* store the session in the store and remove it from the manager */
            if (manager instanceof PersistentManager) {
                Session session = manager.findSession(newsessionId);
                Store store = ((PersistentManager) manager).getStore();
                if (store != null && session!=null &&
                    session.isValid() &&
                    !isSessionStale(session, System.currentTimeMillis())) {
                    // ((StandardSession)session).passivate();
                    store.save(session);
                    ((PersistentManager) manager).removeSuper(session);
                    session.recycle();
                } else {
                    log("newsessionId store: " + store + " session: " +
                        session + " valid: " + session.isValid() +
                        " Staled: " +
                        isSessionStale(session, System.currentTimeMillis()));

                }
            } else {
                log("newsessionId Manager: " + manager);
            }
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
     * Indicate whether the session has been idle for longer
     * than its expiration date as of the supplied time.
     *
     * FIXME: Probably belongs in the Session class.
     */
    protected boolean isSessionStale(Session session, long timeNow) {
 
        int maxInactiveInterval = session.getMaxInactiveInterval();
        if (maxInactiveInterval >= 0) {
            int timeIdle = // Truncate, do not round up
                (int) ((timeNow - session.getLastAccessedTime()) / 1000L);
            if (timeIdle >= maxInactiveInterval)
                return true;
        }
 
        return false;
 
    }

}
