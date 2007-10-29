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
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 *
 * Portions Copyright Apache Software Foundation.
 */

/*
 * BaseSessionLocker.java
 *
 * Created on January 18, 2006, 4:46 PM
 */

package org.apache.catalina.session;

import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import org.apache.catalina.Context;
import org.apache.catalina.SessionLocker;

/**
 *
 * @author lwhite
 */
public class BaseSessionLocker implements SessionLocker {
    
    /** Creates a new instance of BaseSessionLocker */
    public BaseSessionLocker() {
    }
    
    public void init(Context context) {
        _context = context;
    }
    
    public boolean lockSession(ServletRequest req) throws ServletException {
        return true;
    }
    
    public void unlockSession(ServletRequest req) {
    }
    
    protected Context _context = null;
    
}
