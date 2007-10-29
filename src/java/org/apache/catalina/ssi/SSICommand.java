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
 * The interface that all SSI commands ( SSIEcho, SSIInclude, ...) must
 * implement.
 * 
 * @author Bip Thelin
 * @author Dan Sandberg
 * @author David Becker
 * @version $Revision: 467222 $, $Date: 2006-10-23 23:17:11 -0400 (Mon, 23 Oct 2006) $
 */
public interface SSICommand {
    /**
     * Write the output of the command to the writer.
     * 
     * @param ssiMediator
     *            the ssi mediator
     * @param commandName
     *            the name of the actual command ( ie. echo )
     * @param paramNames
     *            The parameter names
     * @param paramValues
     *            The parameter values
     * @param writer
     *            the writer to output to
     * @return the most current modified date resulting from any SSI commands
     * @throws SSIStopProcessingException
     *             if SSI processing should be aborted
     */
	public long process(SSIMediator ssiMediator, String commandName,
            String[] paramNames, String[] paramValues, PrintWriter writer)
            throws SSIStopProcessingException;
}