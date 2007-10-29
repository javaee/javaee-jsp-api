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


package org.apache.naming;

import javax.naming.NameParser;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.CompositeName;

/**
 * Parses names.
 *
 * @author Remy Maucherat
 * @version $Revision: 1.2 $ $Date: 2005/04/29 01:29:46 $
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

