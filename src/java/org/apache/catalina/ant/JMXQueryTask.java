

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
 * Ant task that implements the JMX Query command 
 * (<code>/jmxproxy/?qry</code>) supported by the Tomcat manager application.
 *
 * @author Vivek Chopra
 * @version $Revision: 1.1.1.1 $
 */
public class JMXQueryTask extends AbstractCatalinaTask {

    // Properties

    /**
     * The JMX query string 
     * @see setQuery()
     */
    protected String query      = null;

    // Public Methods
    
    /**
     * Get method for the JMX query string
     * @return Query string
     */
    public String getQuery () {
        return this.query;
    }

    /**
     * Set method for the JMX query string.
    * <P>Examples of query format:
     * <UL>
     * <LI>*:*</LI>
     * <LI>*:type=RequestProcessor,*</LI>
     * <LI>*:j2eeType=Servlet,*</LI>
     * <LI>Catalina:type=Environment,resourcetype=Global,name=simpleValue</LI>
     * </UL>
     * </P> 
     * @param query JMX Query string
     */
    public void setQuery (String query) {
        this.query = query;
    }

    /**
     * Execute the requested operation.
     *
     * @exception BuildException if an error occurs
     */
    public void execute() throws BuildException {
        super.execute();
        String queryString = (query == null) ? "":("?qry="+query);
        log("Query string is " + queryString); 
        execute ("/jmxproxy/" + queryString);
    }
}
