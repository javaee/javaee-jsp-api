

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
package org.apache.tomcat.util.threads;

import org.apache.tomcat.util.*;

/**
 * The reaper is a background thread with which ticks every minute
 * and calls registered objects to allow reaping of old session
 * data.
 * 
 * @author James Duncan Davidson [duncan@eng.sun.com]
 * @author Costin Manolache
 */
public class Reaper extends Thread {

    private static org.apache.commons.logging.Log log=
        org.apache.commons.logging.LogFactory.getLog(Reaper.class );

    private boolean daemon=false;

    public Reaper() {
	if( daemon )
            this.setDaemon(true);
	this.setName("TomcatReaper");
    }

    public Reaper(String name) {
        if( daemon )
            this.setDaemon(true);
	this.setName(name);
    }

    private long interval = 1000 * 60; //ms
    
    // XXX TODO Allow per/callback interval, find next, etc
    // Right now the "interval" is used for all callbacks
    // and it represent a sleep between runs.
    
    ThreadPoolRunnable cbacks[]=new ThreadPoolRunnable[30]; // XXX max
    Object tdata[][]=new Object[30][]; // XXX max
    int count=0;

    /** Adding and removing callbacks is synchronized
     */
    Object lock=new Object();
    static boolean running=true;

    // XXX Should be called 'interval' not defaultInterval

    public void setDefaultInterval( long t ) {
	interval=t;
    }

    public long getDefaultIntervale() {
        return interval;
    }

    public int addCallback( ThreadPoolRunnable c, int interval ) {
	synchronized( lock ) {
	    cbacks[count]=c;
	    count++;
	    return count-1;
	}
    }

    public void removeCallback( int idx ) {
	synchronized( lock ) {
	    count--;
	    cbacks[idx]=cbacks[count];
	    cbacks[count]=null;
	}
    }

    public void startReaper() {
	running=true;
	this.start();
    }

    public synchronized void stopReaper() {
	running=false;
        if (log.isDebugEnabled())
	    log.debug("Stop reaper ");
	this.interrupt(); // notify() doesn't stop sleep
    }
    
    public void run() {
	while (running) {
	    if( !running) break;
	    try {
		this.sleep(interval);
	    } catch (InterruptedException ie) {
		// sometimes will happen
	    }

	    if( !running) break;
	    for( int i=0; i< count; i++ ) {
		ThreadPoolRunnable callB=cbacks[i];
		// it may be null if a callback is removed.
		//  I think the code is correct
		if( callB!= null ) {
		    callB.runIt( tdata[i] );
		}
		if( !running) break;
	    }
	}
    }
}
