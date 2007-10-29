

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

package org.apache.jasper.servlet;

// START PWC 6300204
import java.io.FileNotFoundException;
// END PWC 6300204
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

// START SJSWS 6232180
import java.util.HashSet;
import java.util.StringTokenizer;
// END SJSWS 6232180
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;

import org.apache.jasper.Constants;
import org.apache.jasper.EmbeddedServletOptions;
import org.apache.jasper.Options;
import org.apache.jasper.compiler.JspRuntimeContext;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.runtime.JspApplicationContextImpl;

/**
 * The JSP engine (a.k.a Jasper).
 *
 * The servlet container is responsible for providing a
 * URLClassLoader for the web application context Jasper
 * is being used in. Jasper will try get the Tomcat
 * ServletContext attribute for its ServletContext class
 * loader, if that fails, it uses the parent class loader.
 * In either case, it must be a URLClassLoader.
 *
 * @author Anil K. Vijendran
 * @author Harish Prabandham
 * @author Remy Maucherat
 * @author Kin-man Chung
 * @author Glenn Nielsen
 */
public class JspServlet extends HttpServlet {

    // Logger
    private static Log log = LogFactory.getLog(JspServlet.class);

    private ServletContext context;
    private ServletConfig config;
    private Options options;
    private JspRuntimeContext rctxt;

    // START S1AS
    // jsp error count
    private int countErrors;
    private Object errorCountLk = new Object();
    // END S1AS

    // START SJSWS 6232180
    private String httpMethodsString = null;
    private HashSet httpMethodsSet = null;
    // END SJSWS 6232180

    /*
     * Initializes this JspServlet.
     */
    public void init(ServletConfig config) throws ServletException {

        super.init(config);
        this.config = config;
        this.context = config.getServletContext();

        // Initialize the JSP Runtime Context
        options = new EmbeddedServletOptions(config, context);
        rctxt = new JspRuntimeContext(context,options);

        // START SJSWS 6232180
        // Determine which HTTP methods to service ("*" means all)
        httpMethodsString = config.getInitParameter("httpMethods");
        if (httpMethodsString != null
                && !httpMethodsString.equals("*")) {
            httpMethodsSet = new HashSet();
            StringTokenizer tokenizer = new StringTokenizer(
                    httpMethodsString, ", \t\n\r\f");
            while (tokenizer.hasMoreTokens()) {
                httpMethodsSet.add(tokenizer.nextToken());
            }
        }
        // END SJSWS 6232180

        if (log.isTraceEnabled()) {
            log.trace(Localizer.getMessage("jsp.message.scratch.dir.is",
                                           options.getScratchDir().toString()));
            log.trace(Localizer.getMessage("jsp.message.dont.modify.servlets"));
        }
    }


    /**
     * Returns the number of JSPs for which JspServletWrappers exist, i.e.,
     * the number of JSPs that have been loaded into the webapp with which
     * this JspServlet is associated.
     *
     * <p>This info may be used for monitoring purposes.
     *
     * @return The number of JSPs that have been loaded into the webapp with
     * which this JspServlet is associated
     */
    public int getJspCount() {
        return this.rctxt.getJspCount();
    }


    /**
     * Resets the JSP reload counter.
     *
     * @param count Value to which to reset the JSP reload counter
     */
    public void setJspReloadCount(int count) {
        this.rctxt.setJspReloadCount(count);
    }


    /**
     * Gets the number of JSPs that have been reloaded.
     *
     * <p>This info may be used for monitoring purposes.
     *
     * @return The number of JSPs (in the webapp with which this JspServlet is
     * associated) that have been reloaded
     */
    public int getJspReloadCount() {
        return this.rctxt.getJspReloadCount();
    }


    // START S1AS
    /**
     * Gets the number of errors triggered by JSP invocations.
     *
     * @return The number of errors triggered by JSP invocations
     */
    public int getJspErrorCount() {
        return this.countErrors;
    }
    // END S1AS


    /**
     * <p>Look for a <em>precompilation request</em> as described in
     * Section 8.4.2 of the JSP 1.2 Specification.  <strong>WARNING</strong> -
     * we cannot use <code>request.getParameter()</code> for this, because
     * that will trigger parsing all of the request parameters, and not give
     * a servlet the opportunity to call
     * <code>request.setCharacterEncoding()</code> first.</p>
     *
     * @param request The servlet requset we are processing
     *
     * @exception ServletException if an invalid parameter value for the
     *  <code>jsp_precompile</code> parameter name is specified
     */
    boolean preCompile(HttpServletRequest request) throws ServletException {

        String queryString = request.getQueryString();
        if (queryString == null) {
            return (false);
        }
        int start = queryString.indexOf(Constants.PRECOMPILE);
        if (start < 0) {
            return (false);
        }
        queryString =
            queryString.substring(start + Constants.PRECOMPILE.length());
        if (queryString.length() == 0) {
            return (true);             // ?jsp_precompile
        }
        if (queryString.startsWith("&")) {
            return (true);             // ?jsp_precompile&foo=bar...
        }
        if (!queryString.startsWith("=")) {
            return (false);            // part of some other name or value
        }
        int limit = queryString.length();
        int ampersand = queryString.indexOf("&");
        if (ampersand > 0) {
            limit = ampersand;
        }
        String value = queryString.substring(1, limit);
        if (value.equals("true")) {
            return (true);             // ?jsp_precompile=true
        } else if (value.equals("false")) {
	    // Spec says if jsp_precompile=false, the request should not
	    // be delivered to the JSP page; the easiest way to implement
	    // this is to set the flag to true, and precompile the page anyway.
	    // This still conforms to the spec, since it says the
	    // precompilation request can be ignored.
            return (true);             // ?jsp_precompile=false
        } else {
            throw new ServletException("Cannot have request parameter " +
                                       Constants.PRECOMPILE + " set to " +
                                       value);
        }

    }
    

    public void service (HttpServletRequest request, 
    			 HttpServletResponse response)
                throws ServletException, IOException {

        // START SJSWS 6232180
        if (httpMethodsSet != null) {
            String method = request.getMethod();
            if (method == null) {
                return;
            }
            boolean isSupportedMethod = httpMethodsSet.contains(method);
            if (!isSupportedMethod) {
                if (method.equals("OPTIONS")) {
                    response.addHeader("Allow", httpMethodsString);
                } else {
                    super.service(request, response);
                }
                return;
            }
        }
        // END SJSWS 6232180

        String jspUri = null;

        String jspFile = (String) request.getAttribute(Constants.JSP_FILE);
        if (jspFile != null) {
            // JSP is specified via <jsp-file> in <servlet> declaration
            jspUri = jspFile;
        } else {
            /*
             * Check to see if the requested JSP has been the target of a
             * RequestDispatcher.include()
             */
            jspUri = (String) request.getAttribute(Constants.INC_SERVLET_PATH);
            if (jspUri != null) {
                /*
		 * Requested JSP has been target of
                 * RequestDispatcher.include(). Its path is assembled from the
                 * relevant javax.servlet.include.* request attributes
                 */
                String pathInfo = (String) request.getAttribute(
                                    "javax.servlet.include.path_info");
                if (pathInfo != null) {
                    jspUri += pathInfo;
                }
            } else {
                /*
                 * Requested JSP has not been the target of a 
                 * RequestDispatcher.include(). Reconstruct its path from the
                 * request's getServletPath() and getPathInfo()
                 */
                jspUri = request.getServletPath();
                String pathInfo = request.getPathInfo();
                if (pathInfo != null) {
                    jspUri += pathInfo;
                }
            }
        }

        if (log.isDebugEnabled()) {	    
            StringBuffer msg = new StringBuffer();
            msg.append("JspEngine --> [" + jspUri);
            msg.append("] ServletPath: [" + request.getServletPath());
            msg.append("] PathInfo: [" + request.getPathInfo());
            msg.append("] RealPath: [" + context.getRealPath(jspUri));
            msg.append("] RequestURI: [" + request.getRequestURI());
            msg.append("] QueryString: [" + request.getQueryString());
            msg.append("] RequestParams: [");
            Enumeration e = request.getParameterNames();
            while (e.hasMoreElements()) {
                String name = (String) e.nextElement();
                msg.append(" " + name + "=" 
                          + request.getParameter(name));
            }
            msg.append(" ]");
            log.debug(msg);
        }

        try {
            boolean precompile = preCompile(request);
            serviceJspFile(request, response, jspUri, null, precompile);
        } catch (RuntimeException e) {
            // STARTS S1AS
            incrementErrorCount();
            // END S1AS
            throw e;
        } catch (ServletException e) {
            // STARTS S1AS
            incrementErrorCount();
            // END S1AS
            throw e;
        } catch (IOException e) {
            // STARTS S1AS
            incrementErrorCount();
            // END S1AS
            throw e;
        } catch (Throwable e) {
            // STARTS S1AS
            incrementErrorCount();
            // END S1AS
            throw new ServletException(e);
        }

    }

    public void destroy() {
        if (log.isDebugEnabled()) {
            log.debug("JspServlet.destroy()");
        }

        rctxt.destroy();
        JspApplicationContextImpl.removeJspApplicationContext(context);

    }


    // -------------------------------------------------------- Private Methods

    private void serviceJspFile(HttpServletRequest request,
                                HttpServletResponse response, String jspUri,
                                Throwable exception, boolean precompile)
        throws ServletException, IOException {

        JspServletWrapper wrapper =
            (JspServletWrapper) rctxt.getWrapper(jspUri);
        if (wrapper == null) {
            synchronized(this) {
                wrapper = (JspServletWrapper) rctxt.getWrapper(jspUri);
                if (wrapper == null) {
                    // Check if the requested JSP page exists, to avoid
                    // creating unnecessary directories and files.
                    /* START PWC 6181923
                    if (null == context.getResource(jspUri)) {
                    */
                    // START PWC 6181923
                    if (null == context.getResource(jspUri)
                            && !options.getUsePrecompiled()) {
                    // END PWC 6181923

                        // START PWC 6300204
                        String includeRequestUri = (String) 
                            request.getAttribute("javax.servlet.include.request_uri");
                        if (includeRequestUri != null) {
                            // Missing JSP resource has been the target of a
                            // RequestDispatcher.include().
                            // Throw an exception (rather than returning a 
                            // 404 response error code), because any call to
                            // response.sendError() must be ignored by the
                            // servlet engine when issued from within an
                            // included resource (as per the Servlet spec).
                            throw new FileNotFoundException(jspUri);
                        }
                        // END PWC 6300204

                        /* RIMOD PWC 6282167, 4878272
                        response.sendError(HttpServletResponse.SC_NOT_FOUND,
                                           jspUri);
                        */
                        // START PWC 6282167, 4878272
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                        log.error(Localizer.getMessage(
                            "jsp.error.file.not.found",
                            context.getRealPath(jspUri)));
                        // END PWC 6282167, 4878272
                        return;
                    }
                    boolean isErrorPage = exception != null;
                    wrapper = new JspServletWrapper(config, options, jspUri,
                                                    isErrorPage, rctxt);
                    rctxt.addWrapper(jspUri,wrapper);
                }
            }
        }

        wrapper.service(request, response, precompile);

    }


    // STARTS S1AS
    private void incrementErrorCount() {
        synchronized (errorCountLk) {
            countErrors++;
        }
    }
    // END S1AS
}
