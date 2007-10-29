/*
 * SessionPurgeCapable.java
 *
 * Created on October 2, 2003, 12:28 PM
 */

package org.apache.catalina.session;

/**
 *
 * @author  lwhite
 */
public interface SessionPurgeCapable {
    
    public void clearSessions();
    
    public void clearStore();
    
}
