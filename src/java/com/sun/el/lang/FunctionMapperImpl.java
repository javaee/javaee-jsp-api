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
package com.sun.el.lang;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.el.FunctionMapper;

import com.sun.el.util.ReflectionUtil;

/**
 * @author Jacob Hookom [jacob@hookom.net]
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author: jhook $
 */
public class FunctionMapperImpl extends FunctionMapper implements
        Externalizable {

    private static final long serialVersionUID = 1L;
    
    protected Map functions = null;

    /*
     * (non-Javadoc)
     * 
     * @see javax.el.FunctionMapper#resolveFunction(java.lang.String,
     *      java.lang.String)
     */
    public Method resolveFunction(String prefix, String localName) {
        if (this.functions != null) {
            Function f = (Function) this.functions.get(prefix + ":" + localName);
            return f.getMethod();
        }
        return null;
    }

    public void addFunction(String prefix, String localName, Method m) {
        if (this.functions == null) {
            this.functions = new HashMap();
        }
        Function f = new Function(prefix, localName, m);
        synchronized (this) {
            this.functions.put(prefix+":"+localName, f);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.functions);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        this.functions = (Map) in.readObject();
    }
    
    public static class Function implements Externalizable {
    
        protected transient Method m;
        protected String owner;
        protected String name;
        protected String[] types;
        protected String prefix;
        protected String localName;
    
        /**
         * 
         */
        public Function(String prefix, String localName, Method m) {
            if (localName == null) {
                throw new NullPointerException("LocalName cannot be null");
            }
            if (m == null) {
                throw new NullPointerException("Method cannot be null");
            }
            this.prefix = prefix;
            this.localName = localName;
            this.m = m;
        }
        
        public Function() {
            // for serialization
        }
    
        /*
         * (non-Javadoc)
         * 
         * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
         */
        public void writeExternal(ObjectOutput out) throws IOException {
            
            out.writeUTF((this.prefix != null) ? this.prefix : "");
            out.writeUTF(this.localName);
            
            if (this.owner != null) {
                out.writeUTF(this.owner);
            } else {
                out.writeUTF(this.m.getDeclaringClass().getName());
            }
            if (this.name != null) {
                out.writeUTF(this.name);
            } else {
                out.writeUTF(this.m.getName());
            }
            if (this.types != null) {
                out.writeObject(this.types);
            } else {
                out.writeObject(ReflectionUtil.toTypeNameArray(this.m.getParameterTypes()));
            }
        }
    
        /*
         * (non-Javadoc)
         * 
         * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
         */
        public void readExternal(ObjectInput in) throws IOException,
                ClassNotFoundException {
            
            this.prefix = in.readUTF();
            if ("".equals(this.prefix)) this.prefix = null;
            this.localName = in.readUTF();
            this.owner = in.readUTF();
            this.name = in.readUTF();
            this.types = (String[]) in.readObject();
        }
    
        public Method getMethod() {
            if (this.m == null) {
                try {
                    Class t = Class.forName(this.owner);
                    Class[] p = ReflectionUtil.toTypeArray(this.types);
                    this.m = t.getMethod(this.name, p);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return this.m;
        }
        
        public boolean matches(String prefix, String localName) {
            if (this.prefix != null) {
                if (prefix == null) return false;
                if (!this.prefix.equals(prefix)) return false;
            }
            return this.localName.equals(localName);
        }
    
        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object obj) {
            if (obj instanceof Function) {
                return this.hashCode() == obj.hashCode();
            }
            return false;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            return (this.prefix + this.localName).hashCode();
        }
    }

}
