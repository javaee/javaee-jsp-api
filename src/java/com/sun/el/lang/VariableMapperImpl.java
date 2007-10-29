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
package com.sun.el.lang;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

import javax.el.ValueExpression;
import javax.el.VariableMapper;

public class VariableMapperImpl extends VariableMapper implements Externalizable {

    private static final long serialVersionUID = 1L;
    
    private Map vars = new HashMap();
    
    public VariableMapperImpl() {
        super();
    }

    public ValueExpression resolveVariable(String variable) {
        return (ValueExpression) this.vars.get(variable);
    }

    public ValueExpression setVariable(String variable,
            ValueExpression expression) {
        return (ValueExpression) this.vars.put(variable, expression);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.vars = (Map) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.vars);
    }
}
