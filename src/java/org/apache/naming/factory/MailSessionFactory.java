

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

package org.apache.naming.factory;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import javax.mail.Session;
import javax.naming.Name;
import javax.naming.Context;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/**
 * <p>Factory class that creates a JNDI named JavaMail Session factory,
 * which can be used for managing inbound and outbound electronic mail
 * messages via JavaMail APIs.  All messaging environment properties
 * described in the JavaMail Specification may be passed to the Session
 * factory; however the following properties are the most commonly used:</p>
 * <ul>
 * <li>
 * <li><strong>mail.smtp.host</strong> - Hostname for outbound transport
 *     connections.  Defaults to <code>localhost</code> if not specified.</li>
 * </ul>
 *
 * <p>This factory can be configured in a <code>&lt;DefaultContext&gt;</code>
 * or <code>&lt;Context&gt;</code> element in your <code>conf/server.xml</code>
 * configuration file.  An example of factory configuration is:</p>
 * <pre>
 * &lt;Resource name="mail/smtp" auth="CONTAINER"
 *           type="javax.mail.Session"/&gt;
 * &lt;ResourceParams name="mail/smtp"&gt;
 *   &lt;parameter&gt;
 *     &lt;name&gt;factory&lt;/name&gt;
 *     &lt;value&gt;org.apache.naming.factory.MailSessionFactory&lt;/value&gt;
 *   &lt;/parameter&gt;
 *   &lt;parameter&gt;
 *     &lt;name&gt;mail.smtp.host&lt;/name&gt;
 *     &lt;value&gt;mail.mycompany.com&lt;/value&gt;
 *   &lt;/parameter&gt;
 * &lt;/ResourceParams&gt;
 * </pre>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:15 $
 */

public class MailSessionFactory implements ObjectFactory {


    /**
     * The Java type for which this factory knows how to create objects.
     */
    protected static final String factoryType = "javax.mail.Session";


    /**
     * Create and return an object instance based on the specified
     * characteristics.
     *
     * @param refObj Reference information containing our parameters, or null
     *  if there are no parameters
     * @param name The name of this object, relative to context, or null
     *  if there is no name
     * @param context The context to which name is relative, or null if name
     *  is relative to the default initial context
     * @param env Environment variables, or null if there are none
     *
     * @exception Exception if an error occurs during object creation
     */
    public Object getObjectInstance(Object refObj, Name name, Context context,
				    Hashtable env) throws Exception 
    {

        // Return null if we cannot create an object of the requested type
	final Reference ref = (Reference) refObj;
        if (!ref.getClassName().equals(factoryType))
            return (null);

        // Create a new Session inside a doPrivileged block, so that JavaMail
        // can read its default properties without throwing Security
        // exceptions
        return AccessController.doPrivileged( new PrivilegedAction() {
		public Object run() {

                    // Create the JavaMail properties we will use
                    Properties props = new Properties();
                    props.put("mail.transport.protocol", "smtp");
                    props.put("mail.smtp.host", "localhost");
                    Enumeration attrs = ref.getAll();
                    while (attrs.hasMoreElements()) {
                        RefAddr attr = (RefAddr) attrs.nextElement();
                        if ("factory".equals(attr.getType()))
                            continue;
                        props.put(attr.getType(), (String) attr.getContent());
                    }

                    // Create and return the new Session object
                    Session session = Session.getInstance(props, null);
                    return (session);

		}
	    } );

    }


}
