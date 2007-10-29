

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

package org.apache.jasper.compiler.tagplugin;

/**
 * This interface is to be implemented by the plugin author, to supply
 * an alternate implementation of the tag handlers.  It can be used to
 * specify the Java codes to be generated when a tag is invoked.
 *
 * An implementation of this interface must be registered in a file
 * named "tagPlugins.xml" under WEB-INF.
 */

public interface TagPlugin {

    /**
     * Generate codes for a custom tag.
     * @param ctxt a TagPluginContext for accessing Jasper functions
     */
    void doTag(TagPluginContext ctxt);
}

