

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


package org.apache.coyote.tomcat5;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

import org.apache.coyote.Response;
import org.apache.catalina.util.StringManager;

/**
 * Coyote implementation of the servlet output stream.
 * 
 * @author Costin Manolache
 * @author Remy Maucherat
 */
public class CoyoteOutputStream 
    extends ServletOutputStream {


    /**
     * The string manager for this package.
     */
    private static final StringManager sm =
        StringManager.getManager(Constants.Package);


    // ----------------------------------------------------- Instance Variables


    protected OutputBuffer ob;


    // ----------------------------------------------------------- Constructors


    // BEGIN S1AS 6175642
    /*
    protected CoyoteOutputStream(OutputBuffer ob) {
    */
    public CoyoteOutputStream(OutputBuffer ob) {
    // END S1AS 6175642
        this.ob = ob;
    }
    
    
    // --------------------------------------------------------- Public Methods


    /**
    * Prevent cloning the facade.
    */
    protected Object clone()
        throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

  
    // -------------------------------------------------------- Package Methods


    /**
     * Clear facade.
     */
    void clear() {
        ob = null;
    }


    // --------------------------------------------------- OutputStream Methods


    public void write(int i)
        throws IOException {
        // Disallow operation if the object has gone out of scope
        if (ob == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }

        ob.writeByte(i);
    }


    public void write(byte[] b)
        throws IOException {
        // Disallow operation if the object has gone out of scope
        if (ob == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }

        write(b, 0, b.length);
    }


    public void write(byte[] b, int off, int len)
        throws IOException {
        // Disallow operation if the object has gone out of scope
        if (ob == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }

        ob.write(b, off, len);
    }


    /**
     * Will send the buffer to the client.
     */
    public void flush()
        throws IOException {
        // Disallow operation if the object has gone out of scope
        if (ob == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }

        ob.flush();
    }


    public void close()
        throws IOException {
        // Disallow operation if the object has gone out of scope
        if (ob == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }

        ob.close();
    }


    // -------------------------------------------- ServletOutputStream Methods


    public void print(String s)
        throws IOException {
        // Disallow operation if the object has gone out of scope
        if (ob == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }

        ob.write(s);
    }


}

