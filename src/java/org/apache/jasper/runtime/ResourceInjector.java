/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
