

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
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 */


package org.apache.catalina.deploy;

/**
 * Class representing a servlet mapping containing multiple URL patterns.
 * See Servlet 2.5, SRV.18.0.3 ("Multiple Occurrences of Servlet Mappings")
 * for details.
 */
public class ServletMap {
    String servletName;
    String[] urlPatterns = new String[0];
    
    public void setServletName(String name) {
        servletName = name;
    }

    public void addURLPattern(String pattern) {
        String[] results = new String[urlPatterns.length + 1];
        System.arraycopy(urlPatterns, 0, results, 0, urlPatterns.length);
        results[urlPatterns.length] = pattern;
        urlPatterns = results;
    }

    public String getServletName() {
        return servletName;
    }
        
    public String[] getUrlPatterns() {
        return urlPatterns;
    }
}
