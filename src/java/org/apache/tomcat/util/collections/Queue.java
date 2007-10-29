

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
package org.apache.tomcat.util.collections;

import java.util.Vector;

/**
 * A simple FIFO queue class which causes the calling thread to wait
 * if the queue is empty and notifies threads that are waiting when it
 * is not empty.
 *
 * @author Anil V (akv@eng.sun.com)
 */
public class Queue {
    private Vector vector = new Vector();
    private boolean stopWaiting=false;
    private boolean waiting=false;
    
    /** 
     * Put the object into the queue.
     * 
     * @param	object		the object to be appended to the
     * 				queue. 
     */
    public synchronized void put(Object object) {
	vector.addElement(object);
	notify();
    }

    /** Break the pull(), allowing the calling thread to exit
     */
    public synchronized void stop() {
	stopWaiting=true;
	// just a hack to stop waiting 
	if( waiting ) notify();
    }
    
    /**
     * Pull the first object out of the queue. Wait if the queue is
     * empty.
     */
    public synchronized Object pull() {
	while (isEmpty()) {
	    try {
		waiting=true;
		wait();
	    } catch (InterruptedException ex) {
	    }
	    waiting=false;
	    if( stopWaiting ) return null;
	}
	return get();
    }

    /**
     * Get the first object out of the queue. Return null if the queue
     * is empty. 
     */
    public synchronized Object get() {
	Object object = peek();
	if (object != null)
	    vector.removeElementAt(0);
	return object;
    }

    /**
     * Peek to see if something is available.
     */
    public Object peek() {
	if (isEmpty())
	    return null;
	return vector.elementAt(0);
    }
    
    /**
     * Is the queue empty?
     */
    public boolean isEmpty() {
	return vector.isEmpty();
    }

    /**
     * How many elements are there in this queue?
     */
    public int size() {
	return vector.size();
    }
}
