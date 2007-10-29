

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

package org.apache.jasper.runtime;

/**
 * Interface for tracking the source files dependencies, for the purpose
 * of compiling out of date pages.  This is used for
 * 1) files that are included by page directives
 * 2) files that are included by include-prelude and include-coda in jsp:config
 * 3) files that are tag files and referenced
 * 4) TLDs referenced
 */

public interface JspSourceDependent {

   /**
    * Returns a list of files names that the current page has a source
    * dependency on.
    */
    /* GlassFish Issue 812
    public java.util.List getDependants();
    */
    // START GlassFish Issue 812
    // FIXME: Use java.lang.Object instead of java.util.List as return type
    // due to weird behavior with Eclipse JDT 3.1 in Java 5 mode
    public Object getDependants();
    // END GlassFish Issue 812
}
