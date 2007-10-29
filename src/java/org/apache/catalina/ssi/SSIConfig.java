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
 * Implements the Server-side #exec command
 * 
 * @author Bip Thelin
 * @author Paul Speed
 * @author Dan Sandberg
 * @author David Becker
 * @version $Revision: 467222 $, $Date: 2006-10-23 23:17:11 -0400 (Mon, 23 Oct 2006) $
 */
public final class SSIConfig implements SSICommand {
    /**
     * @see SSICommand
     */
    public long process(SSIMediator ssiMediator, String commandName,
            String[] paramNames, String[] paramValues, PrintWriter writer) {
        for (int i = 0; i < paramNames.length; i++) {
            String paramName = paramNames[i];
            String paramValue = paramValues[i];
            String substitutedValue = ssiMediator
                    .substituteVariables(paramValue);
            if (paramName.equalsIgnoreCase("errmsg")) {
                ssiMediator.setConfigErrMsg(substitutedValue);
            } else if (paramName.equalsIgnoreCase("sizefmt")) {
                ssiMediator.setConfigSizeFmt(substitutedValue);
            } else if (paramName.equalsIgnoreCase("timefmt")) {
                ssiMediator.setConfigTimeFmt(substitutedValue);
            } else {
                ssiMediator.log("#config--Invalid attribute: " + paramName);
                //We need to fetch this value each time, since it may change
                // during the
                // loop
                String configErrMsg = ssiMediator.getConfigErrMsg();
                writer.write(configErrMsg);
            }
        }
        // Setting config options doesn't really change the page
        return 0;
    }
}