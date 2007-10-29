

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
package org.apache.tomcat.util.handler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Enumeration;


/**
 * Store all context informations for the invocation of a handler chain.
 *
 * @author Costin Manolache
 */
public class TcHandlerCtx {


    private int type = 0;

    /**
     * Get the type of the handler context.
     */
    public final int getType() {
        return type;
    }

    /**
     * Set the type of the handler context.
     */
    public final void setType( int type ) {
        this.type = type;
    }


    // XXX Make it configurable ( at startup, since runtime change will require sync )
    private Object notes[] = new Object[32];

    /**
     * Get a note associated with this hanlder context.
     */
    public final Object getNote( int id ) {
        return notes[id];
    }

    /**
     * Associate a note with this hanlder context.
     */
    public final void setNote( int id, Object o ) {
        notes[id]=o;
    }


    /**
     * Recycle the hanlder context.
     */
    public void recycle() {
        type = 0;
        for( int i=0; i<notes.length; i++ ) {
            notes[i]=null;
        }
    }

}
