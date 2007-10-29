

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
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.RuntimeOperationsException;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Logger;
import org.apache.catalina.Realm;
import org.apache.catalina.Valve;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.commons.modeler.BaseModelMBean;
import org.apache.commons.modeler.Registry;
import org.apache.commons.modeler.ManagedBean;


/**
 * <p>A <strong>ModelMBean</strong> implementation for the
 * <code>org.apache.catalina.core.StandardHost</code> component.</p>
 *
 * @author Amy Roh
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:06 $
 */

public class StandardHostMBean extends BaseModelMBean {

    /**
     * The <code>MBeanServer</code> for this application.
     */
    private static MBeanServer mserver = MBeanUtils.createServer();

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
    public StandardHostMBean()
        throws MBeanException, RuntimeOperationsException {

        super();

    }


    // ------------------------------------------------------------- Attributes



    // ------------------------------------------------------------- Operations


   /**
     * Add an alias name that should be mapped to this Host
     *
     * @param alias The alias to be added
     *
     * @exception Exception if an MBean cannot be created or registered
     */
    public void addAlias(String alias)
        throws Exception {

        StandardHost host = (StandardHost) this.resource;
        host.addAlias(alias);

    }


   /**
     * Return the set of alias names for this Host
     *
     * @exception Exception if an MBean cannot be created or registered
     */
    public String [] findAliases()
        throws Exception {

        StandardHost host = (StandardHost) this.resource;
        return host.findAliases();

    }


   /**
     * Return the MBean Names of the Valves assoicated with this Host
     *
     * @exception Exception if an MBean cannot be created or registered
     */
    public String [] getValves()
        throws Exception {

        Registry registry = MBeanUtils.createRegistry();
        StandardHost host = (StandardHost) this.resource;
        String mname = MBeanUtils.createManagedName(host);
        ManagedBean managed = registry.findManagedBean(mname);
        String domain = null;
        if (managed != null) {
            domain = managed.getDomain();
        }
        if (domain == null)
            domain = mserver.getDefaultDomain();
        Valve [] valves = host.getValves();
        String [] mbeanNames = new String[valves.length];
        for (int i = 0; i < valves.length; i++) {
            mbeanNames[i] =
                MBeanUtils.createObjectName(domain, valves[i]).toString();
        }

        return mbeanNames;

    }


   /**
     * Return the specified alias name from the aliases for this Host
     *
     * @param alias Alias name to be removed
     *
     * @exception Exception if an MBean cannot be created or registered
     */
    public void removeAlias(String alias)
        throws Exception {

        StandardHost host = (StandardHost) this.resource;
        host.removeAlias(alias);

    }


}
