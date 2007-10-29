

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

import java.text.*;
import java.util.*;

import javax.servlet.http.Cookie;

// XXX use only one Date instance/request, reuse it.

/**
 * Cookie utils - generate cookie header, etc
 *
 * @author Original Author Unknown
 * @author duncan@eng.sun.com
 */
public class CookieTools {

    /** Return the header name to set the cookie, based on cookie
     *  version
     */
    public static String getCookieHeaderName(Cookie cookie) {
        int version = cookie.getVersion();

        if (version == 1) {
            return "Set-Cookie2";
        } else {
            return "Set-Cookie";
        }
    }

    /** Return the header value used to set this cookie
     *  @deprecated Use StringBuffer version
     */
    public static String getCookieHeaderValue(Cookie cookie) {
        StringBuffer buf = new StringBuffer();
        getCookieHeaderValue( cookie, buf );
        return buf.toString();
    }

    /** Return the header value used to set this cookie
     */
    public static void getCookieHeaderValue(Cookie cookie, StringBuffer buf) {
        int version = cookie.getVersion();

        // this part is the same for all cookies

        String name = cookie.getName();     // Avoid NPE on malformed cookies
        if (name == null)
            name = "";
        String value = cookie.getValue();
        if (value == null)
            value = "";
        
        buf.append(name);
        buf.append("=");
        maybeQuote(version, buf, value);

        // add version 1 specific information
        if (version == 1) {
            // Version=1 ... required
            buf.append ("; Version=1");

            // Comment=comment
            if (cookie.getComment() != null) {
                buf.append ("; Comment=");
                maybeQuote (version, buf, cookie.getComment());
            }
        }

        // add domain information, if present

        if (cookie.getDomain() != null) {
            buf.append("; Domain=");
            maybeQuote (version, buf, cookie.getDomain());
        }

        // Max-Age=secs/Discard ... or use old "Expires" format
        if (cookie.getMaxAge() >= 0) {
            if (version == 0) {
                buf.append ("; Expires=");
                if (cookie.getMaxAge() == 0)
                    DateTool.oldCookieFormat.format(new Date(10000), buf,
                                                    new FieldPosition(0));
                else
                    DateTool.oldCookieFormat.format
                        (new Date( System.currentTimeMillis() +
                                   cookie.getMaxAge() *1000L), buf,
                         new FieldPosition(0));
            } else {
                buf.append ("; Max-Age=");
                buf.append (cookie.getMaxAge());
            }
        } else if (version == 1)
          buf.append ("; Discard");

        // Path=path
        if (cookie.getPath() != null) {
            buf.append ("; Path=");
            maybeQuote (version, buf, cookie.getPath());
        }

        // Secure
        if (cookie.getSecure()) {
          buf.append ("; Secure");
        }
    }

    static void maybeQuote (int version, StringBuffer buf,
                                    String value)
    {
        if (version == 0 || isToken (value))
            buf.append (value);
        else {
            buf.append ('"');
            buf.append (value);
            buf.append ('"');
        }
    }

        //
    // from RFC 2068, token special case characters
    //
    private static final String tspecials = "()<>@,;:\\\"/[]?={} \t";

    /*
     * Return true iff the string counts as an HTTP/1.1 "token".
     */
    private static boolean isToken (String value) {
        int len = value.length ();

        for (int i = 0; i < len; i++) {
            char c = value.charAt (i);

            if (c < 0x20 || c >= 0x7f || tspecials.indexOf (c) != -1)
              return false;
        }
        return true;
    }


}
