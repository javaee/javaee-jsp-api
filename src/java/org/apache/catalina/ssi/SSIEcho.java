

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

/**
 * Return the result associated with the supplied Server Variable.
 *
 * @author Bip Thelin
 * @author Dan Sandberg
 * @version $Revision: 1.1.1.1 $, $Date: 2005/05/27 22:55:08 $
 */
public class SSIEcho implements SSICommand {
    protected final static String DEFAULT_ENCODING = "entity";
    protected final static String MISSING_VARIABLE_VALUE = "(none)";

    /**
     * @see SSICommand
     */
    public void process(SSIMediator ssiMediator,
			String[] paramNames,
			String[] paramValues,
			PrintWriter writer) {

	String encoding = DEFAULT_ENCODING;
	String errorMessage = ssiMediator.getConfigErrMsg();

	for ( int i=0; i < paramNames.length; i++ ) {
	    String paramName = paramNames[i];
	    String paramValue = paramValues[i];

	    if ( paramName.equalsIgnoreCase("var") ) {
		String variableValue = ssiMediator.getVariableValue( paramValue, encoding );
		if ( variableValue == null ) {
		    variableValue = MISSING_VARIABLE_VALUE;
		}
		writer.write( variableValue );
	    } else if ( paramName.equalsIgnoreCase("encoding") ) {
		if ( isValidEncoding( paramValue ) ) {
		    encoding = paramValue;
		} else {
		    ssiMediator.log("#echo--Invalid encoding: " + paramValue );
		    writer.write( errorMessage );
		}
	    } else {
		ssiMediator.log("#echo--Invalid attribute: " + paramName );
		writer.write( errorMessage );
	    }
	}
    }

    protected boolean isValidEncoding( String encoding ) {
	return
	    encoding.equalsIgnoreCase("url") ||
	    encoding.equalsIgnoreCase("entity") ||
	    encoding.equalsIgnoreCase("none");
    }
}
