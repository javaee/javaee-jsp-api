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

import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.tomcat.util.IntrospectionUtils;

/** Hooks support. Hooks implement a chain-of-command pattern, and
 * are commonly used in most web servers as a mechanism of extensibility.
 *
 * The HandlerManager class will provide support for registering and maintaining
 * a list of modules implementing each hook.
 *
 * Each hook will have a name and an id. Name-based operations are slower, it is
 * recomended you use them only in non-critical code. The name-id association is
 * handled by NoteManager.
 *
 *
 *  @todo Merging. It is possible to have different hooks on different contexts
 *        ( server, context, servlet ), we need to merge them.
 */
public class HandlerManager {
    // hook name -> hook[]
    private Hashtable hookMap;
    private TcHandler hooks[][];

    public HandlerManager() {
    }

    /** Add a new handler.
     */
    public void addHandler( String name, TcHandler hook ) {
        
    }

    public TcHandler[] getHandlers( int hookId ) {
        return hooks[hookId];
    }

    public TcHandler[] getHandlers( String name ) {
        return getHandlers( 0 );
    }

    public Hashtable getHookMap() {
        return hookMap;
    }
}
