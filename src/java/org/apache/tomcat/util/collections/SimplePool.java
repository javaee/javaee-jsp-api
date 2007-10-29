

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

/**
 * Simple object pool. Based on ThreadPool and few other classes
 *
 * The pool will ignore overflow and return null if empty.
 *
 * @author Gal Shachor
 * @author Costin Manolache
 */
public final class SimplePool  {

    private static com.sun.org.apache.commons.logging.Log log=
        com.sun.org.apache.commons.logging.LogFactory.getLog(SimplePool.class );

    /*
     * Where the threads are held.
     */
    private Object pool[];

    private int max;
    private int last;
    private int current=-1;
    
    private Object lock;
    public static final int DEFAULT_SIZE=32;
    static final int debug=0;
    
    public SimplePool() {
	this(DEFAULT_SIZE,DEFAULT_SIZE);
    }

    public SimplePool(int size) {
	this(size, size);
    }

    public SimplePool(int size, int max) {
	this.max=max;
	pool=new Object[size];
	this.last=size-1;
	lock=new Object();
    }

    public  void set(Object o) {
	put(o);
    }

    /**
     * Add the object to the pool, silent nothing if the pool is full
     */
    public  void put(Object o) {
	synchronized( lock ) {
	    if( current < last ) {
		current++;
		pool[current] = o;
            } else if( current < max ) {
		// realocate
		int newSize=pool.length*2;
		if( newSize > max ) newSize=max+1;
		Object tmp[]=new Object[newSize];
		last=newSize-1;
		System.arraycopy( pool, 0, tmp, 0, pool.length);
		pool=tmp;
		current++;
		pool[current] = o;
	    }
	    if( debug > 0 ) log("put " + o + " " + current + " " + max );
	}
    }

    /**
     * Get an object from the pool, null if the pool is empty.
     */
    public  Object get() {
	Object item = null;
	synchronized( lock ) {
	    if( current >= 0 ) {
		item = pool[current];
		pool[current] = null;
		current -= 1;
	    }
	    if( debug > 0 ) 
		log("get " + item + " " + current + " " + max);
	}
	return item;
    }

    /**
     * Return the size of the pool
     */
    public int getMax() {
	return max;
    }

    /**
     * Number of object in the pool
     */
    public int getCount() {
	return current+1;
    }


    public void shutdown() {
    }
    
    private void log( String s ) {
        if (log.isDebugEnabled())
	    log.debug("SimplePool: " + s );
    }
}
