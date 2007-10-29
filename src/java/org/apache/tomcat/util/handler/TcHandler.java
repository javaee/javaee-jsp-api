

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

import java.io.*;
import java.util.*;
import java.security.*;


/**
 * The lowest level component of Jk ( and hopefully Coyote ). 
 *
 * Try to keep it minimal and flexible - add only if you _have_ to add.
 *
 * It is similar in concept and can implement/wrap tomcat3.3 Interceptor, tomcat4.0 Valve,
 * axis Handler, tomcat3.3 Handler, apache2 Hooks etc.
 *
 * Both iterative (Interceptor, Hook ) and recursive ( Valve ) behavior are supported.
 * Named TcHandler because Handler name is too overloaded.
 *
 * The interface allows both stateless and statefull implementations ( like Servlet ).
 *
 * @author Costin Manolache
 */
public abstract class TcHandler {
    public static final int OK=0;
    public static final int LAST=1;
    public static final int ERROR=2;

    protected Hashtable attributes=new Hashtable();
    protected TcHandler next;
    protected String name;
    protected int id;

    // -------------------- Configuration --------------------
    
    /** Set the name of the handler. Will allways be called by
     *  worker env after creating the worker.
     */
    public void setName(String s ) {
        name=s;
    }

    public String getName() {
        return name;
    }

    /** Set the id of the worker. It can be used for faster dispatch.
     *  Must be unique, managed by whoever creates the handlers.
     */
    public void setId( int id ) {
        this.id=id;
    }

    public int getId() {
        return id;
    }
    
    /** Catalina-style "recursive" invocation. A handler is required to call
     *  the next handler if set.
     */
    public void setNext( TcHandler h ) {
        next=h;
    }


    /** Base implementation will just save all attributes. 
     *  It is higly desirable to override this and allow runtime reconfiguration.
     *  XXX Should I make it abstract and force everyone to override ?
     */
    public void setAttribute( String name, Object value ) {
        attributes.put( name, value );
    }

    /** Get an attribute. Override to allow runtime query ( attribute can be
     *  anything, including statistics, etc )
     */
    public Object getAttribute( String name ) {
        return attributes.get(name) ;
    }

    //-------------------- Lifecycle --------------------
    
    /** Should register the request types it can handle,
     *   same style as apache2.
     */
    public void init() throws IOException {
    }

    /** Clean up and stop the handler. Override if needed.
     */
    public void destroy() throws IOException {
    }

    public void start() throws IOException {
    }

    public void stop() throws IOException {
    }

    // -------------------- Action --------------------
    
    /** The 'hook' method. If a 'next' was set, invoke should call it ( recursive behavior,
     *  similar with valve ).
     *
     * The application using the handler can also iterate, using the same semantics with
     * Interceptor or APR hooks.
     *
     * @returns OK, LAST, ERROR Status of the execution, semantic similar with apache
     */
    public abstract int invoke(TcHandlerCtx tcCtx)  throws IOException;



}
