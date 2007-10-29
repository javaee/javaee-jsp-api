

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

public final class If implements TagPlugin {

    public void doTag(TagPluginContext ctxt) {
	String condV = ctxt.getTemporaryVariableName();
	ctxt.generateJavaSource("boolean " + condV + "=");
	ctxt.generateAttribute("test");
	ctxt.generateJavaSource(";");
	if (ctxt.isAttributeSpecified("var")) {
	    String scope = "PageContext.PAGE_SCOPE";
	    if (ctxt.isAttributeSpecified("scope")) {
		String scopeStr = ctxt.getConstantAttribute("scope");
		if ("request".equals(scopeStr)) {
		    scope = "PageContext.REQUEST_SCOPE";
		} else if ("session".equals(scopeStr)) {
		    scope = "PageContext.SESSION_SCOPE";
		} else if ("application".equals(scopeStr)) {
		    scope = "PageContext.APPLICATION_SCOPE";
		}
	    }
	    ctxt.generateJavaSource("_jspx_page_context.setAttribute(");
	    ctxt.generateAttribute("var");
	    ctxt.generateJavaSource(", new Boolean(" + condV + ")," + scope + ");");
	}
	ctxt.generateJavaSource("if (" + condV + "){");
	ctxt.generateBody();
	ctxt.generateJavaSource("}");
    }
}
