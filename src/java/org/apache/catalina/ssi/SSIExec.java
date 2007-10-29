

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

package org.apache.catalina.ssi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.RequestDispatcher;
import org.apache.catalina.util.IOTools;

/**
 * Implements the Server-side #exec command
 * 
 * @author Bip Thelin
 * @author Amy Roh
 * @author Dan Sandberg
 * @version $Revision: 1.1.1.1 $, $Date: 2005/05/27 22:55:08 $
 *
 */
public class SSIExec implements SSICommand {   
    protected SSIInclude ssiInclude = new SSIInclude();
    protected final static int BUFFER_SIZE = 1024;

    /**
     * @see SSICommand
     */
    public void process(SSIMediator ssiMediator,
			String[] paramNames,
			String[] paramValues,
			PrintWriter writer) {

	String configErrMsg = ssiMediator.getConfigErrMsg();
	String paramName = paramNames[0];
	String paramValue = paramValues[0];

        if ( paramName.equalsIgnoreCase("cgi") ) {
	    ssiInclude.process( ssiMediator, new String[] {"virtual"}, new String[] {paramValue}, writer );
        } else if ( paramName.equalsIgnoreCase("cmd") ) {
	    boolean foundProgram = false;
	    try {
		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec( paramValue );
		foundProgram = true;

		BufferedReader stdOutReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		BufferedReader stdErrReader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

		char[] buf = new char[BUFFER_SIZE];
		IOTools.flow( stdErrReader, writer, buf );
		IOTools.flow( stdOutReader, writer, buf );
		proc.waitFor();
	    } catch ( InterruptedException e ) {
		ssiMediator.log( "Couldn't exec file: " + paramValue, e );
		writer.write( configErrMsg );
	    } catch ( IOException e ) {
		if ( !foundProgram ) {
		    //apache doesn't output an error message if it can't find a program
		}
		ssiMediator.log( "Couldn't exec file: " + paramValue, e );
	    }
	} 
    }
}
