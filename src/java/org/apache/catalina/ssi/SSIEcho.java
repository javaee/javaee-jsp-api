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

package org.apache.catalina.ssi;


import java.io.PrintWriter;

/**
 * Return the result associated with the supplied Server Variable.
 *
 * @author Bip Thelin
 * @author Dan Sandberg
 * @version $Revision: 1.2 $, $Date: 2005/04/29 01:28:02 $
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
