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
 */

package org.apache.jasper.runtime;

import javax.servlet.ServletContext;
import javax.servlet.jsp.tagext.JspTag;

/**
 * Interface for injecting injectable resources into tag handler instances.
 *
 * @author Jan Luehe
 */
public interface ResourceInjector {

    /**
     * Associates this ResourceInjector with the component environment of the
     * given servlet context.
     *
     * @param servletContext The servlet context 
     */
    public void setContext(ServletContext servletContext);


    /**
     * Injects the injectable resources from the component environment 
     * associated with this ResourceInjector into the given tag handler
     * instance. 
     *
     * @param handler The tag handler instance to be injected
     *
     * @throws Exception if an error occurs during injection
     */
    public void inject(JspTag handler) throws Exception;

}
