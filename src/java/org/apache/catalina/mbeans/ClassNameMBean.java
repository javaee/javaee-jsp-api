

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


import javax.management.MBeanException;
import javax.management.RuntimeOperationsException;
import com.sun.org.apache.commons.modeler.BaseModelMBean;


/**
 * <p>A convenience base class for <strong>ModelMBean</strong> implementations
 * where the underlying base class (and therefore the set of supported
 * properties) is different for varying implementations of a standard
 * interface.  For Catalina, that includes at least the following:
 * Connector, Logger, Realm, and Valve.  This class creates an artificial
 * MBean attribute named <code>className</code>, which reports the fully
 * qualified class name of the managed object as its value.</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.2 $ $Date: 2005/12/08 01:27:46 $
 */

public class ClassNameMBean extends BaseModelMBean {


     // ---------------------------------------------------------- Constructors


     /**
      * Construct a <code>ModelMBean</code> with default
      * <code>ModelMBeanInfo</code> information.
      *
      * @exception MBeanException if the initialize of an object
      *  throws an exception
      * @exception RuntimeOperationsException if an IllegalArgumentException
      *  occurs
      */
     public ClassNameMBean()
         throws MBeanException, RuntimeOperationsException {

         super();

     }


     // ------------------------------------------------------------ Properties


     /**
      * Return the fully qualified Java class name of the managed object
      * for this MBean.
      */
     public String getClassName() {

         return (this.resource.getClass().getName());

     }


 }
