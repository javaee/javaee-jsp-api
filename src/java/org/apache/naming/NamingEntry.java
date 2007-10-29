

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


/**
 * Represents a binding in a NamingContext.
 *
 * @author Remy Maucherat
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:15 $
 */

public class NamingEntry {


    // -------------------------------------------------------------- Constants


    public static final int ENTRY = 0;
    public static final int LINK_REF = 1;
    public static final int REFERENCE = 2;
    
    public static final int CONTEXT = 10;


    // ----------------------------------------------------------- Constructors


    public NamingEntry(String name, Object value, int type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The type instance variable is used to avoid unsing RTTI when doing
     * lookups.
     */
    public int type;
    public String name;
    public Object value;


    // --------------------------------------------------------- Object Methods


    public boolean equals(Object obj) {
        if ((obj != null) && (obj instanceof NamingEntry)) {
            return name.equals(((NamingEntry) obj).name);
        } else {
            return false;
        }
    }


    public int hashCode() {
        return name.hashCode();
    }


}
