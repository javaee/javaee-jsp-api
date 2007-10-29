

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
package org.apache.catalina.ssi;

import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.util.RequestUtil;

public class SSIServletRequestUtil {
    /**
     * Return the relative path associated with this servlet.
     *
     * Taken from DefaultServlet.java.  Perhaps this should be put in
     * org.apache.catalina.util somewhere?  Seems like it would be widely used.
     *
     * @param request The servlet request we are processing
     */
    public static String getRelativePath(HttpServletRequest request) {

        // Are we being processed by a RequestDispatcher.include()?
        if (request.getAttribute("javax.servlet.include.request_uri")!=null) {
            String result = (String)
                request.getAttribute("javax.servlet.include.path_info");
            if (result == null)
                result = (String)
                    request.getAttribute("javax.servlet.include.servlet_path");
            if ((result == null) || (result.equals("")))
                result = "/";
            return (result);
        }

        // No, extract the desired path directly from the request
        String result = request.getPathInfo();
        if (result == null) {
            result = request.getServletPath();
        }
        if ((result == null) || (result.equals(""))) {
            result = "/";
        }
        return normalize(result);
    }

    /**
     * Return a context-relative path, beginning with a "/", that represents
     * the canonical version of the specified path after ".." and "." elements
     * are resolved out.  If the specified path attempts to go outside the
     * boundaries of the current context (i.e. too many ".." path elements
     * are present), return <code>null</code> instead.
     *
     * This normalize should be the same as DefaultServlet.normalize, which is almost the same
     * ( see source code below ) as RequestUtil.normalize.  Do we need all this duplication?
     *
     * @param path Path to be normalized
     */
    public static String normalize(String path) {
        if (path == null)
            return null;

	String normalized = path;

	//Why doesn't RequestUtil do this??

        // Normalize the slashes and add leading slash if necessary
        if ( normalized.indexOf('\\') >= 0 )
            normalized = normalized.replace( '\\', '/' );

	normalized = RequestUtil.normalize( path );
	return normalized;
    }
}
