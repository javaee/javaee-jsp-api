

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

package org.apache.jasper.tagplugins.jstl;

import org.apache.jasper.compiler.tagplugin.*;

public final class When implements TagPlugin {

    public void doTag(TagPluginContext ctxt) {
	// Get the parent context to determine if this is the first <c:when>
	TagPluginContext parentContext = ctxt.getParentContext();
	if (parentContext == null) {
	    ctxt.dontUseTagPlugin();
	    return;
	}

	if ("true".equals(parentContext.getPluginAttribute("hasBeenHere"))) {
	    ctxt.generateJavaSource("} else if(");
	    // See comment below for the reason we generate the extra "}" here.
	}
	else {
	    ctxt.generateJavaSource("if(");
	    parentContext.setPluginAttribute("hasBeenHere", "true");
	}
	ctxt.generateAttribute("test");
	ctxt.generateJavaSource("){");
	ctxt.generateBody();

	// We don't generate the closing "}" for the "if" here because there
	// may be whitespaces in between <c:when>'s.  Instead we delay
	// generating it until the next <c:when> or <c:otherwise> or
	// <c:choose>
    }
}
