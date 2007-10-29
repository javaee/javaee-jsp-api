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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

/**
 * Implements the Server-side #exec command
 *
 * @author Bip Thelin
 * @author Dan Sandberg
 * @version $Revision: 1.2 $, $Date: 2005/04/29 01:28:02 $
 */
public final class SSIConfig implements SSICommand {
    /**
     * @see SSICommand
     */
    public void process(SSIMediator ssiMediator,
			String[] paramNames, 
			String[] paramValues,
			PrintWriter writer ) {

        for(int i=0;i<paramNames.length;i++) {
	    String paramName = paramNames[i];
	    String paramValue = paramValues[i];

            if ( paramName.equalsIgnoreCase("errmsg") ) {
		ssiMediator.setConfigErrMsg( paramValue );
            } else if ( paramName.equalsIgnoreCase("sizefmt") ) {
		ssiMediator.setConfigSizeFmt( paramValue );
            } else if ( paramName.equalsIgnoreCase("timefmt") ) {
		ssiMediator.setConfigTimeFmt( paramValue );
	    } else {
		ssiMediator.log("#config--Invalid attribute: " + paramName );
		//We need to fetch this value each time, since it may change during the loop
		String configErrMsg = ssiMediator.getConfigErrMsg();
		writer.write( configErrMsg );
	    }
        }
    }
}

