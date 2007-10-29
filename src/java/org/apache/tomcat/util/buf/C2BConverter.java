

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


package org.apache.tomcat.util.buf;

import org.apache.tomcat.util.buf.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CoderResult;
import java.nio.charset.CharsetEncoder;

/** Efficient conversion of character to bytes.
 *  
 *  This uses the standard JDK mechansim - a writer - but provides mechanisms
 *  to recycle all the objects that are used. It is compatible with JDK1.1 and up,
 *  ( nio is better, but it's not available even in 1.2 or 1.3 )
 * 
 */

class C2BConverter_8859_1 extends C2BConverter {
    protected C2BConverter_8859_1(ByteChunk bb, String enc) throws IOException {
        super(bb, enc);
    }

    public final void convert(char sa[]) throws IOException {
        convert(sa, 0, sa.length);
    }

    public final void convert(String s) throws IOException {
        convert(s.toCharArray(), 0, s.length());
    }

    public final void convert(String s, int off, int len) throws IOException {
        convert(s.toCharArray(), off, len);
    }

    public final void convert(char sa[], int off, int len) throws IOException {
        int res = convertLoop(sa, off, len);
        while (res < len) {
            bb.flushBuffer();
            off += res;
            len -= res;
            res = convertLoop(sa, off, len);
        }
    }

    private int convertLoop(char sa[], int sp, int len) throws IOException {
        int sl = sp + len;
        if (sl > sa.length)
            sl = sa.length;
        byte[] da = bb.getBytes();
        int dp = bb.getEnd();
        int dl = da.length - dp;
        int nChars = 0;
        while (sp < sl) {
            char c = sa[sp];
            if (c <= '\u00FF') {
                if (dp >= dl) {
                    bb.setEnd(dp);
                    return nChars;
                }
                da[dp++] = (byte)c;
                sp++;
                nChars++;
            }
            else {
                bb.setEnd(dp);
                throw new IOException("Unexpected character in C2BConverter for ISO_8859_1");
            }
        }
        bb.setEnd(dp);
        return nChars;
    }
}

public class C2BConverter {

    private static com.sun.org.apache.commons.logging.Log log=
        com.sun.org.apache.commons.logging.LogFactory.getLog(C2BConverter.class );

    protected ByteChunk bb;
    protected String enc;
    protected CharsetEncoder encoder;
    
    /** Create a converter, with bytes going to a byte buffer
     */
    public C2BConverter(ByteChunk output, String encoding) throws IOException {
        this.bb=output;
        this.enc=encoding;
        encoder = Charset.forName(enc).newEncoder();
    }

    /** Create a converter
     */
    public C2BConverter(String encoding) throws IOException {
        this( new ByteChunk(1024), encoding );
    }

    public static C2BConverter getInstance(ByteChunk output, String encoding) throws IOException {
        if (encoding.equals("ISO-8859-1")) {
            return new C2BConverter_8859_1(output, encoding);
        }
        return new C2BConverter(output, encoding);
    }
    
    public ByteChunk getByteChunk() {
        return bb;
    }

    public String getEncoding() {
        return enc;
    }

    public void setByteChunk(ByteChunk bb) {
        this.bb=bb;
    }

    /** Reset the internal state, empty the buffers.
     *  The encoding remain in effect, the internal buffers remain allocated.
     */
    public  void recycle() {
        bb.recycle();
    }

    /** Generate the bytes using the specified encoding
     */
    public void convert(char c[], int off, int len) throws IOException {
        CharBuffer cb = CharBuffer.wrap(c, off, len);
        byte[] barr = bb.getBuffer();
        int boff = bb.getEnd();
        ByteBuffer tmp = ByteBuffer.wrap(barr, boff, barr.length - boff);
        CoderResult cr = encoder.encode(cb, tmp, true);
        bb.setEnd(tmp.position());
        while (cr == CoderResult.OVERFLOW) {
            bb.flushBuffer();
            tmp = ByteBuffer.wrap(barr, 0, barr.length);
            cr = encoder.encode(cb, tmp, true);
            bb.setEnd(tmp.position());
        }
        if (cr != CoderResult.UNDERFLOW)
            throw new IOException("Encoding error");
    }

    /** Generate the bytes using the specified encoding
     */
    public  void convert(String s ) throws IOException {
        convert(s, 0, s.length());
    }
    
    /** Generate the bytes using the specified encoding
     */    
    public  void convert(String s, int off, int len ) throws IOException {
        convert(s.toCharArray(), off, len);
    }

    /** Generate the bytes using the specified encoding
     */
    public  void convert(char c ) throws IOException {
        char[] tmp = new char[1];
        tmp[0] = c;
        convert(tmp, 0, 1);
    }

    /** Convert a message bytes chars to bytes
     */
    public void convert(MessageBytes mb ) throws IOException {
        int type=mb.getType();
        if( type==MessageBytes.T_BYTES )
            return;
        ByteChunk orig=bb;
        setByteChunk( mb.getByteChunk());
        bb.recycle();
        bb.allocate( 32, -1 );
        
        if( type==MessageBytes.T_STR ) {
            convert( mb.getString() );
            // System.out.println("XXX Converting " + mb.getString() );
        } else if( type==MessageBytes.T_CHARS ) {
            CharChunk charC=mb.getCharChunk();
            convert( charC.getBuffer(),
                                charC.getOffset(), charC.getLength());
            //System.out.println("XXX Converting " + mb.getCharChunk() );
        } else {
            if (log.isDebugEnabled()) 
                log.debug("XXX unknowon type " + type );
        }
        flushBuffer();
        //System.out.println("C2B: XXX " + bb.getBuffer() + bb.getLength()); 
        setByteChunk(orig);
    }

    /** Flush any internal buffers into the ByteOutput or the internal
     *  byte[]
     */
    public  void flushBuffer() throws IOException {
        bb.flushBuffer();
    }

}
