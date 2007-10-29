

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

package org.apache.catalina.mbeans;


import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.InvalidTargetObjectTypeException;

import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.tomcat5.CoyoteConnector;
import org.apache.tomcat.util.IntrospectionUtils;


/**
 * <p>A <strong>ModelMBean</strong> implementation for the
 * <code>org.apache.coyote.tomcat4.CoyoteConnector</code> component.</p>
 *
 * @author Amy Roh
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:05 $
 */

public class ConnectorMBean extends ClassNameMBean {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a <code>ModelMBean</code> with default
     * <code>ModelMBeanInfo</code> information.
     *
     * @exception MBeanException if the initializer of an object
     *  throws an exception
     * @exception RuntimeOperationsException if an IllegalArgumentException
     *  occurs
     */
    public ConnectorMBean()
        throws MBeanException, RuntimeOperationsException {

        super();

    }


    // ------------------------------------------------------------- Attributes


    /**
     * Obtain and return the value of a specific attribute of this MBean.
     *
     * @param name Name of the requested attribute
     *
     * @exception AttributeNotFoundException if this attribute is not
     *  supported by this MBean
     * @exception MBeanException if the initializer of an object
     *  throws an exception
     * @exception ReflectionException if a Java reflection exception
     *  occurs when invoking the getter
     */
    public Object getAttribute(String name)
        throws AttributeNotFoundException, MBeanException,
        ReflectionException {
		
 	Object attribute = null;
        // Validate the input parameters
        if (name == null)
            throw new RuntimeOperationsException
                (new IllegalArgumentException("Attribute name is null"),
                 "Attribute name is null");
		 
        CoyoteConnector connector = null;
	try {
	    connector = (CoyoteConnector) getManagedResource();
	} catch (InstanceNotFoundException e) {
	    throw new MBeanException(e);
	} catch (InvalidTargetObjectTypeException e) {
	   throw new MBeanException(e);
        } 	    
	
	if (("algorithm").equals(name) || ("keystoreType").equals(name) ||
            ("maxThreads").equals(name) || ("maxSpareThreads").equals(name) ||
            ("minSpareThreads").equals(name)) {
                
            if (("keystoreType").equals(name)) {
                name = "keyType";
            }
                
            ProtocolHandler protocolHandler = connector.getProtocolHandler();
	    /* check the Protocol first, since JkCoyote has an independent
             * configure method.
             */
            try {
                if( protocolHandler != null ) {
                    attribute = IntrospectionUtils.getAttribute(protocolHandler, name);
                }
            } catch (Exception e) {
                throw new MBeanException(e);
            }
            //if( attribute == null ) {
            //    attribute = connector.getProperty(name);
            //}
	} else {
	    attribute = super.getAttribute(name);
	}
	
        return attribute;

    }

    
    /**
     * Set the value of a specific attribute of this MBean.
     *
     * @param attribute The identification of the attribute to be set
     *  and the new value
     *
     * @exception AttributeNotFoundException if this attribute is not
     *  supported by this MBean
     * @exception MBeanException if the initializer of an object
     *  throws an exception
     * @exception ReflectionException if a Java reflection exception
     *  occurs when invoking the getter
     */
     public void setAttribute(Attribute attribute)
        throws AttributeNotFoundException, MBeanException,
        ReflectionException {

        // Validate the input parameters
        if (attribute == null)
            throw new RuntimeOperationsException
                (new IllegalArgumentException("Attribute is null"),
                 "Attribute is null");
        String name = attribute.getName();
        Object value = attribute.getValue();
        if (name == null)
            throw new RuntimeOperationsException
                (new IllegalArgumentException("Attribute name is null"),
                 "Attribute name is null"); 
		 
        CoyoteConnector connector = null;
	try {
	    connector = (CoyoteConnector) getManagedResource();
	} catch (InstanceNotFoundException e) {
	    throw new MBeanException(e);
	} catch (InvalidTargetObjectTypeException e) {
	   throw new MBeanException(e);
        } 	    
	
        if (("algorithm").equals(name) || ("keystoreType").equals(name) ||
            ("maxThreads").equals(name) || ("maxSpareThreads").equals(name) ||
            ("minSpareThreads").equals(name)) {
                
            if (("keystoreType").equals(name)) {
                name = "keyType";
            }
            
            ProtocolHandler protocolHandler = connector.getProtocolHandler();
	    /* check the Protocol first, since JkCoyote has an independent
             * configure method.
             */
            try {
                if( protocolHandler != null ) {
                    IntrospectionUtils.setAttribute(protocolHandler, name, value);
                }   
            } catch (Exception e) {
                throw new MBeanException(e);
            }
  
	} else {
	    super.setAttribute(attribute);
	}
	
    }


    // ------------------------------------------------------------- Operations

    
    
}
