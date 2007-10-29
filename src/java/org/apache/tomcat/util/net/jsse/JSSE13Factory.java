

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

package org.apache.tomcat.util.net.jsse;

import java.net.Socket;
import javax.net.ssl.SSLSocket;
// START SJSAS 6439313
import javax.net.ssl.SSLEngine;
// END SJSAS 6439313
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.ServerSocketFactory;

/**
 * Implementation class for JSSEFactory for JSSE 1.0.x (that is an extension
 * to the 1.3 JVM).
 *
 * @author Bill Barker
 */

class JSSE13Factory implements JSSEFactory {

    JSSE13Factory() {
    }

    public ServerSocketFactory getSocketFactory() {
        return new JSSE13SocketFactory();
    }

    public SSLSupport getSSLSupport(Socket socket) {
        return new JSSESupport((SSLSocket)socket);
    }

    // START SJSAS 6439313
    public SSLSupport getSSLSupport(SSLEngine sslEngine) {
        throw new IllegalStateException("Not Supported");
    }
    // END SJSAS 6439313
}
