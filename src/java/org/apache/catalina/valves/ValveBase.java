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
import javax.servlet.ServletException;
import javax.management.ObjectName;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;

import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Valve;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Engine;
import org.apache.catalina.Service;
import org.apache.catalina.Host;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.ContainerBase;
import org.apache.catalina.util.StringManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Convenience base class for implementations of the <b>Valve</b> interface.
 * A subclass <strong>MUST</strong> implement an <code>invoke()</code>
 * method to provide the required functionality, and <strong>MAY</strong>
 * implement the <code>Lifecycle</code> interface to provide configuration
 * management and lifecycle support.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.2 $ $Date: 2005/04/29 01:28:41 $
 */

public abstract class ValveBase
    implements Contained, Valve, MBeanRegistration {
    private static Log log = LogFactory.getLog(ValveBase.class);

    //------------------------------------------------------ Instance Variables


    /**
     * The Container whose pipeline this Valve is a component of.
     */
    protected Container container = null;


    /**
     * The debugging detail level for this component.
     */
    protected int debug = 0;


    /**
     * Descriptive information about this Valve implementation.  This value
     * should be overridden by subclasses.
     */
    protected static String info =
        "org.apache.catalina.core.ValveBase/1.0";


    /**
     * The string manager for this package.
     */
    protected final static StringManager sm =
        StringManager.getManager(Constants.Package);


    //-------------------------------------------------------------- Properties


    /**
     * Return the Container with which this Valve is associated, if any.
     */
    public Container getContainer() {

        return (container);

    }


    /**
     * Set the Container with which this Valve is associated, if any.
     *
     * @param container The new associated container
     */
    public void setContainer(Container container) {

        this.container = container;

    }


   /**
     * Return the debugging detail level for this component.
     */
    public int getDebug() {

        return (this.debug);

    }


    /**
     * Set the debugging detail level for this component.
     *
     * @param debug The new debugging detail level
     */
    public void setDebug(int debug) {

        this.debug = debug;

    }


    /**
     * Return descriptive information about this Valve implementation.
     */
    public String getInfo() {

        return (info);

    }


    //---------------------------------------------------------- Public Methods


    /**
     * The implementation-specific logic represented by this Valve.  See the
     * Valve description for the normal design patterns for this method.
     * <p>
     * This method <strong>MUST</strong> be provided by a subclass.
     *
     * @param request The servlet request to be processed
     * @param response The servlet response to be created
     * @param context The valve context used to invoke the next valve
     *  in the current processing pipeline
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    // START OF IASRI 4665318
    public abstract int invoke(Request request, Response response)
        throws IOException, ServletException;


    /**
     * A post-request processing implementation that does nothing.
     *
     * Very few Valves override this behaviour as most Valve logic
     * is used for request processing.
     */
    public void postInvoke(Request request, Response response)
        throws IOException, ServletException {


    }
    // END OF IASRI 4665318

    // -------------------- JMX and Registration  --------------------
    protected String domain;
    protected ObjectName oname;
    protected MBeanServer mserver;
    protected ObjectName controller;

    public ObjectName getObjectName() {
        return oname;
    }

    public void setObjectName(ObjectName oname) {
        this.oname = oname;
    }

    public String getDomain() {
        return domain;
    }

    public ObjectName preRegister(MBeanServer server,
                                  ObjectName name) throws Exception {
        oname=name;
        mserver=server;
        domain=name.getDomain();


        return name;
    }

    public void postRegister(Boolean registrationDone) {
    }

    public void preDeregister() throws Exception {
    }

    public void postDeregister() {
    }

    public ObjectName getController() {
        return controller;
    }

    public void setController(ObjectName controller) {
        this.controller = controller;
    }

    /** From the name, extract the parent object name
     *
     * @param valveName
     * @return
     */
    public ObjectName getParentName( ObjectName valveName ) {

        return null;
    }

    public ObjectName createObjectName(String domain, ObjectName parent)
            throws MalformedObjectNameException
    {
        Container container=this.getContainer();
        if( container == null || ! (container instanceof ContainerBase) )
            return null;
        ContainerBase containerBase=(ContainerBase)container;
        Pipeline pipe=containerBase.getPipeline();
        Valve valves[]=pipe.getValves();

        /* Compute the "parent name" part */
        String parentName="";
        if (container instanceof Engine) {
        } else if (container instanceof Host) {
            parentName=",host=" +container.getName();
        } else if (container instanceof Context) {
                    String path = ((Context)container).getPath();
                    if (path.length() < 1) {
                        path = "/";
                    }
                    Host host = (Host) container.getParent();
                    parentName=",path=" + path + ",host=" +
                            host.getName();
        } else if (container instanceof Wrapper) {
            Context ctx = (Context) container.getParent();
            String path = ctx.getPath();
            if (path.length() < 1) {
                path = "/";
            }
            Host host = (Host) ctx.getParent();
            parentName=",servlet=" + container.getName() +
                    ",path=" + path + ",host=" + host.getName();
        }
        log.debug("valve parent=" + parentName + " " + parent);

        String className=this.getClass().getName();
        int period = className.lastIndexOf('.');
        if (period >= 0)
            className = className.substring(period + 1);

        int seq=0;
        for( int i=0; i<valves.length; i++ ) {
            // Find other valves with the same name
            if (valves[i]==this) {
                break;
            }
            if( valves[i]!=null &&
                    valves[i].getClass() == this.getClass()) {
                log.debug("Duplicate " + valves[i] + " " + this + " " + container);
                seq++;
            }
        }
        String ext="";
        if( seq > 0 ) {
            ext=",seq=" + seq;
        }

        ObjectName objectName = 
            new ObjectName( domain + ":type=Valve,name=" + className + ext + parentName);
        log.debug("valve objectname = "+objectName);
        return objectName;
    }

    // -------------------- JMX data  --------------------

    public ObjectName getContainerName() {
        if( container== null) return null;
        return ((ContainerBase)container).getJmxName();
    }
}
