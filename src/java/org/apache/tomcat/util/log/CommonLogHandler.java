

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

import org.apache.tomcat.util.log.*;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.StringWriter;

import java.util.*;

import org.apache.commons.logging.*;

/**
 *  Log using common-logging.
 *
 * @author Costin Manolache
 */
public  class CommonLogHandler extends LogHandler {

    private Hashtable loggers=new Hashtable();
    
    /**
     * Prints log message and stack trace.
     * This method should be overriden by real logger implementations
     *
     * @param	prefix		optional prefix. 
     * @param	message		the message to log. 
     * @param	t		the exception that was thrown.
     * @param	verbosityLevel	what type of message is this?
     * 				(WARNING/DEBUG/INFO etc)
     */
    public void log(String prefix, String msg, Throwable t,
		    int verbosityLevel)
    {
        if( prefix==null ) prefix="tomcat";

        org.apache.commons.logging.Log l=(org.apache.commons.logging.Log)loggers.get( prefix );
        if( l==null ) {
            l=LogFactory.getLog( prefix );
            loggers.put( prefix, l );
        }
        
	if( verbosityLevel > this.level ) return;

        if( t==null ) {
            if( verbosityLevel == Log.FATAL )
                l.fatal(msg);
            else if( verbosityLevel == Log.ERROR )
                l.error( msg );
            else if( verbosityLevel == Log.WARNING )
                l.warn( msg );
            else if( verbosityLevel == Log.INFORMATION)
                l.info( msg );
            else if( verbosityLevel == Log.DEBUG )
                l.debug( msg );
        } else {
            if( verbosityLevel == Log.FATAL )
                l.fatal(msg, t);
            else if( verbosityLevel == Log.ERROR )
                l.error( msg, t );
            else if( verbosityLevel == Log.WARNING )
                l.warn( msg, t );
            else if( verbosityLevel == Log.INFORMATION)
                l.info( msg, t );
            else if( verbosityLevel == Log.DEBUG )
                l.debug( msg, t );
        }
    }

    /**
     * Flush the log. 
     */
    public void flush() {
	// Nothing - commons logging doesn't have the notion
    }

    /**
     * Close the log. 
     */
    public synchronized void close() {
	// Nothing - commons logging doesn't have the notion
    }
    
}
