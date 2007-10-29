

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

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.text.*;

/**
 *  Common place for date utils.
 *
 * @author dac@eng.sun.com
 * @author Jason Hunter [jch@eng.sun.com]
 * @author James Todd [gonzo@eng.sun.com]
 * @author Costin Manolache
 */
public class DateTool {

    private static StringManager sm =
        StringManager.getManager("org.apache.catalina.util");

    /**
     * US locale - all HTTP dates are in english
     */
    public final static Locale LOCALE_US = Locale.US;

    /**
     * GMT timezone - all HTTP dates are on GMT
     */
    public final static TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");

    /**
     * format for RFC 1123 date string -- "Sun, 06 Nov 1994 08:49:37 GMT"
     */
    public final static String RFC1123_PATTERN =
        "EEE, dd MMM yyyyy HH:mm:ss z";

    /** 
     * Format for http response header date field
     */
    public static final String HTTP_RESPONSE_DATE_HEADER =
        "EEE, dd MMM yyyy HH:mm:ss zzz";

    // format for RFC 1036 date string -- "Sunday, 06-Nov-94 08:49:37 GMT"
    private final static String rfc1036Pattern =
        "EEEEEEEEE, dd-MMM-yy HH:mm:ss z";

    // format for C asctime() date string -- "Sun Nov  6 08:49:37 1994"
    private final static String asctimePattern =
        "EEE MMM d HH:mm:ss yyyyy";

    /**
     * Pattern used for old cookies
     */
    public final static String OLD_COOKIE_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss z";

    /**
     * DateFormat to be used to format dates
     */
    public final static DateFormat rfc1123Format =
        new SimpleDateFormat(RFC1123_PATTERN, LOCALE_US);

    /**
     * DateFormat to be used to format old netscape cookies
     */
    public final static DateFormat oldCookieFormat =
        new SimpleDateFormat(OLD_COOKIE_PATTERN, LOCALE_US);

    public final static DateFormat rfc1036Format =
        new SimpleDateFormat(rfc1036Pattern, LOCALE_US);

    public final static DateFormat asctimeFormat =
        new SimpleDateFormat(asctimePattern, LOCALE_US);

    static {
        rfc1123Format.setTimeZone(GMT_ZONE);
        oldCookieFormat.setTimeZone(GMT_ZONE);
        rfc1036Format.setTimeZone(GMT_ZONE);
        asctimeFormat.setTimeZone(GMT_ZONE);
    }

}
