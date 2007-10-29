

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

package org.apache.naming;

import javax.naming.Context;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;
import javax.management.MBeanServer;
import javax.management.MBeanRegistration;
import javax.management.AttributeChangeNotification;
import javax.management.Notification;

/**
 * Implementation of the NamingService JMX MBean.
 * 
 * @author <a href="mailto:remm@apache.org">Remy Maucherat</a>
 * @version $Revision: 1.2 $
 */

public final class NamingService
    extends NotificationBroadcasterSupport
    implements NamingServiceMBean, MBeanRegistration {
    
    
    // ----------------------------------------------------- Instance Variables
    
    
    /**
     * Status of the Slide domain.
     */
    private int state = STOPPED;
    
    
    /**
     * Notification sequence number.
     */
    private long sequenceNumber = 0;
    
    
    /**
     * Old URL packages value.
     */
    private String oldUrlValue = "";
    
    
    /**
     * Old initial context value.
     */
    private String oldIcValue = "";
    
    
    // ---------------------------------------------- MBeanRegistration Methods
    
    
    public ObjectName preRegister(MBeanServer server, ObjectName name)
        throws Exception {
        return new ObjectName(OBJECT_NAME);
    }
    
    
    public void postRegister(Boolean registrationDone) {
        if (!registrationDone.booleanValue())
            destroy();
    }
    
    
    public void preDeregister()
        throws Exception {
    }
    
    
    public void postDeregister() {
        destroy();
    }
    
    
    // ----------------------------------------------------- SlideMBean Methods
    
    
    /**
     * Retruns the Catalina component name.
     */
    public String getName() {
        return NAME;
    }
    
    
    /**
     * Returns the state.
     */
    public int getState() {
        return state;
    }
    
    
    /**
     * Returns a String representation of the state.
     */
    public String getStateString() {
        return states[state];
    }
    
    
    /**
     * Start the servlet container.
     */
    public void start()
        throws Exception {
        
        Notification notification = null;
        
        if (state != STOPPED)
            return;
        
        state = STARTING;
        
        // Notifying the MBEan server that we're starting
        
        notification = new AttributeChangeNotification
            (this, sequenceNumber++, System.currentTimeMillis(), 
             "Starting " + NAME, "State", "java.lang.Integer", 
             Integer.valueOf(STOPPED), Integer.valueOf(STARTING));
        sendNotification(notification);
        
        try {
            
            String value = "org.apache.naming";
            String oldValue = System.getProperty(Context.URL_PKG_PREFIXES);
            if (oldValue != null) {
                oldUrlValue = oldValue;
                value = oldValue + ":" + value;
            }
            System.setProperty(Context.URL_PKG_PREFIXES, value);
            
            oldValue = System.getProperty(Context.INITIAL_CONTEXT_FACTORY);
            if (oldValue != null) {
                oldIcValue = oldValue;
            } else {
                System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                                   Constants.Package 
                                   + ".java.javaURLContextFactory");
            }
            
        } catch (Throwable t) {
            state = STOPPED;
            notification = new AttributeChangeNotification
                (this, sequenceNumber++, System.currentTimeMillis(), 
                 "Stopped " + NAME, "State", "java.lang.Integer", 
                 Integer.valueOf(STARTING), Integer.valueOf(STOPPED));
            sendNotification(notification);
        }
        
        state = STARTED;
        notification = new AttributeChangeNotification
            (this, sequenceNumber++, System.currentTimeMillis(), 
             "Started " + NAME, "State", "java.lang.Integer", 
             Integer.valueOf(STARTING), Integer.valueOf(STARTED));
        sendNotification(notification);
        
    }
    
    
    /**
     * Stop the servlet container.
     */
    public void stop() {
        
        Notification notification = null;
        
        if (state != STARTED)
            return;
        
        state = STOPPING;
        
        notification = new AttributeChangeNotification
            (this, sequenceNumber++, System.currentTimeMillis(), 
             "Stopping " + NAME, "State", "java.lang.Integer", 
             Integer.valueOf(STARTED), Integer.valueOf(STOPPING));
        sendNotification(notification);
        
        try {
            
            System.setProperty(Context.URL_PKG_PREFIXES, oldUrlValue);
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY, oldIcValue);
            
        } catch (Throwable t) {
            
            // FIXME
            t.printStackTrace();
            
        }
        
        state = STOPPED;
        
        notification = new AttributeChangeNotification
            (this, sequenceNumber++, System.currentTimeMillis(), 
             "Stopped " + NAME, "State", "java.lang.Integer", 
             Integer.valueOf(STOPPING), Integer.valueOf(STOPPED));
        sendNotification(notification);
        
    }
    
    
    /**
     * Destroy servlet container (if any is running).
     */
    public void destroy() {
        
        if (getState() != STOPPED)
            stop();
        
    }
    
    
}
