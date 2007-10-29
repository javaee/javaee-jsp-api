

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


package org.apache.catalina.realm;


import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.RuleSetBase;
import org.xml.sax.Attributes;


/**
 * <p><strong>RuleSet</strong> for recognizing the users defined in the
 * XML file processed by <code>MemoryRealm</code>.</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:06 $
 */

public class MemoryRuleSet extends RuleSetBase {


    // ----------------------------------------------------- Instance Variables


    /**
     * The matching pattern prefix to use for recognizing our elements.
     */
    protected String prefix = null;


    // ------------------------------------------------------------ Constructor


    /**
     * Construct an instance of this <code>RuleSet</code> with the default
     * matching pattern prefix.
     */
    public MemoryRuleSet() {

        this("tomcat-users/");

    }


    /**
     * Construct an instance of this <code>RuleSet</code> with the specified
     * matching pattern prefix.
     *
     * @param prefix Prefix for matching pattern rules (including the
     *  trailing slash character)
     */
    public MemoryRuleSet(String prefix) {

        super();
        this.namespaceURI = null;
        this.prefix = prefix;

    }


    // --------------------------------------------------------- Public Methods


    /**
     * <p>Add the set of Rule instances defined in this RuleSet to the
     * specified <code>Digester</code> instance, associating them with
     * our namespace URI (if any).  This method should only be called
     * by a Digester instance.</p>
     *
     * @param digester Digester instance to which the new Rule instances
     *  should be added.
     */
    public void addRuleInstances(Digester digester) {

        digester.addRule
            (prefix + "user",
             new MemoryUserRule(digester));

    }


}


/**
 * Private class used when parsing the XML database file.
 */
final class MemoryUserRule extends Rule {


    /**
     * Construct a new instance of this <code>Rule</code>.
     *
     * @param digester The <code>Digester</code> we are associated with.
     */
    public MemoryUserRule(Digester digester) {

        super(digester);

    }


    /**
     * Process a <code>&lt;user&gt;</code> element from the XML database file.
     *
     * @param attributes The attribute list for this element
     */
    public void begin(Attributes attributes) throws Exception {

        String username = attributes.getValue("name");
        if (username == null) {
            username = attributes.getValue("username");
        }
        String password = attributes.getValue("password");
        String roles = attributes.getValue("roles");

        MemoryRealm realm =
            (MemoryRealm) digester.peek(digester.getCount() - 1);
        realm.addUser(username, password, roles);

    }


}
