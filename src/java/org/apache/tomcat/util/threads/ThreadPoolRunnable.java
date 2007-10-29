

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

import java.util.zip.*;
import java.net.*;
import java.util.*;
import java.io.*;

/** Implemented if you want to run a piece of code inside a thread pool.
 */
public interface ThreadPoolRunnable {
    // XXX use notes or a hashtable-like
    // Important: ThreadData in JDK1.2 is implemented as a Hashtable( Thread -> object ),
    // expensive.
    
    /** Called when this object is first loaded in the thread pool.
     *  Important: all workers in a pool must be of the same type,
     *  otherwise the mechanism becomes more complex.
     */
    public Object[] getInitData();

    /** This method will be executed in one of the pool's threads. The
     *  thread will be returned to the pool.
     */
    public void runIt(Object thData[]);

}
