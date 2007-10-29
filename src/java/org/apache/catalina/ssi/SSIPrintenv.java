

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

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

/**
 * Implements the Server-side #printenv command
 *
 * @author Dan Sandberg
 * @version $Revision: 1.1.1.1 $, $Date: 2005/05/27 22:55:08 $
 */
public class SSIPrintenv implements SSICommand {
    /**
     * @see SSICommand
     */
    public void process(SSIMediator ssiMediator,
			String[] paramNames,
			String[] paramValues,
			PrintWriter writer) {

	//any arguments should produce an error
	if ( paramNames.length > 0 ) {
	    String errorMessage = ssiMediator.getConfigErrMsg();
	    writer.write( errorMessage );
	} else {
	    Collection variableNames = ssiMediator.getVariableNames();
	    Iterator iter = variableNames.iterator();
	    while ( iter.hasNext() ) {
		String variableName = (String) iter.next();
		String variableValue = ssiMediator.getVariableValue( variableName );
		//This shouldn't happen, since all the variable names must have values
		if ( variableValue == null ) {
		    variableValue = "(none)";
		}
		writer.write( variableName );
		writer.write( '=' );
		writer.write( variableValue );
		writer.write( '\n' );
	    }
	}
    }
}
