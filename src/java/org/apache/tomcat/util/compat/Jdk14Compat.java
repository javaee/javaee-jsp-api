

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

package org.apache.tomcat.util.compat;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.MalformedURLException;

import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;


/**
 *  See JdkCompat. This is an extension of that class for Jdk1.4 support.
 *
 * @author Tim Funk
 * @author Remy Maucherat
 */
public class Jdk14Compat extends JdkCompat {
    // -------------------------------------------------------------- Constants

    // ------------------------------------------------------- Static Variables
    static Log logger = LogFactory.getLog(Jdk14Compat.class);

    // ----------------------------------------------------------- Constructors
    /**
     *  Default no-arg constructor
     */
    protected Jdk14Compat() {
    }


    // --------------------------------------------------------- Public Methods

    /**
     *  Return the URI for the given file. Originally created for
     *  o.a.c.loader.WebappClassLoader
     *
     *  @param File to wrap into URI
     *  @return A URI as a URL
     */
    public URL getURI(File file)
        throws MalformedURLException {

        File realFile = file;
        try {
            realFile = realFile.getCanonicalFile();
        } catch (IOException e) {
            // Ignore
        }

        return realFile.toURI().toURL();
    }


    /**
     *  Return the maximum amount of memory the JVM will attempt to use.
     */
    public long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }


    /**
     * Print out a partial servlet stack trace (truncating at the last 
     * occurrence of javax.servlet.).
     */
    public String getPartialServletStackTrace(Throwable t) {
        StringBuffer trace = new StringBuffer();
        trace.append(t.toString()).append('\n');
        StackTraceElement[] elements = t.getStackTrace();
        int pos = elements.length;
        for (int i = 0; i < elements.length; i++) {
            if ((elements[i].getClassName().startsWith
                 ("org.apache.catalina.core.ApplicationFilterChain"))
                && (elements[i].getMethodName().equals("internalDoFilter"))) {
                pos = i;
            }
        }
        for (int i = 0; i < pos; i++) {
            if (!(elements[i].getClassName().startsWith
                  ("org.apache.catalina.core."))) {
                trace.append('\t').append(elements[i].toString()).append('\n');
            }
        }
        return trace.toString();
    }

    public  String [] split(String path, String pat) {
        return path.split(pat);
    }
 }
