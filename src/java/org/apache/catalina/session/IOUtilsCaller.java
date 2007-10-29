/*
 * IOUtilsCaller.java
 *
 * Created on October 13, 2003, 11:49 AM
 */

package org.apache.catalina.session;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
//FIXME: move this to commons so it can be added back to api
//import com.sun.ejb.spi.io.NonSerializableObjectHandler;

/**
 *
 * @author  Administrator
 */
public interface IOUtilsCaller {
    
    public ObjectInputStream createObjectInputStream(
        InputStream is,
        boolean resolveObject,
        ClassLoader loader) throws Exception;
    
    public ObjectOutputStream createObjectOutputStream(
        OutputStream os,
        boolean replaceObject) throws IOException;  
    
}

