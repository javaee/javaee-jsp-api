

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


package org.apache.naming.factory;

import java.util.Hashtable;
import java.sql.Driver;
import java.sql.DriverManager;
import javax.naming.Name;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.RefAddr;
import javax.naming.spi.ObjectFactory;
import org.apache.naming.ResourceLinkRef;


/**
 * <p>Object factory for resource links.</p>
 * 
 * @author Remy Maucherat
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:15 $
 */

public class ResourceLinkFactory
    implements ObjectFactory {


    // ----------------------------------------------------------- Constructors


    // ------------------------------------------------------- Static Variables


    /**
     * Global naming context.
     */
    private static Context globalContext = null;


    // --------------------------------------------------------- Public Methods


    /**
     * Set the global context (note: can only be used once).
     * 
     * @param newGlobalContext new global context value
     */
    public static void setGlobalContext(Context newGlobalContext) {
        if (globalContext != null)
            return;
        globalContext = newGlobalContext;
    }


    // -------------------------------------------------- ObjectFactory Methods


    /**
     * Create a new DataSource instance.
     * 
     * @param obj The reference object describing the DataSource
     */
    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                    Hashtable environment)
        throws NamingException {
        
        if (!(obj instanceof ResourceLinkRef))
            return null;

        // Can we process this request?
        Reference ref = (Reference) obj;

        String type = ref.getClassName();

        // Read the global ref addr
        String globalName = null;
        RefAddr refAddr = ref.get(ResourceLinkRef.GLOBALNAME);
        if (refAddr != null) {
            globalName = refAddr.getContent().toString();
            Object result = null;
            result = globalContext.lookup(globalName);
            // FIXME: Check type
            return result;
        }

        return (null);

        
    }


}
