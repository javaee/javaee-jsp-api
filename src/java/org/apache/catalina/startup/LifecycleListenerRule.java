

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


import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import com.sun.org.apache.commons.digester.Digester;
import com.sun.org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;


/**
 * <p>Rule that creates a new <code>LifecycleListener</code> instance,
 * and associates it with the top object on the stack (which must
 * implement <code>LifecycleListener</code>).</p>
 */

public class LifecycleListenerRule extends Rule {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new instance of this Rule.
     *
     * @param digester Digester we are associated with
     * @param listenerClass Default name of the LifecycleListener
     *  implementation class to be created
     * @param attributeName Name of the attribute that optionally
     *  includes an override name of the LifecycleListener class
     */
    public LifecycleListenerRule(Digester digester, String listenerClass,
                                 String attributeName) {

        super(digester);
        this.listenerClass = listenerClass;
        this.attributeName = attributeName;

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The attribute name of an attribute that can override the
     * implementation class name.
     */
    private String attributeName;


    /**
     * The name of the <code>LifecycleListener</code> implementation class.
     */
    private String listenerClass;


    // --------------------------------------------------------- Public Methods


    /**
     * Handle the beginning of an XML element.
     *
     * @param attributes The attributes of this element
     *
     * @exception Exception if a processing error occurs
     */
    public void begin(Attributes attributes) throws Exception {

        // Instantiate a new LifecyleListener implementation object
        String className = listenerClass;
        if (attributeName != null) {
            String value = attributes.getValue(attributeName);
            if (value != null)
                className = value;
        }
        Class clazz = Class.forName(className);
        LifecycleListener listener =
            (LifecycleListener) clazz.newInstance();

        // Add this LifecycleListener to our associated component
        Lifecycle lifecycle = (Lifecycle) digester.peek();
        lifecycle.addLifecycleListener(listener);

    }


}
