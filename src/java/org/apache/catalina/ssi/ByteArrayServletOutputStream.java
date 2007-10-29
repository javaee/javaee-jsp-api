

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

package org.apache.catalina.ssi;

import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import javax.servlet.ServletOutputStream;

/**
 * Class that extends ServletOuputStream, used as a wrapper
 * from within <code>SsiInclude</code>
 *
 * @author Bip Thelin
 * @version $Revision: 1.1.1.1 $, $Date: 2005/05/27 22:55:07 $
 * @see ServletOutputStream and ByteArrayOutputStream
 */
public class ByteArrayServletOutputStream extends ServletOutputStream {
    /**
     * Our buffer to hold the stream
     */
    protected ByteArrayOutputStream _buf = null;

    /**
     * Construct a new ServletOutputStream
     *
     */
    public ByteArrayServletOutputStream() {
        _buf = new ByteArrayOutputStream();
    }

    /**
     * Write our stream to the <code>OutputStream</code> provided.
     *
     * @param out the OutputStream to write this stream to
     * @exception IOException if an input/output error occurs
     */
    public byte[] toByteArray() {
        return _buf.toByteArray();
    }

    /**
     * Write to our buffer
     *
     * @param b The parameter to write
     */
    public void write(int b) {
        _buf.write(b);
    }
}
