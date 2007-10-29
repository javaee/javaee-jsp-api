

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

import javax.naming.NameParser;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.CompositeName;

/**
 * Parses names.
 *
 * @author Remy Maucherat
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:15 $
 */

public class NameParserImpl 
    implements NameParser {


    // ----------------------------------------------------- Instance Variables


    // ----------------------------------------------------- NameParser Methods


    /**
     * Parses a name into its components.
     * 
     * @param name The non-null string name to parse
     * @return A non-null parsed form of the name using the naming convention 
     * of this parser.
     */
    public Name parse(String name)
        throws NamingException {
        return new CompositeName(name);
    }


}

