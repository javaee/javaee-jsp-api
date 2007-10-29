

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

package org.apache.tomcat.util.collections;

import org.apache.tomcat.util.buf.MessageBytes;

import java.io.*;
import java.util.*;
import java.text.*;

/** Enumerate the values for a (possibly ) multiple
 *    value element.
 */
class MultiMapValuesEnumeration implements Enumeration {
    int pos;
    int size;
    MessageBytes next;
    MultiMap headers;
    String name;

    MultiMapValuesEnumeration(MultiMap headers, String name,
			      boolean toString) {
        this.name=name;
	this.headers=headers;
	pos=0;
	size = headers.size();
	findNext();
    }

    private void findNext() {
	next=null;
	for( ; pos< size; pos++ ) {
	    MessageBytes n1=headers.getName( pos );
	    if( n1.equalsIgnoreCase( name )) {
		next=headers.getValue( pos );
		break;
	    }
	}
	pos++;
    }
    
    public boolean hasMoreElements() {
	return next!=null;
    }

    public Object nextElement() {
	MessageBytes current=next;
	findNext();
	return current.toString();
    }
}
