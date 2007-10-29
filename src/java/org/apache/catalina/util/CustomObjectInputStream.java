

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

package org.apache.catalina.util;

import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * Custom subclass of <code>ObjectInputStream</code> that loads from the
 * class loader for this web application.  This allows classes defined only
 * with the web application to be found correctly.
 *
 * @author Craig R. McClanahan
 * @author Bip Thelin
 * @version $Revision: 1.1.1.1 $, $Date: 2005/05/27 22:55:09 $
 */

public final class CustomObjectInputStream
    extends ObjectInputStream {


    /**
     * The class loader we will use to resolve classes.
     */
    private ClassLoader classLoader = null;


    /**
     * Construct a new instance of CustomObjectInputStream
     *
     * @param stream The input stream we will read from
     * @param classLoader The class loader used to instantiate objects
     *
     * @exception IOException if an input/output error occurs
     */
    public CustomObjectInputStream(InputStream stream,
                                   ClassLoader classLoader)
        throws IOException {

        super(stream);
        this.classLoader = classLoader;
    }


    /**
     * Load the local class equivalent of the specified stream class
     * description, by using the class loader assigned to this Context.
     *
     * @param classDesc Class description from the input stream
     *
     * @exception ClassNotFoundException if this class cannot be found
     * @exception IOException if an input/output error occurs
     */
    public Class resolveClass(ObjectStreamClass classDesc)
        throws ClassNotFoundException, IOException {
        return Class.forName(classDesc.getName(), false, classLoader);
    }


}
