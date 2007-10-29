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

import java.io.*;
import javax.servlet.*;

public final class ResponseUtil {

    /**
     * Copies the contents of the specified input stream to the specified
     * output stream.
     *
     * @param istream The input stream to read from
     * @param ostream The output stream to write to
     *
     * @return Exception that occurred during processing, or null
     */
    public static IOException copy(InputStream istream,
                                   ServletOutputStream ostream) {

        IOException exception = null;
        byte buffer[] = new byte[2048];
        int len = buffer.length;
        while (true) {
            try {
                len = istream.read(buffer);
                if (len == -1)
                    break;
                ostream.write(buffer, 0, len);
            } catch (IOException e) {
                exception = e;
                len = -1;
                break;
            }
        }
        return exception;

    }


    /**
     * Copies the contents of the specified input stream to the specified
     * output stream.
     *
     * @param reader The reader to read from
     * @param writer The writer to write to
     *
     * @return Exception that occurred during processing, or null
     */
    public static IOException copy(Reader reader, PrintWriter writer) {

        IOException exception = null;
        char buffer[] = new char[2048];
        int len = buffer.length;
        while (true) {
            try {
                len = reader.read(buffer);
                if (len == -1)
                    break;
                writer.write(buffer, 0, len);
            } catch (IOException e) {
                exception = e;
                len = -1;
                break;
            }
        }
        return exception;

    }

}
