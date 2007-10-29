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
 */

package org.apache.tomcat.util.threads;

import java.util.Hashtable;

/** Special thread that allows storing of attributes and notes.
 *  A guard is used to prevent untrusted code from accessing the
 *  attributes.
 *
 *  This avoids hash lookups and provide something very similar
 * with ThreadLocal ( but compatible with JDK1.1 and faster on
 * JDK < 1.4 ).
 *
 * The main use is to store 'state' for monitoring ( like "processing
 * request 'GET /' ").
 */
public class ThreadWithAttributes extends Thread {
    
    private Object control;
    public static final int MAX_NOTES=16;
    private Object notes[]=new Object[MAX_NOTES];
    private Hashtable attributes=new Hashtable();
    private String currentStage;
    private Object param;
    
    private Object thData[];

    public ThreadWithAttributes(Object control, Runnable r) {
        super(r);
        this.control=control;
    }
    
    public final Object[] getThreadData(Object control ) {
        return thData;
    }
    
    public final void setThreadData(Object control, Object thData[] ) {
        this.thData=thData;
    }

    /** Notes - for attributes that need fast access ( array )
     * The application is responsible for id management
     */
    public final void setNote( Object control, int id, Object value ) {
        if( this.control != control ) return;
        notes[id]=value;
    }

    /** Information about the curent performed operation
     */
    public final String getCurrentStage(Object control) {
        if( this.control != control ) return null;
        return currentStage;
    }

    /** Information about the current request ( or the main object
     * we are processing )
     */
    public final Object getParam(Object control) {
        if( this.control != control ) return null;
        return param;
    }

    public final void setCurrentStage(Object control, String currentStage) {
        if( this.control != control ) return;
        this.currentStage = currentStage;
    }

    public final void setParam( Object control, Object param ) {
        if( this.control != control ) return;
        this.param=param;
    }

    public final Object getNote(Object control, int id ) {
        if( this.control != control ) return null;
        return notes[id];
    }

    /** Generic attributes. You'll need a hashtable lookup -
     * you can use notes for array access.
     */
    public final Hashtable getAttributes(Object control) {
        return attributes;
    }
}
