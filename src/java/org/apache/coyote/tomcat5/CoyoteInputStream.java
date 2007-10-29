

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
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedActionException;
import javax.servlet.ServletInputStream;

import org.apache.catalina.security.SecurityUtil;
import org.apache.catalina.util.StringManager;

/**
 * This class handles reading bytes.
 * 
 * @author Remy Maucherat
 * @author Jean-Francois Arcand
 */
public class CoyoteInputStream
    extends ServletInputStream {


    /**
     * The string manager for this package.
     */
    private static final StringManager sm =
        StringManager.getManager(Constants.Package);


    // ----------------------------------------------------- Instance Variables


    protected InputBuffer ib;


    // ----------------------------------------------------------- Constructors


    public CoyoteInputStream(InputBuffer ib) {
        this.ib = ib;
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
        ib = null;
    }


    // --------------------------------------------- ServletInputStream Methods


    public int read()
        throws IOException {      

        // Disallow operation if the object has gone out of scope
        if (ib == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
    
        if (SecurityUtil.isPackageProtectionEnabled()){
            
            try{
                Integer result = 
                    (Integer)AccessController.doPrivileged(
                        new PrivilegedExceptionAction(){

                            public Object run() throws IOException{
                                Integer integer = new Integer(ib.readByte());
                                return integer;
                            }

                });
                return result.intValue();
            } catch(PrivilegedActionException pae){
                Exception e = pae.getException();
                if (e instanceof IOException){
                    throw (IOException)e;
                } else {
                    throw new RuntimeException(e.getMessage());
                }
            }
        } else {
            return ib.readByte();
        }   
    }

    public int available() throws IOException {
        // Disallow operation if the object has gone out of scope
        if (ib == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }

        if (SecurityUtil.isPackageProtectionEnabled()){
            try{
                Integer result = 
                    (Integer)AccessController.doPrivileged(
                        new PrivilegedExceptionAction(){

                            public Object run() throws IOException{
                                Integer integer = new Integer(ib.available());
                                return integer;
                            }

                });
                return result.intValue();
            } catch(PrivilegedActionException pae){
                Exception e = pae.getException();
                if (e instanceof IOException){
                    throw (IOException)e;
                } else {
                    throw new RuntimeException(e.getMessage());
                }
            }
        } else {
           return ib.available();
        }           
    }

    public int read(final byte[] b) throws IOException {
        // Disallow operation if the object has gone out of scope
        if (ib == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }

        if (SecurityUtil.isPackageProtectionEnabled()){
            try{
                Integer result = 
                    (Integer)AccessController.doPrivileged(
                        new PrivilegedExceptionAction(){

                            public Object run() throws IOException{
                                Integer integer = 
                                    new Integer(ib.read(b, 0, b.length));
                                return integer;
                            }

                });
                return result.intValue();
            } catch(PrivilegedActionException pae){
                Exception e = pae.getException();
                if (e instanceof IOException){
                    throw (IOException)e;
                } else {
                    throw new RuntimeException(e.getMessage());
                }
            }
        } else {
            return ib.read(b, 0, b.length);
        }          
    }


    public int read(final byte[] b, final int off, final int len)
        throws IOException {

        // Disallow operation if the object has gone out of scope
        if (ib == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }
        
        if (SecurityUtil.isPackageProtectionEnabled()){
            try{
                Integer result = 
                    (Integer)AccessController.doPrivileged(
                        new PrivilegedExceptionAction(){

                            public Object run() throws IOException{
                                Integer integer = 
                                    new Integer(ib.read(b, off, len));
                                return integer;
                            }

                });
                return result.intValue();
            } catch(PrivilegedActionException pae){
                Exception e = pae.getException();
                if (e instanceof IOException){
                    throw (IOException)e;
                } else {
                    throw new RuntimeException(e.getMessage());
                }
            }
        } else {
            return ib.read(b, off, len);
        }        
    }


    public int readLine(byte[] b, int off, int len) throws IOException {
        // Disallow operation if the object has gone out of scope
        if (ib == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }

        return super.readLine(b, off, len);
    }


    /** 
     * Close the stream
     * Since we re-cycle, we can't allow the call to super.close()
     * which would permantely disable us.
     */
    public void close() throws IOException {
        // Disallow operation if the object has gone out of scope
        if (ib == null) {
            throw new IllegalStateException(
                sm.getString("object.invalidScope"));
        }

        if (SecurityUtil.isPackageProtectionEnabled()){
            try{
                AccessController.doPrivileged(
                    new PrivilegedExceptionAction(){

                        public Object run() throws IOException{
                            ib.close();
                            return null;
                        }

                });
            } catch(PrivilegedActionException pae){
                Exception e = pae.getException();
                if (e instanceof IOException){
                    throw (IOException)e;
                } else {
                    throw new RuntimeException(e.getMessage());
                }
            }
        } else {
             ib.close();
        }            
    }

}
