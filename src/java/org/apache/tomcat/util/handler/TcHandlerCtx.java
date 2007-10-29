/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
