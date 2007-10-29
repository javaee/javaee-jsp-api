

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
package org.apache.jasper.runtime;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletContext;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.JspEngineInfo;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspApplicationContext;

import org.apache.jasper.Constants;
import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;

/**
 * Implementation of JspFactory.
 *
 * @author Anil K. Vijendran
 * @author Kin-man Chung
 */
public class JspFactoryImpl extends JspFactory {

    // Logger
    private static Log log = LogFactory.getLog(JspFactoryImpl.class);

    private static final String SPEC_VERSION = "2.1";
    private static final boolean USE_POOL = true;

    // Per-thread pool of PageContext objects
    private ThreadLocal pool = new ThreadLocal() {
        protected synchronized Object initialValue() {
            return new LinkedList<PageContext>();
        }
    };
    
    public PageContext getPageContext(Servlet servlet,
				      ServletRequest request,
                                      ServletResponse response,
                                      String errorPageURL,                    
                                      boolean needsSession,
				      int bufferSize,
                                      boolean autoflush) {

	if (Constants.IS_SECURITY_ENABLED) {
	    PrivilegedGetPageContext dp = new PrivilegedGetPageContext(
		(JspFactoryImpl)this, servlet, request, response, errorPageURL,
                needsSession, bufferSize, autoflush);
	    return (PageContext)AccessController.doPrivileged(dp);
	} else {
	    return internalGetPageContext(servlet, request, response,
					  errorPageURL, needsSession,
					  bufferSize, autoflush);
	}
    }

    public void releasePageContext(PageContext pc) {
	if( pc == null )
	    return;
        if (Constants.IS_SECURITY_ENABLED) {
            PrivilegedReleasePageContext dp = new PrivilegedReleasePageContext(
                (JspFactoryImpl)this,pc);
            AccessController.doPrivileged(dp);
        } else {
            internalReleasePageContext(pc);
	}
    }

    public JspEngineInfo getEngineInfo() {
        return new JspEngineInfo() {
		public String getSpecificationVersion() {
		    return SPEC_VERSION;
		}
	    };
    }

    public JspApplicationContext getJspApplicationContext
            (ServletContext context) {
        return JspApplicationContextImpl.findJspApplicationContext(context);
    }

    private PageContext internalGetPageContext(Servlet servlet,
					       ServletRequest request,
					       ServletResponse response, 
					       String errorPageURL, 
					       boolean needsSession,
					       int bufferSize, 
					       boolean autoflush) {
        try {
	    PageContext pc = null;
	    if( USE_POOL ) {
                LinkedList<PageContext> pcPool = (LinkedList<PageContext>)
                                                    pool.get();
                if (!pcPool.isEmpty()) {
                    pc = pcPool.removeFirst();
                }
                if (pc == null) {
                    pc = new PageContextImpl(this);
                }
	    } else {
		pc = new PageContextImpl(this);
	    }
	    pc.initialize(servlet, request, response, errorPageURL, 
                          needsSession, bufferSize, autoflush);
            return pc;
        } catch (Throwable ex) {
            /* FIXME: need to do something reasonable here!! */
            log.fatal("Exception initializing page context", ex);
            return null;
        }
    }

    private void internalReleasePageContext(PageContext pc) {
        pc.release();
	if (USE_POOL && (pc instanceof PageContextImpl)) {
            LinkedList<PageContext> pcPool = (LinkedList<PageContext>) pool.get();
            pcPool.addFirst(pc);
	}
    }

    private class PrivilegedGetPageContext implements PrivilegedAction {

	private JspFactoryImpl factory;
	private Servlet servlet;
	private ServletRequest request;
	private ServletResponse response;
	private String errorPageURL;
	private boolean needsSession;
	private int bufferSize;
	private boolean autoflush;

	PrivilegedGetPageContext(JspFactoryImpl factory,
				 Servlet servlet,
				 ServletRequest request,
				 ServletResponse response,
				 String errorPageURL,
				 boolean needsSession,
				 int bufferSize,
				 boolean autoflush) {
	    this.factory = factory;
	    this.servlet = servlet;
	    this.request = request;
	    this.response = response;
	    this.errorPageURL = errorPageURL;
	    this.needsSession = needsSession;
	    this.bufferSize = bufferSize;
	    this.autoflush = autoflush;
	}
 
	public Object run() {
	    return factory.internalGetPageContext(servlet,
						  request,
						  response,
						  errorPageURL,
						  needsSession,
						  bufferSize,
						  autoflush);
	}
    }

    private class PrivilegedReleasePageContext implements PrivilegedAction {

        private JspFactoryImpl factory;
	private PageContext pageContext;

        PrivilegedReleasePageContext(JspFactoryImpl factory,
				     PageContext pageContext) {
            this.factory = factory;
            this.pageContext = pageContext;
        }

        public Object run() {
            factory.internalReleasePageContext(pageContext);
	    return null;
        }
    }
}
