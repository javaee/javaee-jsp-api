

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
package org.apache.jasper.compiler;

import javax.servlet.jsp.tagext.*;

/**
 * TagInfo extension used by tag handlers that are implemented via tag files.
 * This class provides access to the name of the Map used to store the
 * dynamic attribute names and values passed to the custom action invocation.
 * This information is used by the code generator.
 */
class JasperTagInfo extends TagInfo {

    private String dynamicAttrsMapName;

    public JasperTagInfo(String tagName,
			 String tagClassName,
			 String bodyContent,
			 String infoString,
			 TagLibraryInfo taglib,
			 TagExtraInfo tagExtraInfo,
			 TagAttributeInfo[] attributeInfo,
			 String displayName,
			 String smallIcon,
			 String largeIcon,
			 TagVariableInfo[] tvi,
			 String mapName) {

	super(tagName, tagClassName, bodyContent, infoString, taglib,
	      tagExtraInfo, attributeInfo, displayName, smallIcon, largeIcon,
	      tvi);
	this.dynamicAttrsMapName = mapName;
    }

    public String getDynamicAttributesMapName() {
	return dynamicAttrsMapName;
    }

    public boolean hasDynamicAttributes() {
        return dynamicAttrsMapName != null;
    }
}
