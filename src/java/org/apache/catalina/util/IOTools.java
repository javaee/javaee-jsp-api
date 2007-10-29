

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
package org.apache.catalina.util;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;


/**
 * Contains commonly needed I/O-related methods 
 *
 * @author Dan Sandberg
 */
public class IOTools {
    protected final static int DEFAULT_BUFFER_SIZE=4*1024; //4k

    //Ensure non-instantiability
    private IOTools() {
    }

     /**
     * Read input from reader and write it to writer until there is no more
     * input from reader.
     *
     * @param reader the reader to read from.
     * @param writer the writer to write to.
     * @param buf the char array to use as a bufferx
     */
    public static void flow( Reader reader, Writer writer, char[] buf ) 
        throws IOException {
        int numRead;
        while ( (numRead = reader.read(buf) ) >= 0) {
            writer.write(buf, 0, numRead);
        }
    }

    /**
     * @see flow( Reader, Writer, char[] )
     */
    public static void flow( Reader reader, Writer writer ) 
        throws IOException {
        char[] buf = new char[DEFAULT_BUFFER_SIZE];
        flow( reader, writer, buf );
    }

    /**
     * Read input from input stream and write it to output stream 
     * until there is no more input from input stream.
     *
     * @param input stream the input stream to read from.
     * @param output stream the output stream to write to.
     * @param buf the byte array to use as a buffer
     */
    public static void flow( InputStream is, OutputStream os, byte[] buf ) 
        throws IOException {
        int numRead;
        while ( (numRead = is.read(buf) ) >= 0) {
            os.write(buf, 0, numRead);
        }
    }  

    /**
     * @see flow( Reader, Writer, byte[] )
     */ 
    public static void flow( InputStream is, OutputStream os ) 
        throws IOException {
        byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
        flow( is, os, buf );
    }
}
