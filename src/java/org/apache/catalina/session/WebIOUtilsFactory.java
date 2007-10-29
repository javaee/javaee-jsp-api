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
 *//*
 * WebIOUtilsFactory.java
 *
 * Created on October 13, 2003, 9:51 AM
 */

package org.apache.catalina.session;

/**
 *
 * @author  Administrator
 */
public class WebIOUtilsFactory {
    
    private static final String IO_UTILITY_CLASS_NAME = "com.sun.ejb.base.io.IOUtilsCallerImpl";
    
    /** Creates a new instance of WebCustomObjectStreamFactory */
    public WebIOUtilsFactory() {
    }
    
    public IOUtilsCaller createWebIOUtil() {
        IOUtilsCaller webIOUtil = null;
        try {
            webIOUtil = 
                (IOUtilsCaller) (Class.forName(IO_UTILITY_CLASS_NAME)).newInstance();
        } catch (Exception ex) {
            //FIXME: log error
        }        
        return webIOUtil;
    }
     
    
}
