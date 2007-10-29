

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


package org.apache.catalina.deploy;


import org.apache.catalina.util.RequestUtil;
import java.io.Serializable;


/**
 * Representation of a web resource collection for a web application's security
 * constraint, as represented in a <code>&lt;web-resource-collection&gt;</code>
 * element in the deployment descriptor.
 * <p>
 * <b>WARNING</b>:  It is assumed that instances of this class will be created
 * and modified only within the context of a single thread, before the instance
 * is made visible to the remainder of the application.  After that, only read
 * access is expected.  Therefore, none of the read and write access within
 * this class is synchronized.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:04 $
 */

public class SecurityCollection implements Serializable {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new security collection instance with default values.
     */
    public SecurityCollection() {

        this(null, null);

    }


    /**
     * Construct a new security collection instance with specified values.
     *
     * @param name Name of this security collection
     */
    public SecurityCollection(String name) {

        this(name, null);

    }


    /**
     * Construct a new security collection instance with specified values.
     *
     * @param name Name of this security collection
     * @param description Description of this security collection
     */
    public SecurityCollection(String name, String description) {

        super();
        setName(name);
        setDescription(description);

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * Description of this web resource collection.
     */
    private String description = null;


    /**
     * The HTTP methods covered by this web resource collection.
     */
    private String methods[] = new String[0];


    /**
     * The name of this web resource collection.
     */
    private String name = null;


    /**
     * The URL patterns protected by this security collection.
     */
    private String patterns[] = new String[0];


    // ------------------------------------------------------------- Properties


    /**
     * Return the description of this web resource collection.
     */
    public String getDescription() {

        return (this.description);

    }


    /**
     * Set the description of this web resource collection.
     *
     * @param description The new description
     */
    public void setDescription(String description) {

        this.description = description;

    }


    /**
     * Return the name of this web resource collection.
     */
    public String getName() {

        return (this.name);

    }


    /**
     * Set the name of this web resource collection
     *
     * @param name The new name
     */
    public void setName(String name) {

        this.name = name;

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Add an HTTP request method to be part of this web resource collection.
     */
    public void addMethod(String method) {

        if (method == null)
            return;
        String results[] = new String[methods.length + 1];
        for (int i = 0; i < methods.length; i++)
            results[i] = methods[i];
        results[methods.length] = method;
        methods = results;

    }


    /**
     * Add a URL pattern to be part of this web resource collection.
     */
    public void addPattern(String pattern) {

        if (pattern == null)
            return;
        pattern = RequestUtil.URLDecode(pattern);
        String results[] = new String[patterns.length + 1];
        for (int i = 0; i < patterns.length; i++)
            results[i] = patterns[i];
        results[patterns.length] = pattern;
        patterns = results;

    }


    /**
     * Return <code>true</code> if the specified HTTP request method is
     * part of this web resource collection.
     *
     * @param method Request method to check
     */
    public boolean findMethod(String method) {

        if (methods.length == 0)
            return (true);
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].equals(method))
                return (true);
        }
        return (false);

    }


    /**
     * Return the set of HTTP request methods that are part of this web
     * resource collection, or a zero-length array if all request methods
     * are included.
     */
    public String[] findMethods() {

        return (methods);

    }


    /**
     * Is the specified pattern part of this web resource collection?
     *
     * @param pattern Pattern to be compared
     */
    public boolean findPattern(String pattern) {

        for (int i = 0; i < patterns.length; i++) {
            if (patterns[i].equals(pattern))
                return (true);
        }
        return (false);

    }


    /**
     * Return the set of URL patterns that are part of this web resource
     * collection.  If none have been specified, a zero-length array is
     * returned.
     */
    public String[] findPatterns() {

        return (patterns);

    }


    /**
     * Remove the specified HTTP request method from those that are part
     * of this web resource collection.
     *
     * @param method Request method to be removed
     */
    public void removeMethod(String method) {

        if (method == null)
            return;
        int n = -1;
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].equals(method)) {
                n = i;
                break;
            }
        }
        if (n >= 0) {
            int j = 0;
            String results[] = new String[methods.length - 1];
            for (int i = 0; i < methods.length; i++) {
                if (i != n)
                    results[j++] = methods[i];
            }
            methods = results;
        }

    }


    /**
     * Remove the specified URL pattern from those that are part of this
     * web resource collection.
     *
     * @param pattern Pattern to be removed
     */
    public void removePattern(String pattern) {

        if (pattern == null)
            return;
        int n = -1;
        for (int i = 0; i < patterns.length; i++) {
            if (patterns[i].equals(pattern)) {
                n = i;
                break;
            }
        }
        if (n >= 0) {
            int j = 0;
            String results[] = new String[patterns.length - 1];
            for (int i = 0; i < patterns.length; i++) {
                if (i != n)
                    results[j++] = patterns[i];
            }
            patterns = results;
        }

    }


    /**
     * Return a String representation of this security collection.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("SecurityCollection[");
        sb.append(name);
        if (description != null) {
            sb.append(", ");
            sb.append(description);
        }
        sb.append("]");
        return (sb.toString());

    }


}
