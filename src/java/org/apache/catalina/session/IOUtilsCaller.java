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
 *//*
 * IOUtilsCaller.java
 *
 * Created on October 13, 2003, 11:49 AM
 */

package org.apache.catalina.session;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
//FIXME: move this to commons so it can be added back to api
//import com.sun.ejb.spi.io.NonSerializableObjectHandler;

/**
 *
 * @author  Administrator
 */
public interface IOUtilsCaller {
    
    public ObjectInputStream createObjectInputStream(
        InputStream is,
        boolean resolveObject,
        ClassLoader loader) throws Exception;
    
    public ObjectOutputStream createObjectOutputStream(
        OutputStream os,
        boolean replaceObject) throws IOException;  
    
}

