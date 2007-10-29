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
 */
package com.sun.el.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * @author Jacob Hookom [jacob@hookom.net]
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author: kchung $
 */
public final class MessageFactory {

    protected final static ResourceBundle bundle = ResourceBundle
            .getBundle("com.sun.el.Messages");
    /**
     * 
     */
    public MessageFactory() {
        super();
    }
    
    public static String get(final String key) {
        return bundle.getString(key);
    }

    public static String get(final String key, final Object obj0) {
        return getArray(key, new Object[] { obj0 });
    }

    public static String get(final String key, final Object obj0,
            final Object obj1) {
        return getArray(key, new Object[] { obj0, obj1 });
    }

    public static String get(final String key, final Object obj0,
            final Object obj1, final Object obj2) {
        return getArray(key, new Object[] { obj0, obj1, obj2 });
    }

    public static String get(final String key, final Object obj0,
            final Object obj1, final Object obj2, final Object obj3) {
        return getArray(key, new Object[] { obj0, obj1, obj2, obj3 });
    }

    public static String get(final String key, final Object obj0,
            final Object obj1, final Object obj2, final Object obj3,
            final Object obj4) {
        return getArray(key, new Object[] { obj0, obj1, obj2, obj3, obj4 });
    }

    public static String getArray(final String key, final Object[] objA) {
        return MessageFormat.format(bundle.getString(key), objA);
    }

}
