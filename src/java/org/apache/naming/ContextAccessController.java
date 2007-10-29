

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


package org.apache.naming;

import java.util.Hashtable;
import javax.naming.NamingException;

/**
 * Handles the access control on the JNDI contexts.
 *
 * @author Remy Maucherat
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:15 $
 */

public class ContextAccessController {


    // -------------------------------------------------------------- Variables


    /**
     * Catalina context names on which writing is not allowed.
     */
    private static Hashtable readOnlyContexts = new Hashtable();


    /**
     * Security tokens repository.
     */
    private static Hashtable securityTokens = new Hashtable();


    // --------------------------------------------------------- Public Methods


    /**
     * Set a security token for a context. Can be set only once.
     * 
     * @param name Name of the context
     * @param context Security token
     */
    public static void setSecurityToken(Object name, Object token) {
        if ((!securityTokens.containsKey(name)) && (token != null)) {
            securityTokens.put(name, token);
        }
    }


    /**
     * Remove a security token for a context.
     * 
     * @param name Name of the context
     * @param context Security token
     */
    public static void unsetSecurityToken(Object name, Object token) {
        if (checkSecurityToken(name, token)) {
            securityTokens.remove(name);
        }
    }


    /**
     * Check a submitted security token. The submitted token must be equal to
     * the token present in the repository. If no token is present for the 
     * context, then returns true.
     * 
     * @param name Name of the context
     * @param context Submitted security token
     */
    public static boolean checkSecurityToken
        (Object name, Object token) {
        Object refToken = securityTokens.get(name);
        if (refToken == null)
            return (true);
        if ((refToken != null) && (refToken.equals(token)))
            return (true);
        return (false);
    }


    /**
     * Allow writing to a context.
     * 
     * @param name Name of the context
     * @param token Security token
     */
    public static void setWritable(Object name, Object token) {
        if (checkSecurityToken(name, token))
            readOnlyContexts.remove(name);
    }


    /**
     * Set whether or not a context is writable.
     * 
     * @param name Name of the context
     */
    public static void setReadOnly(Object name) {
        readOnlyContexts.put(name, name);
    }


    /**
     * Returns if a context is writable.
     * 
     * @param name Name of the context
     */
    public static boolean isWritable(Object name) {
        return !(readOnlyContexts.containsKey(name));
    }


}

