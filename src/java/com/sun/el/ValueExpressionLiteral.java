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
package com.sun.el;

import java.io.Externalizable;
import java.io.IOException;
import javax.el.ELContext;
import javax.el.PropertyNotWritableException;

import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.el.ValueExpression;

import com.sun.el.lang.ELSupport;
import com.sun.el.util.MessageFactory;
import com.sun.el.util.ReflectionUtil;

public final class ValueExpressionLiteral extends ValueExpression implements
        Externalizable {

    private static final long serialVersionUID = 1L;

    private Object value;

    private Class expectedType;

    public ValueExpressionLiteral() {
        super();
    }
    
    public ValueExpressionLiteral(Object value, Class expectedType) {
        this.value = value;
        this.expectedType = expectedType;
    }

    public Object getValue(ELContext context) {
        if (this.expectedType != null) {
            return ELSupport.coerceToType(this.value, this.expectedType);
        }
        return this.value;
    }

    public void setValue(ELContext context, Object value) {
        throw new PropertyNotWritableException(MessageFactory.get(
                "error.value.literal.write", this.value));
    }

    public boolean isReadOnly(ELContext context) {
        return true;
    }

    public Class getType(ELContext context) {
        return (this.value != null) ? this.value.getClass() : null;
    }

    public Class getExpectedType() {
        return this.expectedType;
    }

    public String getExpressionString() {
        return (this.value != null) ? this.value.toString() : null;
    }

    public boolean equals(Object obj) {
        return (obj instanceof ValueExpressionLiteral && this
                .equals((ValueExpressionLiteral) obj));
    }

    public boolean equals(ValueExpressionLiteral ve) {
        return (ve != null && (this.value != null && ve.value != null && (this.value == ve.value || this.value
                .equals(ve.value))));
    }

    public int hashCode() {
        return (this.value != null) ? this.value.hashCode() : 0;
    }

    public boolean isLiteralText() {
        return true;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.value);
        out.writeUTF((this.expectedType != null) ? this.expectedType.getName()
                : "");
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        this.value = in.readObject();
        String type = in.readUTF();
        if (!"".equals(type)) {
            this.expectedType = ReflectionUtil.forName(type);
        }
    }
}
