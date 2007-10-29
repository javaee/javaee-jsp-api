

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
 * Representation of a filter mapping for a web application, as represented
 * in a <code>&lt;filter-mapping&gt;</code> element in the deployment
 * descriptor.  Each filter mapping must contain a filter name and any 
 * number of URL patterns and servlet names.
 *
 */

public class FilterMaps {

    private String[] urlPatterns = new String[0];
    private String[] servletNames = new String[0];
    private String filterName = null;
    private FilterMap fmap = new FilterMap();

    // ------------------------------------------------------------ Properties
    
    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String getFilterName() {
        return filterName;
    }

    public void addServletName(String servletName) {
        String[] results = new String[servletNames.length + 1];
        System.arraycopy(servletNames, 0, results, 0, servletNames.length);
        results[servletNames.length] = servletName;
        servletNames = results;
    }

    public String[] getServletNames() {
        return servletNames;
    }

    public void addURLPattern(String urlPattern) {
        String[] results = new String[urlPatterns.length + 1];
        System.arraycopy(urlPatterns, 0, results, 0, urlPatterns.length);
        results[urlPatterns.length] = urlPattern;
        urlPatterns = results;
    }

    public String[] getURLPatterns() {
        return urlPatterns;
    }
    
    public void setDispatcher(String dispatcherString) {
        fmap.setDispatcher(dispatcherString);
    }

    public int getDispatcherMapping() {
        return fmap.getDispatcherMapping();
    }
}
