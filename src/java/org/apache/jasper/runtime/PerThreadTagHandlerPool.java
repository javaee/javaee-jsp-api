

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

package org.apache.jasper.runtime;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import org.apache.jasper.Constants;

/**
 * Thread-local based pool of tag handlers that can be reused.
 *
 * @author Jan Luehe
 * @author Costin Manolache
 */
public class PerThreadTagHandlerPool extends TagHandlerPool {

    private int maxSize;

    // For cleanup
    private Vector perThreadDataVector;

    private ThreadLocal perThread;

    private static class PerThreadData {
        Tag handlers[];
        int current;
    }

    /**
     * Constructs a tag handler pool with the default capacity.
     */
    public PerThreadTagHandlerPool() {
        super();
        perThreadDataVector = new Vector();
    }

    protected void init(ServletConfig config) {
        maxSize = Constants.MAX_POOL_SIZE;
        String maxSizeS = getOption(config, OPTION_MAXSIZE, null);
        if (maxSizeS != null) {
            maxSize = Integer.parseInt(maxSizeS);
            if (maxSize < 0) {
                maxSize = Constants.MAX_POOL_SIZE;
            }
        }

        perThread = new ThreadLocal() {
            protected Object initialValue() {
                PerThreadData ptd = new PerThreadData();
                ptd.handlers = new Tag[maxSize];
                ptd.current = -1;
                perThreadDataVector.addElement(ptd);
                return ptd;
            }
        };
    }

    /**
     * Gets the next available tag handler from this tag handler pool,
     * instantiating one if this tag handler pool is empty.
     *
     * @param handlerClass Tag handler class
     *
     * @return Reused or newly instantiated tag handler
     *
     * @throws JspException if a tag handler cannot be instantiated
     */
    public Tag get(Class handlerClass) throws JspException {
        PerThreadData ptd = (PerThreadData)perThread.get();
        if(ptd.current >=0 ) {
            return ptd.handlers[ptd.current--];
        } else {
	    try {
		return (Tag) handlerClass.newInstance();
	    } catch (Exception e) {
		throw new JspException(e.getMessage(), e);
	    }
	}
    }

    /**
     * Adds the given tag handler to this tag handler pool, unless this tag
     * handler pool has already reached its capacity, in which case the tag
     * handler's release() method is called.
     *
     * @param handler Tag handler to add to this tag handler pool
     */
    public void reuse(Tag handler) {
        PerThreadData ptd=(PerThreadData)perThread.get();
	if (ptd.current < (ptd.handlers.length - 1)) {
	    ptd.handlers[++ptd.current] = handler;
        } else {
            handler.release();
        }
    }

    /**
     * Calls the release() method of all tag handlers in this tag handler pool.
     */
    public void release() {        
        Enumeration enumeration = perThreadDataVector.elements();
        while (enumeration.hasMoreElements()) {
	    PerThreadData ptd = (PerThreadData)enumeration.nextElement();
            if (ptd.handlers != null) {
                for (int i=ptd.current; i>=0; i--) {
                    if (ptd.handlers[i] != null) {
                        ptd.handlers[i].release();
		    }
                }
            }
        }
    }
}

