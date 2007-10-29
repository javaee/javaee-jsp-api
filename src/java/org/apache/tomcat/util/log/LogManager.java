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
