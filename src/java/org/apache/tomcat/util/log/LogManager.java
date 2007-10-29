

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
package org.apache.tomcat.util.log;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;


/**
 * Allows the control the log properties at runtime.
 * Normal applications will just use Log, without having to
 * deal with the way the log is configured or managed.
 *
 *
 * @author Alex Chaffee [alex@jguru.com]
 * @author Costin Manolache
 **/
public class LogManager {

    //static LogHandler defaultChannel=new LogHandler();
    static LogHandler defaultChannel=new CommonLogHandler();
    
    protected Hashtable loggers=new Hashtable();
    protected Hashtable channels=new Hashtable();

    public  Hashtable getLoggers() {
	return loggers;
    }

    public Hashtable getChannels() {
	return channels;
    }
    
    public static void setDefault( LogHandler l ) {
	if( defaultChannel==null)
	    defaultChannel=l;
    }

    public void addChannel( String name, LogHandler logH ) {
	if(name==null) name="";

	channels.put( name, logH );
	Enumeration enumeration=loggers.keys();
	while( enumeration.hasMoreElements() ) {
	    String k=(String)enumeration.nextElement();
	    Log l=(Log)loggers.get( k );
	    if( name.equals( l.getChannel( this ) )) {
		l.setProxy( this, logH );
	    }
	}
    }
    
    /** Default method to create a log facade.
     */
    public Log getLog( String channel, String prefix,
		       Object owner ) {
	if( prefix==null && owner!=null ) {
	    String cname = owner.getClass().getName();
	    prefix = cname.substring( cname.lastIndexOf(".") +1);
	}

	LogHandler proxy=(LogHandler)channels.get(channel);
	if( proxy==null ) proxy=defaultChannel;
	
	// user-level loggers
	Log log=new Log( channel, prefix, proxy, owner );
	loggers.put( channel + ":" + prefix, log );
	if( dL > 0 )
	    System.out.println("getLog facade " + channel + ":" + prefix);
	return log;
    }

    private static int dL=0;

}    
