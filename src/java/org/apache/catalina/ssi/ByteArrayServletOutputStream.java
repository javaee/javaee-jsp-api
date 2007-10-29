/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * @version $Revision: 1.2 $, $Date: 2005/04/29 01:28:02 $
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
