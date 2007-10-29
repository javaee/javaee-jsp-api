

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

import org.apache.tomcat.util.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 */
public class TcpConnection  { // implements Endpoint {
    /**
     * Maxium number of times to clear the socket input buffer.
     */
    static  int MAX_SHUTDOWN_TRIES=20;

    public TcpConnection() {
    }

    // -------------------- Properties --------------------

    PoolTcpEndpoint endpoint;
    Socket socket;

    public static void setMaxShutdownTries(int mst) {
	MAX_SHUTDOWN_TRIES = mst;
    }
    public void setEndpoint(PoolTcpEndpoint endpoint) {
	this.endpoint = endpoint;
    }

    public PoolTcpEndpoint getEndpoint() {
	return endpoint;
    }

    public void setSocket(Socket socket) {
	this.socket=socket;
    }

    public Socket getSocket() {
	return socket;
    }

    public void recycle() {
        endpoint = null;
        socket = null;
    }

    // Another frequent repetition
    public static int readLine(InputStream in, byte[] b, int off, int len)
	throws IOException
    {
	if (len <= 0) {
	    return 0;
	}
	int count = 0, c;

	while ((c = in.read()) != -1) {
	    b[off++] = (byte)c;
	    count++;
	    if (c == '\n' || count == len) {
		break;
	    }
	}
	return count > 0 ? count : -1;
    }

    
    // Usefull stuff - avoid having it replicated everywhere
    public static void shutdownInput(Socket socket)
	throws IOException
    {
	try {
	    InputStream is = socket.getInputStream();
	    int available = is.available ();
	    int count=0;
	    
	    // XXX on JDK 1.3 just socket.shutdownInput () which
	    // was added just to deal with such issues.
	    
	    // skip any unread (bogus) bytes
	    while (available > 0 && count++ < MAX_SHUTDOWN_TRIES) {
		is.skip (available);
		available = is.available();
	    }
	}catch(NullPointerException npe) {
	    // do nothing - we are just cleaning up, this is
	    // a workaround for Netscape \n\r in POST - it is supposed
	    // to be ignored
	}
    }
}


