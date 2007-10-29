

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

/** Enumerate the distinct header names.
    Each nextElement() is O(n) ( a comparation is
    done with all previous elements ).

    This is less frequesnt than add() -
    we want to keep add O(1).
*/
public final class MultiMapNamesEnumeration implements Enumeration {
    int pos;
    int size;
    String next;
    MultiMap headers;

    // toString and unique options are not implemented -
    // we allways to toString and unique.
    
    /** Create a new multi-map enumeration.
     * @param  headers the collection to enumerate 
     * @param  toString convert each name to string 
     * @param  unique return only unique names
     */
    MultiMapNamesEnumeration(MultiMap headers, boolean toString,
			     boolean unique) {
	this.headers=headers;
	pos=0;
	size = headers.size();
	findNext();
    }

    private void findNext() {
	next=null;
	for(  ; pos< size; pos++ ) {
	    next=headers.getName( pos ).toString();
	    for( int j=0; j<pos ; j++ ) {
		if( headers.getName( j ).equalsIgnoreCase( next )) {
		    // duplicate.
		    next=null;
		    break;
		}
	    }
	    if( next!=null ) {
		// it's not a duplicate
		break;
	    }
	}
	// next time findNext is called it will try the
	// next element
	pos++;
    }
    
    public boolean hasMoreElements() {
	return next!=null;
    }

    public Object nextElement() {
	String current=next;
	findNext();
	return current;
    }
}
