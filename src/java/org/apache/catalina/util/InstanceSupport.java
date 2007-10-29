

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


package org.apache.catalina.util;


import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.catalina.InstanceEvent;
import org.apache.catalina.InstanceListener;
import org.apache.catalina.Wrapper;


/**
 * Support class to assist in firing InstanceEvent notifications to
 * registered InstanceListeners.
 *
 * @author Craig R. McClanahan
 * @version $Id: InstanceSupport.java,v 1.1.1.1 2005/05/27 22:55:09 dpatil Exp $
 */

public final class InstanceSupport {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new InstanceSupport object associated with the specified
     * Instance component.
     *
     * @param lifecycle The Instance component that will be the source
     *  of events that we fire
     */
    public InstanceSupport(Wrapper wrapper) {

        super();
        this.wrapper = wrapper;

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The set of registered InstanceListeners for event notifications.
     */
    private InstanceListener listeners[] = new InstanceListener[0];


    /**
     * The source component for instance events that we will fire.
     */
    private Wrapper wrapper = null;


    // ------------------------------------------------------------- Properties


    /**
     * Return the Wrapper with which we are associated.
     */
    public Wrapper getWrapper() {

        return (this.wrapper);

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Add a lifecycle event listener to this component.
     *
     * @param listener The listener to add
     */
    public void addInstanceListener(InstanceListener listener) {

      synchronized (listeners) {
          InstanceListener results[] =
            new InstanceListener[listeners.length + 1];
          for (int i = 0; i < listeners.length; i++)
              results[i] = listeners[i];
          results[listeners.length] = listener;
          listeners = results;
      }

    }


    /**
     * Notify all lifecycle event listeners that a particular event has
     * occurred for this Container.  The default implementation performs
     * this notification synchronously using the calling thread.
     *
     * @param type Event type
     * @param filter The relevant Filter for this event
     */
    public void fireInstanceEvent(String type, Filter filter) {

        if (listeners.length == 0)
            return;

        InstanceEvent event = new InstanceEvent(wrapper, filter, type);
        InstanceListener interested[] = null;
        synchronized (listeners) {
            interested = (InstanceListener[]) listeners.clone();
        }
        for (int i = 0; i < interested.length; i++)
            interested[i].instanceEvent(event);

    }


    /**
     * Notify all lifecycle event listeners that a particular event has
     * occurred for this Container.  The default implementation performs
     * this notification synchronously using the calling thread.
     *
     * @param type Event type
     * @param filter The relevant Filter for this event
     * @param exception Exception that occurred
     */
    public void fireInstanceEvent(String type, Filter filter,
                                  Throwable exception) {

        if (listeners.length == 0)
            return;

        InstanceEvent event = new InstanceEvent(wrapper, filter, type,
                                                exception);
        InstanceListener interested[] = null;
        synchronized (listeners) {
            interested = (InstanceListener[]) listeners.clone();
        }
        for (int i = 0; i < interested.length; i++)
            interested[i].instanceEvent(event);

    }


    /**
     * Notify all lifecycle event listeners that a particular event has
     * occurred for this Container.  The default implementation performs
     * this notification synchronously using the calling thread.
     *
     * @param type Event type
     * @param filter The relevant Filter for this event
     * @param request The servlet request we are processing
     * @param response The servlet response we are processing
     */
    public void fireInstanceEvent(String type, Filter filter,
                                  ServletRequest request,
                                  ServletResponse response) {

        if (listeners.length == 0)
            return;

        InstanceEvent event = new InstanceEvent(wrapper, filter, type,
                                                request, response);
        InstanceListener interested[] = null;
        synchronized (listeners) {
            interested = (InstanceListener[]) listeners.clone();
        }
        for (int i = 0; i < interested.length; i++)
            interested[i].instanceEvent(event);

    }


    /**
     * Notify all lifecycle event listeners that a particular event has
     * occurred for this Container.  The default implementation performs
     * this notification synchronously using the calling thread.
     *
     * @param type Event type
     * @param filter The relevant Filter for this event
     * @param request The servlet request we are processing
     * @param response The servlet response we are processing
     * @param exception Exception that occurred
     */
    public void fireInstanceEvent(String type, Filter filter,
                                  ServletRequest request,
                                  ServletResponse response,
                                  Throwable exception) {

        if (listeners.length == 0)
            return;

        InstanceEvent event = new InstanceEvent(wrapper, filter, type,
                                                request, response, exception);
        InstanceListener interested[] = null;
        synchronized (listeners) {
            interested = (InstanceListener[]) listeners.clone();
        }
        for (int i = 0; i < interested.length; i++)
            interested[i].instanceEvent(event);

    }


    /**
     * Notify all lifecycle event listeners that a particular event has
     * occurred for this Container.  The default implementation performs
     * this notification synchronously using the calling thread.
     *
     * @param type Event type
     * @param servlet The relevant Servlet for this event
     */
    public void fireInstanceEvent(String type, Servlet servlet) {

        if (listeners.length == 0)
            return;

        InstanceEvent event = new InstanceEvent(wrapper, servlet, type);
        InstanceListener interested[] = null;
        synchronized (listeners) {
            interested = (InstanceListener[]) listeners.clone();
        }
        for (int i = 0; i < interested.length; i++)
            interested[i].instanceEvent(event);

    }


    /**
     * Notify all lifecycle event listeners that a particular event has
     * occurred for this Container.  The default implementation performs
     * this notification synchronously using the calling thread.
     *
     * @param type Event type
     * @param servlet The relevant Servlet for this event
     * @param exception Exception that occurred
     */
    public void fireInstanceEvent(String type, Servlet servlet,
                                  Throwable exception) {

        if (listeners.length == 0)
            return;

        InstanceEvent event = new InstanceEvent(wrapper, servlet, type,
                                                exception);
        InstanceListener interested[] = null;
        synchronized (listeners) {
            interested = (InstanceListener[]) listeners.clone();
        }
        for (int i = 0; i < interested.length; i++)
            interested[i].instanceEvent(event);

    }


    /**
     * Notify all lifecycle event listeners that a particular event has
     * occurred for this Container.  The default implementation performs
     * this notification synchronously using the calling thread.
     *
     * @param type Event type
     * @param servlet The relevant Servlet for this event
     * @param request The servlet request we are processing
     * @param response The servlet response we are processing
     */
    public void fireInstanceEvent(String type, Servlet servlet,
                                  ServletRequest request,
                                  ServletResponse response) {

        if (listeners.length == 0)
            return;

        InstanceEvent event = new InstanceEvent(wrapper, servlet, type,
                                                request, response);
        InstanceListener interested[] = null;
        synchronized (listeners) {
            interested = (InstanceListener[]) listeners.clone();
        }
        for (int i = 0; i < interested.length; i++)
            interested[i].instanceEvent(event);

    }


    /**
     * Notify all lifecycle event listeners that a particular event has
     * occurred for this Container.  The default implementation performs
     * this notification synchronously using the calling thread.
     *
     * @param type Event type
     * @param servlet The relevant Servlet for this event
     * @param request The servlet request we are processing
     * @param response The servlet response we are processing
     * @param exception Exception that occurred
     */
    public void fireInstanceEvent(String type, Servlet servlet,
                                  ServletRequest request,
                                  ServletResponse response,
                                  Throwable exception) {

        if (listeners.length == 0)
            return;

        InstanceEvent event = new InstanceEvent(wrapper, servlet, type,
                                                request, response, exception);
        InstanceListener interested[] = null;
        synchronized (listeners) {
            interested = (InstanceListener[]) listeners.clone();
        }
        for (int i = 0; i < interested.length; i++)
            interested[i].instanceEvent(event);

    }


    /**
     * Remove a lifecycle event listener from this component.
     *
     * @param listener The listener to remove
     */
    public void removeInstanceListener(InstanceListener listener) {

        synchronized (listeners) {
            int n = -1;
            for (int i = 0; i < listeners.length; i++) {
                if (listeners[i] == listener) {
                    n = i;
                    break;
                }
            }
            if (n < 0)
                return;
            InstanceListener results[] =
              new InstanceListener[listeners.length - 1];
            int j = 0;
            for (int i = 0; i < listeners.length; i++) {
                if (i != n)
                    results[j++] = listeners[i];
            }
            listeners = results;
        }

    }


}
