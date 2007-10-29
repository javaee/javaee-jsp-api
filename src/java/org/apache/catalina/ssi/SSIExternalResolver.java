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
import java.util.Collection;
import java.util.Date;
/**
 * Interface used by SSIMediator to talk to the 'outside world' ( usually a
 * servlet )
 * 
 * @author Dan Sandberg
 * @version $Revision: 467222 $, $Date: 2006-10-23 23:17:11 -0400 (Mon, 23 Oct 2006) $
 */
public interface SSIExternalResolver {
    /**
     * Adds any external variables to the variableNames collection.
     * 
     * @param variableNames
     *            the collection to add to
     */
    public void addVariableNames(Collection variableNames);


    public String getVariableValue(String name);


    /**
     * Set the named variable to the specified value. If value is null, then
     * the variable will be removed ( ie. a call to getVariableValue will
     * return null )
     * 
     * @param name
     *            of the variable
     * @param value
     *            of the variable
     */
    public void setVariableValue(String name, String value);


    /**
     * Returns the current date. This is useful for putting the SSI stuff in a
     * regression test. Since you can make the current date a constant, it
     * makes testing easier since the output won't change.
     * 
     * @return the data
     */
    public Date getCurrentDate();


    public long getFileSize(String path, boolean virtual) throws IOException;


    public long getFileLastModified(String path, boolean virtual)
            throws IOException;


    public String getFileText(String path, boolean virtual) throws IOException;


    public void log(String message, Throwable throwable);
}