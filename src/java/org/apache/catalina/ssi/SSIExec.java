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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import org.apache.catalina.util.IOTools;
/**
 * Implements the Server-side #exec command
 * 
 * @author Bip Thelin
 * @author Amy Roh
 * @author Paul Speed
 * @author Dan Sandberg
 * @author David Becker
 * @version $Revision: 467222 $, $Date: 2006-10-23 23:17:11 -0400 (Mon, 23 Oct 2006) $
 */
public class SSIExec implements SSICommand {
    protected SSIInclude ssiInclude = new SSIInclude();
    protected final static int BUFFER_SIZE = 1024;


    /**
     * @see SSICommand
     */
    public long process(SSIMediator ssiMediator, String commandName,
            String[] paramNames, String[] paramValues, PrintWriter writer) {
        long lastModified = 0;
        String configErrMsg = ssiMediator.getConfigErrMsg();
        String paramName = paramNames[0];
        String paramValue = paramValues[0];
        String substitutedValue = ssiMediator.substituteVariables(paramValue);
        if (paramName.equalsIgnoreCase("cgi")) {
            lastModified = ssiInclude.process(ssiMediator, "include",
                    			new String[]{"virtual"}, new String[]{substitutedValue},
								writer);
        } else if (paramName.equalsIgnoreCase("cmd")) {
            boolean foundProgram = false;
            try {
                Runtime rt = Runtime.getRuntime();
                Process proc = rt.exec(substitutedValue);
                foundProgram = true;
                BufferedReader stdOutReader = new BufferedReader(
                        new InputStreamReader(proc.getInputStream()));
                BufferedReader stdErrReader = new BufferedReader(
                        new InputStreamReader(proc.getErrorStream()));
                char[] buf = new char[BUFFER_SIZE];
                IOTools.flow(stdErrReader, writer, buf);
                IOTools.flow(stdOutReader, writer, buf);
                proc.waitFor();
                lastModified = System.currentTimeMillis();                
            } catch (InterruptedException e) {
                ssiMediator.log("Couldn't exec file: " + substitutedValue, e);
                writer.write(configErrMsg);
            } catch (IOException e) {
                if (!foundProgram) {
                    //apache doesn't output an error message if it can't find
                    // a program
                }
                ssiMediator.log("Couldn't exec file: " + substitutedValue, e);
            }
        }
        return lastModified;
    }
}