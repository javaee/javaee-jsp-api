/*
 * SessionLock.java
 *
 * Created on January 21, 2003, 4:34 PM
 * HERCULES:add
 */

package org.apache.catalina.session;


public class SessionLock {
    
    private static final String BACKGROUND_LOCK = "background_lock";
    private static final String FOREGROUND_LOCK = "foreground_lock";
    
    /** Creates a new instance of SessionLock */
    public SessionLock() {
    }

    /**
     * get the lock type
     *
     */     
    public String getLockType() {
        return _lockType;
    }

    /**
     * set the lock type - lockType must be BACKGROUND_LOCK or FOREGROUND_LOCK
     *
     * @param lockType the type of the lock
     */    
    public void setLockType(String lockType) {
        _lockType = lockType;
    }
    
    /**
     * get the foregroundRefCount
     *
     */     
    public int getForegroundRefCount() {
        return _foregroundRefCount;
    }
    
    /**
     * set the foregroundRefCount
     *
     * @param foregroundRefCount
     */    
    public void setForegroundRefCount(int foregroundRefCount) {
        _foregroundRefCount = foregroundRefCount;
    }
    
    /**
     * increment the foregroundRefCount
     *
     */      
    public void incrementForegroundRefCount() {
        _foregroundRefCount++;
    }
    
    /**
     * decrement the foregroundRefCount
     *
     */    
    public void decrementForegroundRefCount() {
        _foregroundRefCount--;
    }    
    
    /**
     * return whether lock is background locked
     *
     */    
    public boolean isBackgroundLocked() {
        if(_lockType == null) {
            return false;
        }
        return (_lockType.equals(BACKGROUND_LOCK));
    }
    
    /**
     * return whether lock is foreground locked
     *
     */     
    public boolean isForegroundLocked() {
        if(_lockType == null) {
            return false;
        }
        return (_lockType.equals(FOREGROUND_LOCK));
    }
    
    /**
     * return whether lock is locked (either foreground or background)
     *
     */       
    public boolean isLocked() {
        return (_lockType != null);
    }
   
    /**
     * unlock the lock
     * if background locked the lock will become fully unlocked
     * if foreground locked the lock will become fully unlocked
     * if foregroundRefCount was 1; otherwise it will
     * decrement the foregroundRefCount and the lock will remain foreground locked
     *
     */    
    public void unlock() {
        if(!isLocked())
            return;
        if(isBackgroundLocked()) {
            this.setLockType(null);
            this.setForegroundRefCount(0);
            return;
        }
        if(isForegroundLocked()) {
            decrementForegroundRefCount();
            if(_foregroundRefCount == 0) {
                this.setLockType(null);
            }
        }                        
    } 
    
    /**
     * unlock the lock for the foreground locked case
     * the lock will be unlocked
     * if foregroundRefCount was 1; otherwise it will
     * decrement the foregroundRefCount and the lock will remain foreground locked
     *
     */    
    public void unlockForeground() {
        //unlock if the lock is foreground locked
        //else do nothing        
        if(!isLocked())
            return;
        if(isForegroundLocked()) {
            decrementForegroundRefCount();
            if(_foregroundRefCount == 0) {
                this.setLockType(null);
            }
        }                        
    }  
    
    /**
     * unlock the lock
     * this is a force unlock; foregroundRefCount is ignored
     *
     */      
    public void unlockForegroundCompletely() {
        //unlock completely if the lock is foreground locked
        //else do nothing        
        if(!isLocked())
            return;
        if(isForegroundLocked()) {
            this.setForegroundRefCount(0);
            this.setLockType(null);
        }                        
    }      
    
    /**
     * unlock the lock for the background locked case
     * the lock will be unlocked
     *
     */     
    public void unlockBackground() {
        //unlock if the lock is background locked
        //else do nothing
        if(!isLocked())
            return;
        if(isBackgroundLocked()) {
            this.setLockType(null);
            this.setForegroundRefCount(0);
            return;
        }                        
    }     
    
    /**
     * if possible, the lock will be foreground locked
     * if it was already foreground locked; it will
     * remain so and the foregroundRefCount will be incremented
     *
     * if the lock is already background locked the method
     * will return false and the lock remains background locked 
     * (i.e. lock failed) otherwise it will return true (lock succeeded)
     */     
    public synchronized boolean lockForeground() {
        if(isBackgroundLocked()) {
            return false;
        }
        if(isForegroundLocked()) {
            incrementForegroundRefCount();
        } else {
            setForegroundRefCount(1);
        }
        setLockType(FOREGROUND_LOCK);
        return true;
    }
    
    /**
     * if possible, the lock will be background locked
     *
     * if the lock is already foreground locked the method
     * will return false and the lock remains foreground locked 
     * (i.e. lock failed) otherwise it will return true (lock succeeded)
     */      
    public synchronized boolean lockBackground() {
        if(isForegroundLocked()) {
            return false;
        } 
        setLockType(BACKGROUND_LOCK);
        setForegroundRefCount(0);
        return true;
    }
    
    /**
     * returns String representation of the state of the lock
     */     
    public String toString() {
        StringBuffer sb = new StringBuffer(50);
        sb.append("_lockType= " + _lockType);
        sb.append("\n" + "foregroundRefCount= " + _foregroundRefCount);
        return sb.toString();
    }
    
    private String _lockType = null;
    private int _foregroundRefCount = 0;
    
}
