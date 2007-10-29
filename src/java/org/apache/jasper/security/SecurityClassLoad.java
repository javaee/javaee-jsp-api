

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


package org.apache.jasper.security;

/**
 * Static class used to preload java classes when using the
 * Java SecurityManager so that the defineClassInPackage
 * RuntimePermission does not trigger an AccessControlException.
 *
 * @author Jean-Francois Arcand
 */

public final class SecurityClassLoad {

    private static com.sun.org.apache.commons.logging.Log log=
        com.sun.org.apache.commons.logging.LogFactory.getLog( SecurityClassLoad.class );

    public static void securityClassLoad(ClassLoader loader){

        if( System.getSecurityManager() == null ){
            return;
        }

        String basePackage = "org.apache.jasper.";
        try {
            loader.loadClass( basePackage +
                "runtime.JspFactoryImpl$PrivilegedGetPageContext");
            loader.loadClass( basePackage +
                "runtime.JspFactoryImpl$PrivilegedReleasePageContext");

            loader.loadClass( basePackage +
                "runtime.JspRuntimeLibrary");
            loader.loadClass( basePackage +
                "runtime.JspRuntimeLibrary$PrivilegedIntrospectHelper");
            
            loader.loadClass( basePackage +
                "runtime.ServletResponseWrapperInclude");
            loader.loadClass( basePackage +
                "runtime.TagHandlerPool");
            loader.loadClass( basePackage +
                "runtime.JspFragmentHelper");

            loader.loadClass( basePackage +
                "runtime.ProtectedFunctionMapper");
            loader.loadClass( basePackage +
                "runtime.ProtectedFunctionMapper$1");
            loader.loadClass( basePackage +
                "runtime.ProtectedFunctionMapper$2"); 
            loader.loadClass( basePackage +
                "runtime.ProtectedFunctionMapper$3");
            loader.loadClass( basePackage +
                "runtime.ProtectedFunctionMapper$4"); 

            loader.loadClass( basePackage +
                "runtime.PageContextImpl");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$1");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$2");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$3");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$4");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$5");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$6");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$7");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$8");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$9");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$10");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$11");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$12");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$13");      

            loader.loadClass( basePackage +
                "runtime.JspContextWrapper");   

            loader.loadClass( basePackage +
                "servlet.JspServletWrapper");

            loader.loadClass( basePackage +
                "runtime.JspWriterImpl$1");
        } catch (ClassNotFoundException ex) {
            log.error("SecurityClassLoad", ex);
        }
    }
}
