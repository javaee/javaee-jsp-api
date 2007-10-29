

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

package org.apache.jasper.util;

/**
 * Simple object pool. Based on ThreadPool and few other classes
 *
 * The pool will ignore overflow and return null if empty.
 *
 * @author Gal Shachor
 * @author Costin
 */
public final class SimplePool  {

    private static final int DEFAULT_SIZE=16;

    /*
     * Where the threads are held.
     */
    private Object pool[];

    private int max;
    private int current=-1;

    private Object lock;
    
    public SimplePool() {
	this.max=DEFAULT_SIZE;
	this.pool=new Object[max];
	this.lock=new Object();
    }
    
    public SimplePool(int max) {
	this.max=max;
	this.pool=new Object[max];
	this.lock=new Object();
    }

    /**
     * Adds the given object to the pool, and does nothing if the pool is full
     */
    public void put(Object o) {
	synchronized( lock ) {
	    if( current < (max-1) ) {
		current += 1;
		pool[current] = o;
            }
	}
    }

    /**
     * Get an object from the pool, null if the pool is empty.
     */
    public Object get() {
	Object item = null;
	synchronized( lock ) {
	    if( current >= 0 ) {
		item = pool[current];
		current -= 1;
	    }
	}
	return item;
    }

    /**
     * Return the size of the pool
     */
    public int getMax() {
	return max;
    }
}
