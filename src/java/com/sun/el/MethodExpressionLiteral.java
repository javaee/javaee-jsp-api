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
 */package com.sun.el;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.MethodExpression;
import javax.el.MethodInfo;

import com.sun.el.lang.ELSupport;
import com.sun.el.util.ReflectionUtil;

public class MethodExpressionLiteral extends MethodExpression implements Externalizable {

    private Class expectedType;

    private String expr;
    
    private Class[] paramTypes;
    
    public MethodExpressionLiteral() {
        // do nothing
    }
    
    public MethodExpressionLiteral(String expr, Class expectedType, Class[] paramTypes) {
        this.expr = expr;
        this.expectedType = expectedType;
        this.paramTypes = paramTypes;
    }

    public MethodInfo getMethodInfo(ELContext context) throws ELException {
        return new MethodInfo(this.expr, this.expectedType, this.paramTypes);
    }

    public Object invoke(ELContext context, Object[] params) throws ELException {
        if (this.expectedType != null) {
            return ELSupport.coerceToType(this.expr, this.expectedType);
        } else {
            return this.expr;
        }
    }

    public String getExpressionString() {
        return this.expr;
    }

    public boolean equals(Object obj) {
        return (obj instanceof MethodExpressionLiteral && this.hashCode() == obj.hashCode());
    }

    public int hashCode() {
        return this.expr.hashCode();
    }

    public boolean isLiteralText() {
        return true;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.expr = in.readUTF();
        String type = in.readUTF();
        if (!"".equals(type)) {
            this.expectedType = ReflectionUtil.forName(type);
        }
        this.paramTypes = ReflectionUtil.toTypeArray(((String[]) in
                .readObject()));
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(this.expr);
        out.writeUTF((this.expectedType != null) ? this.expectedType.getName()
                : "");
        out.writeObject(ReflectionUtil.toTypeNameArray(this.paramTypes));
    }
}
