

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

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.RequestDispatcher;

/**
 * Implements the Server-side #include command
 *
 * @author Bip Thelin
 * @author Dan Sandberg
 * @version $Revision: 1.1.1.1 $, $Date: 2005/05/27 22:55:08 $
 */
public final class SSIInclude implements SSICommand {
    /**
     * @see SSICommand
     */
    public void process(SSIMediator ssiMediator,
			String[] paramNames,
			String[] paramValues,
			PrintWriter writer) {

	String configErrMsg = ssiMediator.getConfigErrMsg();

	for ( int i=0; i < paramNames.length; i++ ) {
	    String paramName = paramNames[i];
	    String paramValue = paramValues[i];

	    try {
		if ( paramName.equalsIgnoreCase("file") ||
		     paramName.equalsIgnoreCase("virtual") ) {
		    boolean virtual = paramName.equalsIgnoreCase("virtual");
		    String text = ssiMediator.getFileText( paramValue, virtual );
		    writer.write( text );
		} else {
		    ssiMediator.log("#include--Invalid attribute: " + paramName );
		    writer.write( configErrMsg );
		}
	    } catch ( IOException e ) {
		ssiMediator.log("#include--Couldn't include file: " + paramValue, e );
		writer.write( configErrMsg );
	    }
	}
    }
}

