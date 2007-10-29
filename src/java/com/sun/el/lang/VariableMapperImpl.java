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
