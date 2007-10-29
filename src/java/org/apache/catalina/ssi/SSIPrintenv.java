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
import java.util.Collection;
import java.util.Iterator;

/**
 * Implements the Server-side #printenv command
 *
 * @author Dan Sandberg
 * @version $Revision: 1.2 $, $Date: 2005/04/29 01:28:04 $
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
