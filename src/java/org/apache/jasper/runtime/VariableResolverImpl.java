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

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.VariableResolver;


/**
 * <p>This is the implementation of VariableResolver in JSP 2.0,
 * using ELResolver in JSP2.1.
 * It looks up variable references in the PageContext, and also
 * recognizes references to implicit objects.
 * 
 * @author Kin-man Chung
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author: dpatil $
 **/

public class VariableResolverImpl
    implements VariableResolver
{
    private PageContext pageContext;

    //-------------------------------------
    /**
     * Constructor
     **/
    public VariableResolverImpl (PageContext pageContext) {
        this.pageContext = pageContext;
    }
  
    //-------------------------------------
    /**
     * Resolves the specified variable within the given context.
     * Returns null if the variable is not found.
     **/
    public Object resolveVariable (String pName)
            throws javax.servlet.jsp.el.ELException {

        ELContext elContext = pageContext.getELContext();
        ELResolver elResolver = elContext.getELResolver();
        try {
            return elResolver.getValue(elContext, null, pName);
        } catch (javax.el.ELException ex) {
            throw new javax.servlet.jsp.el.ELException();
        }
    }
}
