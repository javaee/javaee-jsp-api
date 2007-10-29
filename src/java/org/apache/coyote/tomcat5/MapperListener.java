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
package org.apache.coyote.tomcat5;

import java.util.Iterator;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.modeler.Registry;

import org.apache.tomcat.util.http.mapper.Mapper;

import org.apache.tomcat.util.res.StringManager;


/**
 * Mapper listener.
 *
 * @author Remy Maucherat
 * @author Costin Manolache
 */
public class MapperListener
    implements NotificationListener 
 {
    private static Log log = LogFactory.getLog(MapperListener.class);


    // ----------------------------------------------------- Instance Variables
    /**
     * Associated mapper.
     */
    protected Mapper mapper = null;

    /**
     * MBean server.
     */
    protected MBeanServer mBeanServer = null;


    /**
     * The string manager for this package.
     */
    private StringManager sm =
        StringManager.getManager(Constants.Package);

    // It should be null - and fail if not set
    private String domain="*";
    private String engine="*";


    // BEGIN S1AS 5000999
    private int port;
    private String defaultHost;
    // END S1AS 5000999


    // ----------------------------------------------------------- Constructors


    /**
     * Create mapper listener.
     */
    public MapperListener(Mapper mapper) {
        this.mapper = mapper;
    }


    // --------------------------------------------------------- Public Methods

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    // BEGIN S1AS 5000999
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDefaultHost() {
        return defaultHost;
    }

    public void setDefaultHost(String defaultHost) {
        this.defaultHost = defaultHost;
    }
    // END S1AS 5000999

    /**
     * Initialize associated mapper.
     */
    public void init() {

        try {

            mBeanServer = Registry.getServer();

            registerEngine();

            // Query hosts
            String onStr = domain + ":type=Host,*";
            ObjectName objectName = new ObjectName(onStr);
            Set set = mBeanServer.queryMBeans(objectName, null);
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                ObjectInstance oi = (ObjectInstance) iterator.next();
                registerHost(oi.getObjectName());
            }


            // Query contexts
            onStr = "*:j2eeType=WebModule,*";
            objectName = new ObjectName(onStr);
            set = mBeanServer.queryMBeans(objectName, null);
            iterator = set.iterator();
            while (iterator.hasNext()) {
                ObjectInstance oi = (ObjectInstance) iterator.next();
                registerContext(oi.getObjectName());
            }

            // Query wrappers
            onStr = "*:j2eeType=Servlet,*";
            objectName = new ObjectName(onStr);
            set = mBeanServer.queryMBeans(objectName, null);
            iterator = set.iterator();
            while (iterator.hasNext()) {
                ObjectInstance oi = (ObjectInstance) iterator.next();
                registerWrapper(oi.getObjectName());
            }

            onStr = "JMImplementation:type=MBeanServerDelegate";
            objectName = new ObjectName(onStr);
            mBeanServer.addNotificationListener(objectName, this, null, null);

        } catch (Exception e) {
            log.warn("Error registering contexts",e);
        }

    }


    // ------------------------------------------- NotificationListener Methods


    public void handleNotification(Notification notification,
                                   java.lang.Object handback) {

        if (notification instanceof MBeanServerNotification) {
            ObjectName objectName = 
                ((MBeanServerNotification) notification).getMBeanName();
            String j2eeType = objectName.getKeyProperty("j2eeType");
            String engineName = null;
            if (j2eeType != null) {
                if ((j2eeType.equals("WebModule")) || 
                    (j2eeType.equals("Servlet"))) {
                    if (mBeanServer.isRegistered(objectName)) {
                        try {
                            engineName = (String)
                                mBeanServer.getAttribute(objectName, "engineName");
                        } catch (Exception e) {
                            // Ignore  
                        }
                    }
                }
            }

            // At deployment time, engineName is always = null.
            if ( (!"*".equals(domain)) &&
                 ( !domain.equals(objectName.getDomain()) ) &&
                 ( (!domain.equals(engineName) ) &&
                   (engineName != null) ) )  {
                return;
            }

            log.debug( "Handle " + objectName );    
            if (notification.getType().equals
                (MBeanServerNotification.REGISTRATION_NOTIFICATION)) {
                String type=objectName.getKeyProperty("type");
                if( "Host".equals( type )) {
                    try {
                        registerHost(objectName);
                    } catch (Exception e) {
                        log.warn("Error registering Host " + objectName, e);  
                    }
                }
    
                if (j2eeType != null) {
                    if (j2eeType.equals("WebModule")) {
                        try {
                            registerContext(objectName);
                        } catch (Throwable t) {
                            log.warn("Error registering Context " + objectName,t);
                        }
                    } else if (j2eeType.equals("Servlet")) {
                        try {
                            registerWrapper(objectName);
                        } catch (Throwable t) {
                            log.warn("Error registering Wrapper " + objectName,t);
                        }
                    }
                }
            } else if (notification.getType().equals
                       (MBeanServerNotification.UNREGISTRATION_NOTIFICATION)) {
                String type=objectName.getKeyProperty("type");
                if( "Host".equals( type )) {
                    try {
                        unregisterHost(objectName);
                    } catch (Exception e) {
                        log.warn("Error unregistering Host " + objectName,e);  
                    }
                }
 
                if (j2eeType != null) {
                    if (j2eeType.equals("WebModule")) {
                        try {
                            unregisterContext(objectName);
                        } catch (Throwable t) {
                            log.warn("Error unregistering webapp " + objectName,t);
                        }
                    }
                }
            }
        }

    }


    // ------------------------------------------------------ Protected Methods

    private void registerEngine()
        throws Exception
    {
        ObjectName engineName = new ObjectName
            (domain + ":type=Engine");
        if ( ! mBeanServer.isRegistered(engineName)) return;
        // BEGIN S1AS 5000999
        /*
        String defaultHost =
            (String) mBeanServer.getAttribute(engineName, "defaultHost");
	*/
        if (defaultHost == null) {
            defaultHost = 
                (String) mBeanServer.getAttribute(engineName, "defaultHost");
        }
        // END S1AS 5000999

        ObjectName hostName = new ObjectName
             (domain + ":type=Host," + "host=" + defaultHost);

        if (!mBeanServer.isRegistered(hostName)) {

            // Get the hosts' list
            String onStr = domain + ":type=Host,*";
            ObjectName objectName = new ObjectName(onStr);
            Set set = mBeanServer.queryMBeans(objectName, null);
            Iterator iterator = set.iterator();
            String[] aliases;
            boolean isRegisteredWithAlias = false;
            
            while (iterator.hasNext()) {

                if (isRegisteredWithAlias) break;
            
                ObjectInstance oi = (ObjectInstance) iterator.next();
                hostName = oi.getObjectName();
                aliases = (String[])
                    mBeanServer.invoke(hostName, "findAliases", null, null);

                for (int i=0; i < aliases.length; i++){
                    if (aliases[i].equalsIgnoreCase(defaultHost)){
                        isRegisteredWithAlias = true;
                        break;
                    }
                }
            }
            
            if (!isRegisteredWithAlias)
                log.warn("Unknown default host: " + defaultHost);
        }

        // This should probablt be called later 
        if( defaultHost != null ) {
            mapper.setDefaultHostName(defaultHost);
        }
    }

    /**
     * Register host.
     */
    private void registerHost(ObjectName objectName)
        throws Exception {
        String name=objectName.getKeyProperty("host");
        if( name != null ) {
            // BEGIN S1AS 5000999
            /*
             * Register the given Host only if one of its associated port
             * numbers matches the port number of this MapperListener
             */
            int[] ports = (int[]) mBeanServer.invoke
                    (objectName, "findPorts", null, null);
            boolean portMatch = false;
            if (ports != null) {
                for (int i=0; i<ports.length; i++) {
                    if (ports[i] == this.port) {
                        portMatch = true;
                        break;
                    }
                }
            }
            if (!portMatch) {
                return;
            }
            // END S1AS 5000999
            String[] aliases = (String[])
                mBeanServer.invoke(objectName, "findAliases", null, null);
            mapper.addHost(name, aliases, objectName);
        }
    }


    /**
     * Unregister host.
     */
    private void unregisterHost(ObjectName objectName)
        throws Exception {
        String name=objectName.getKeyProperty("host");
        // BEGIN S1AS 5000999
        if (name != null) {
            int[] ports = (int[]) mBeanServer.invoke
                    (objectName, "findPorts", null, null);
            boolean portMatch = false;
            if (ports != null) {
                for (int i=0; i<ports.length; i++) {
                    if (ports[i] == this.port) {
                        portMatch = true;
                        break;
                    }
                }
            }
            if (!portMatch) {
                return;
            }
        }
        // END S1AS 5000999
        mapper.removeHost(name);
    }


    /**
     * Register context.
     */
    private void registerContext(ObjectName objectName)
        throws Exception {

        String name = objectName.getKeyProperty("name");
        
        // If the domain is the same with ours or the engine 
        // name attribute is the same... - then it's ours
        String targetDomain=objectName.getDomain();
        if( ! domain.equals( targetDomain )) {
            try {
                targetDomain = (String) mBeanServer.getAttribute
                    (objectName, "engineName");
            } catch (Exception e) {
                // Ignore
            }
            if( ! domain.equals( targetDomain )) {
                // not ours
                return;
            }
        }

        String hostName = null;
        String contextName = null;
        if (name.startsWith("//")) {
            name = name.substring(2);
        }
        int slash = name.indexOf("/");
        if (slash != -1) {
            hostName = name.substring(0, slash);
            contextName = name.substring(slash);
        } else {
            return;
        }
        // Special case for the root context
        if (contextName.equals("/")) {
            contextName = "";
        }

        log.debug(sm.getString
                  ("mapperListener.registerContext", contextName));

        Object context = 
            mBeanServer.invoke(objectName, "findMappingObject", null, null);
            //mBeanServer.getAttribute(objectName, "mappingObject");
        javax.naming.Context resources = (javax.naming.Context)
            mBeanServer.invoke(objectName, "findStaticResources", null, null);
            //mBeanServer.getAttribute(objectName, "staticResources");
        String[] welcomeFiles = (String[])
            mBeanServer.getAttribute(objectName, "welcomeFiles");

        mapper.addContext(hostName, contextName, context, 
                          welcomeFiles, resources);

    }


    /**
     * Unregister context.
     */
    private void unregisterContext(ObjectName objectName)
        throws Exception {

        String name = objectName.getKeyProperty("name");

        // If the domain is the same with ours or the engine 
        // name attribute is the same... - then it's ours
        String targetDomain=objectName.getDomain();
        if( ! domain.equals( targetDomain )) {
            try {
                targetDomain = (String) mBeanServer.getAttribute
                    (objectName, "engineName");
            } catch (Exception e) {
                // Ignore
            }
            if( ! domain.equals( targetDomain )) {
                // not ours
                return;
            }
        }

        String hostName = null;
        String contextName = null;
        if (name.startsWith("//")) {
            name = name.substring(2);
        }
        int slash = name.indexOf("/");
        if (slash != -1) {
            hostName = name.substring(0, slash);
            contextName = name.substring(slash);
        } else {
            return;
        }
        // Special case for the root context
        if (contextName.equals("/")) {
            contextName = "";
        }

        log.debug(sm.getString
                  ("mapperListener.unregisterContext", contextName));

        mapper.removeContext(hostName, contextName);

    }


    /**
     * Register wrapper.
     */
    private void registerWrapper(ObjectName objectName)
        throws Exception {
    
        // If the domain is the same with ours or the engine 
        // name attribute is the same... - then it's ours
        String targetDomain=objectName.getDomain();
        if( ! domain.equals( targetDomain )) {
            try {
                targetDomain=(String) mBeanServer.getAttribute(objectName, "engineName");
            } catch (Exception e) {
                // Ignore
            }
            if( ! domain.equals( targetDomain )) {
                // not ours
                return;
            }
            
        }

        String wrapperName = objectName.getKeyProperty("name");
        String name = objectName.getKeyProperty("WebModule");

        String hostName = null;
        String contextName = null;
        if (name.startsWith("//")) {
            name = name.substring(2);
        }
        int slash = name.indexOf("/");
        if (slash != -1) {
            hostName = name.substring(0, slash);
            contextName = name.substring(slash);
        } else {
            return;
        }
        // Special case for the root context
        if (contextName.equals("/")) {
            contextName = "";
        }

        log.debug(sm.getString
                  ("mapperListener.registerWrapper", 
                   wrapperName, contextName));

        String[] mappings = (String[])
            mBeanServer.invoke(objectName, "findMappings", null, null);
        Object wrapper = 
            mBeanServer.invoke(objectName, "findMappingObject", null, null);

        for (int i = 0; i < mappings.length; i++) {
            boolean jspWildCard = (wrapperName.equals("jsp")
                                   && mappings[i].endsWith("/*"));
            mapper.addWrapper(hostName, contextName, mappings[i], wrapper,
                              jspWildCard);
        }

    }


}
