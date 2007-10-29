

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

package org.apache.tomcat.util.net;

import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.tomcat.util.*;

/**
 * This interface will be implemented by any object that
 * uses TcpConnections. It is supported by the pool tcp
 * connection manager and should be supported by future
 * managers.
 * The goal is to decouple the connection handler from
 * the thread, socket and pooling complexity.
 */
public interface TcpConnectionHandler {
    
    /** Add informations about the a "controler" object
     *  specific to the server. In tomcat it will be a
     *  ContextManager.
     *  @deprecated This has nothing to do with TcpHandling,
     *  was used as a workaround
     */
    public void setServer(Object manager);

    
    /** Used to pass config informations to the handler
     *  @deprecated. This has nothing to do with Tcp,
     *  was used as a workaround 
     */
    public void setAttribute(String name, Object value );
    
    /** Called before the call to processConnection.
     *  If the thread is reused, init() should be called once per thread.
     *
     *  It may look strange, but it's a _very_ good way to avoid synchronized
     *  methods and keep per thread data.
     *
     *  Assert: the object returned from init() will be passed to
     *  all processConnection() methods happening in the same thread.
     * 
     */
    public Object[] init( );

    /**
     *  Assert: connection!=null
     *  Assert: connection.getSocket() != null
     *  Assert: thData != null and is the result of calling init()
     *  Assert: thData is preserved per Thread.
     */
    public void processConnection(TcpConnection connection, Object thData[]);    
}
