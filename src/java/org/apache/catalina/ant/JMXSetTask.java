

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


package org.apache.catalina.ant;


import java.net.URLEncoder;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;


/**
 * Ant task that implements the JMX Set command (<code>/jmxproxy/?set</code>)
 * supported by the Tomcat manager application.
 *
 * @author Vivek Chopra
 * @version $Revision: 1.1.1.1 $
 */
public class JMXSetTask extends AbstractCatalinaTask {

    // Properties

    /**
     * The full bean name
     */
    protected String bean      = null;

    /**
     * The attribute you wish to alter
     */
    protected String attribute = null;

    /**
     * The new value for the attribute
     */
    protected String value     = null;

    // Public Methods
    
    /**
     * Get method for the bean name
     * @return Bean name
     */
    public String getBean () {
        return this.bean;
    }

    /**
     * Set method for the bean name
     * @param bean Bean name
     */
    public void setBean (String bean) {
        this.bean = bean;
    }

    /**
     * Get method for the attribute name
     * @return Attribute name
     */
    public String getAttribute () {
        return this.attribute;
    }

    /**
     * Set method for the attribute name
     * @param attribute Attribute name
     */
    public void setAttribute (String attribute) {
        this.attribute = attribute;
    }

    /**
     * Get method for the attribute value
     * @return Attribute value
     */
    public String getValue () {
        return this.value;
    }

    /**
     * Set method for the attribute value
     * @param attribute Attribute value
     */
    public void setValue (String value) {
        this.value = value;
    }

    /**
     * Execute the requested operation.
     *
     * @exception BuildException if an error occurs
     */
    public void execute() throws BuildException {
        super.execute();
        if (bean == null || attribute == null || value == null) {
            throw new BuildException
                ("Must specify 'bean', 'attribute' and 'value' attributes");
        }
        log("Setting attribute " + attribute +
                            " in bean " + bean +
                            " to " + value); 
        execute("/jmxproxy/?set=" + bean 
                + "&att=" + attribute 
                + "&val=" + value);
    }
}
