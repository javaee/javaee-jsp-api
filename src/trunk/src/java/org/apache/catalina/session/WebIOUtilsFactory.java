/*
 * WebIOUtilsFactory.java
 *
 * Created on October 13, 2003, 9:51 AM
 */

package org.apache.catalina.session;

/**
 *
 * @author  Administrator
 */
public class WebIOUtilsFactory {
    
    private static final String IO_UTILITY_CLASS_NAME = "com.sun.ejb.base.io.IOUtilsCallerImpl";
    
    /** Creates a new instance of WebCustomObjectStreamFactory */
    public WebIOUtilsFactory() {
    }
    
    public IOUtilsCaller createWebIOUtil() {
        IOUtilsCaller webIOUtil = null;
        try {
            webIOUtil = 
                (IOUtilsCaller) (Class.forName(IO_UTILITY_CLASS_NAME)).newInstance();
        } catch (Exception ex) {
            //FIXME: log error
        }        
        return webIOUtil;
    }
     
    
}
