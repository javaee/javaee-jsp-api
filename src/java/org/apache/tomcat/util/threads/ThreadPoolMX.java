

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

package org.apache.tomcat.util.threads;

import java.util.*;
import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;

/**
 * Manageable thread pool. 
 * 
 * @author Costin Manolache
 * @deprecated This was an attempt to introduce a JMX dependency. A better solution
 * was the ThreadPoolListener - which is more powerfull and provides the same
 * features. The class is here for backward compatibility, all the methods are in
 * super().  
 */
public class ThreadPoolMX extends ThreadPool {
    static Log log = LogFactory.getLog(ThreadPoolMX.class);
    protected String domain; // not used 

    protected String name; // not used

    public ThreadPoolMX() {
        super();
    }

}
