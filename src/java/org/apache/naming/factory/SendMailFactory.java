

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
import java.util.Hashtable;
import java.util.Properties;
import java.util.Enumeration;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimePartDataSource;
import javax.naming.Name;
import javax.naming.Context;
import javax.naming.Reference;
import javax.naming.RefAddr;
import javax.naming.spi.ObjectFactory;

/**
 * Factory class that creates a JNDI named javamail MimePartDataSource
 * object which can be used for sending email using SMTP.
 * <p>
 * Can be configured in the DefaultContext or Context scope
 * of your server.xml configuration file.
 * <p>
 * Example:
 * <p>
 * <pre>
 * &lt;Resource name="mail/send" auth="CONTAINER"
 *           type="javax.mail.internet.MimePartDataSource"/>
 * &lt;ResourceParams name="mail/send">
 *   &lt;parameter>&lt;name>factory&lt;/name>
 *     &lt;value>org.apache.naming.factory.SendMailFactory&lt;/value>
 *   &lt;/parameter>
 *   &lt;parameter>&lt;name>mail.smtp.host&lt;/name>
 *     &lt;value>your.smtp.host&lt;/value>
 *   &lt;/parameter>
 *   &lt;parameter>&lt;name>mail.smtp.user&lt;/name>
 *     &lt;value>someuser&lt;/value>
 *   &lt;/parameter>
 *   &lt;parameter>&lt;name>mail.from&lt;/name>
 *     &lt;value>someuser@some.host&lt;/value>
 *   &lt;/parameter>
 *   &lt;parameter>&lt;name>mail.smtp.sendpartial&lt;/name>
 *     &lt;value>true&lt;/value>
 *   &lt;/parameter>
 *  &lt;parameter>&lt;name>mail.smtp.dsn.notify&lt;/name>
 *     &lt;value>FAILURE&lt;/value>
 *   &lt;/parameter>
 *   &lt;parameter>&lt;name>mail.smtp.dsn.ret&lt;/name>
 *     &lt;value>FULL&lt;/value>
 *   &lt;/parameter>
 * &lt;/ResourceParams>
 * </pre>
 *
 * @author Glenn Nielsen Rich Catlett
 */

public class SendMailFactory implements ObjectFactory 
{
    // The class name for the javamail MimeMessageDataSource
    protected final String DataSourceClassName = 
	"javax.mail.internet.MimePartDataSource";

    public Object getObjectInstance(Object RefObj, Name Nm, Context Ctx,
				    Hashtable Env) throws Exception 
    {
	final Reference Ref = (Reference)RefObj;

	// Creation of the DataSource is wrapped inside a doPrivileged
	// so that javamail can read its default properties without
	// throwing Security Exceptions
	if (Ref.getClassName().equals(DataSourceClassName)) {
	    return AccessController.doPrivileged( new PrivilegedAction()
	    {
		public Object run() {
        	    // set up the smtp session that will send the message
	            Properties props = new Properties();
		    // enumeration of all refaddr
		    Enumeration list = Ref.getAll();
		    // current refaddr to be set
		    RefAddr refaddr;
	            // set transport to smtp
	            props.put("mail.transport.protocol", "smtp");

		    while (list.hasMoreElements()) {
			refaddr = (RefAddr)list.nextElement();

			// set property
			props.put(refaddr.getType(), (String)refaddr.getContent());
		    }
		    MimeMessage message = new MimeMessage(
			Session.getInstance(props));
		    try {
			String from = (String)Ref.get("mail.from").getContent();
		        message.setFrom(new InternetAddress(from));
		        message.setSubject("");
		    } catch (Exception e) {}
		    MimePartDataSource mds = new MimePartDataSource(
			(MimePart)message);
		    return mds;
		}
	    } );
	}
	else { // We can't create an instance of the DataSource
	    return null;
	}
    }
}
