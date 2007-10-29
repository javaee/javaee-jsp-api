

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


package org.apache.catalina.startup;


import java.lang.reflect.Method;
import org.apache.catalina.Container;
import com.sun.org.apache.commons.digester.Digester;
import com.sun.org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;


/**
 * <p>Rule that copies the <code>parentClassLoader</code> property from the
 * next-to-top item on the stack (which must be a <code>Container</code>)
 * to the top item on the stack (which must also be a
 * <code>Container</code>).</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.2 $ $Date: 2005/12/08 01:28:07 $
 */

public class CopyParentClassLoaderRule extends Rule {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new instance of this Rule.
     *
     * @param digester Digester we are associated with
     */
    public CopyParentClassLoaderRule(Digester digester) {

        super(digester);

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Handle the beginning of an XML element.
     *
     * @param attributes The attributes of this element
     *
     * @exception Exception if a processing error occurs
     */
    public void begin(Attributes attributes) throws Exception {

        if (digester.getDebug() >= 1)
            digester.log("Copying parent class loader");
        Container child = (Container) digester.peek(0);
        Object parent = digester.peek(1);
        Method method =
            parent.getClass().getMethod("getParentClassLoader", new Class[0]);
        ClassLoader classLoader =
            (ClassLoader) method.invoke(parent, new Object[0]);
        child.setParentClassLoader(classLoader);

    }


}
