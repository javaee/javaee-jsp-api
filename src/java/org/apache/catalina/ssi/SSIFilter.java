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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Globals;
/**
 * Filter to process SSI requests within a webpage. Mapped to a content types
 * from within web.xml.
 * 
 * @author David Becker
 * @version $Revision: 500684 $, $Date: 2007-01-27 18:27:18 -0500 (Sat, 27 Jan 2007) $
 * @see org.apache.catalina.ssi.SSIServlet
 */
public class SSIFilter implements Filter {
	protected FilterConfig config = null;
    /** Debug level for this servlet. */
    protected int debug = 0;
    /** Expiration time in seconds for the doc. */
    protected Long expires = null;
    /** virtual path can be webapp-relative */
    protected boolean isVirtualWebappRelative = false;
    /** regex pattern to match when evaluating content types */
	protected Pattern contentTypeRegEx = null;
	/** default pattern for ssi filter content type matching */
	protected Pattern shtmlRegEx =
        Pattern.compile("text/x-server-parsed-html(;.*)?");


    //----------------- Public methods.
    /**
     * Initialize this servlet.
     * 
     * @exception ServletException
     *                if an error occurs
     */
    public void init(FilterConfig config) throws ServletException {
    	this.config = config;
    	
        if (config.getInitParameter("debug") != null) {
            debug = Integer.parseInt(config.getInitParameter("debug"));
        }

        if (config.getInitParameter("contentType") != null) {
            contentTypeRegEx = Pattern.compile(config.getInitParameter("contentType"));
        } else {
            contentTypeRegEx = shtmlRegEx;
        }

        isVirtualWebappRelative = 
            Boolean.parseBoolean(config.getInitParameter("isVirtualWebappRelative"));

        if (config.getInitParameter("expires") != null)
            expires = Long.valueOf(config.getInitParameter("expires"));

        if (debug > 0)
            config.getServletContext().log(
                    "SSIFilter.init() SSI invoker started with 'debug'=" + debug);
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        // cast once
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;
        
        // indicate that we're in SSI processing
        req.setAttribute(Globals.SSI_FLAG_ATTR, "true");           

        // setup to capture output
        ByteArrayServletOutputStream basos = new ByteArrayServletOutputStream();
        ResponseIncludeWrapper responseIncludeWrapper =
            new ResponseIncludeWrapper(config.getServletContext(),req, res, basos);

        // process remainder of filter chain
        chain.doFilter(req, responseIncludeWrapper);

        // we can't assume the chain flushed its output
        responseIncludeWrapper.flushOutputStreamOrWriter();
        byte[] bytes = basos.toByteArray();

        // get content type
        String contentType = responseIncludeWrapper.getContentType();

        // is this an allowed type for SSI processing?
        if (contentTypeRegEx.matcher(contentType).matches()) {
            String encoding = res.getCharacterEncoding();

            // set up SSI processing 
            SSIExternalResolver ssiExternalResolver =
                new SSIServletExternalResolver(config.getServletContext(), req,
                        res, isVirtualWebappRelative, debug, encoding);
            SSIProcessor ssiProcessor = new SSIProcessor(ssiExternalResolver,
                    debug);
            
            // prepare readers/writers
            Reader reader =
                new InputStreamReader(new ByteArrayInputStream(bytes), encoding);
            ByteArrayOutputStream ssiout = new ByteArrayOutputStream();
            PrintWriter writer =
                new PrintWriter(new OutputStreamWriter(ssiout, encoding));
            
            // do SSI processing  
            long lastModified = ssiProcessor.process(reader,
                    responseIncludeWrapper.getLastModified(), writer);
            
            // set output bytes
            writer.flush();
            bytes = ssiout.toByteArray();
            
            // override headers
            if (expires != null) {
                res.setDateHeader("expires", (new java.util.Date()).getTime()
                        + expires.longValue() * 1000);
            }
            if (lastModified > 0) {
                res.setDateHeader("last-modified", lastModified);
            }
            res.setContentLength(bytes.length);
            
            Matcher shtmlMatcher =
                shtmlRegEx.matcher(responseIncludeWrapper.getContentType());
            if (shtmlMatcher.matches()) {
            	// Convert shtml mime type to ordinary html mime type but preserve
                // encoding, if any.
            	String enc = shtmlMatcher.group(1);
            	res.setContentType("text/html" + ((enc != null) ? enc : ""));
            }
        }

        // write output
        OutputStream out = null;
        try {
            out = res.getOutputStream();
        } catch (IllegalStateException e) {
            // Ignore, will try to use a writer
        }
        if (out == null) {
            res.getWriter().write(new String(bytes));
        } else {
            out.write(bytes);
        }
    }

    public void destroy() {
    }
}
