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
package org.apache.coyote;


/**
 * Abstract the protocol implementation, including threading, etc.
 * Processor is single threaded and specific to stream-based protocols,
 * will not fit Jk protocols like JNI.
 *
 * This is the main interface to be implemented by a coyote connector.
 * (In contrast, Adapter is the main interface to be implemented by a
 * coyote servlet container.)
 *
 * @see Adapter
 *
 * @author Remy Maucherat
 * @author Costin Manolache
 */
public interface ProtocolHandler {


    /**
     * Pass config info.
     */
    public void setAttribute(String name, Object value);


    public Object getAttribute(String name);


    /**
     * The adapter, used to call the connector.
     */
    public void setAdapter(Adapter adapter);


    public Adapter getAdapter();


    /**
     * Init the protocol.
     */
    public void init()
        throws Exception;


    /**
     * Start the protocol.
     */
    public void start()
        throws Exception;


    public void destroy()
        throws Exception;


}
