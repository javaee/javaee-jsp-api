

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


package org.apache.catalina;


import java.beans.PropertyChangeListener;
import java.io.IOException;

//START OF RIMOD# 4820359
import javax.servlet.http.HttpSession;
//END OF RIMOD# 4820359

/**
 * A <b>Manager</b> manages the pool of Sessions that are associated with a
 * particular Container.  Different Manager implementations may support
 * value-added features such as the persistent storage of session data,
 * as well as migrating sessions for distributable web applications.
 * <p>
 * In order for a <code>Manager</code> implementation to successfully operate
 * with a <code>Context</code> implementation that implements reloading, it
 * must obey the following constraints:
 * <ul>
 * <li>Must implement <code>Lifecycle</code> so that the Context can indicate
 *     that a restart is required.
 * <li>Must allow a call to <code>stop()</code> to be followed by a call to
 *     <code>start()</code> on the same <code>Manager</code> instance.
 * </ul>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.2 $ $Date: 2005/06/26 18:35:13 $
 */

public interface Manager {


    // ------------------------------------------------------------- Properties


    /**
     * Return the Container with which this Manager is associated.
     */
    public Container getContainer();


    /**
     * Set the Container with which this Manager is associated.
     *
     * @param container The newly associated Container
     */
    public void setContainer(Container container);


    /**
     * Return the DefaultContext with which this Manager is associated.
     */
    public DefaultContext getDefaultContext();


    /**
     * Set the DefaultContext with which this Manager is associated.
     *
     * @param defaultContext The newly associated DefaultContext
     */
    public void setDefaultContext(DefaultContext defaultContext);


    /**
     * Return the distributable flag for the sessions supported by
     * this Manager.
     */
    public boolean getDistributable();


    /**
     * Set the distributable flag for the sessions supported by this
     * Manager.  If this flag is set, all user data objects added to
     * sessions associated with this manager must implement Serializable.
     *
     * @param distributable The new distributable flag
     */
    public void setDistributable(boolean distributable);


    /**
     * Return descriptive information about this Manager implementation and
     * the corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getInfo();


    /**
     * Same as getMaxInactiveIntervalSeconds
     */
    public int getMaxInactiveInterval();


    /**
     * Return the default maximum inactive interval (in seconds)
     * for Sessions created by this Manager.
     */
    public int getMaxInactiveIntervalSeconds();


    /**
     * Same as setMaxInactiveIntervalSeconds
     */
    public void setMaxInactiveInterval(int interval);


    /**
     * Set the default maximum inactive interval (in seconds)
     * for Sessions created by this Manager.
     *
     * @param interval The new default value
     */
    public void setMaxInactiveIntervalSeconds(int interval);


    /**
     * Gets the session id length (in bytes) of Sessions created by
     * this Manager.
     *
     * @return The session id length
     */
    public int getSessionIdLength();


    /**
     * Sets the session id length (in bytes) for Sessions created by this
     * Manager.
     *
     * @param sessionIdLength The session id length
     */
    public void setSessionIdLength(int idLength);


    /** 
     * Same as getSessionCount
     */
    public int getSessionCounter();


    /** 
     * Returns the total number of sessions created by this manager.
     *
     * @return Total number of sessions created by this manager.
     */
    public int getSessionCount();


    /** 
     * Same as setSessionCount
     */
    public void setSessionCounter(int sessionCounter);


    /** 
     * Sets the total number of sessions created by this manager.
     *
     * @param sessionCounter Total number of sessions created by this manager.
     */
    public void setSessionCount(int sessionCounter);


    /**
     * Gets the maximum number of sessions that have been active at the same
     * time.
     *
     * @return Maximum number of sessions that have been active at the same
     * time
     */
    public int getMaxActive();


    /**
     * (Re)sets the maximum number of sessions that have been active at the
     * same time.
     *
     * @param maxActive Maximum number of sessions that have been active at
     * the same time.
     */
    public void setMaxActive(int maxActive);


    /** 
     * Gets the number of currently active sessions.
     *
     * @return Number of currently active sessions
     */
    public int getActiveSessions();


    /**
     * Gets the number of sessions that have expired.
     *
     * @return Number of sessions that have expired
     */
    public int getExpiredSessions();


    /**
     * Sets the number of sessions that have expired.
     *
     * @param expiredSessions Number of sessions that have expired
     */
    public void setExpiredSessions(int expiredSessions);


    /**
     * Gets the number of sessions that were not created because the maximum
     * number of active sessions was reached.
     *
     * @return Number of rejected sessions
     */
    public int getRejectedSessions();


    /**
     * Sets the number of sessions that were not created because the maximum
     * number of active sessions was reached.
     *
     * @param rejectedSessions Number of rejected sessions
     */
    public void setRejectedSessions(int rejectedSessions);


    /**
     * Same as getSessionMaxAliveTimeSeconds
     */
    public int getSessionMaxAliveTime();


    /**
     * Gets the longest time (in seconds) that an expired session had been
     * alive.
     *
     * @return Longest time (in seconds) that an expired session had been
     * alive.
     */
    public int getSessionMaxAliveTimeSeconds();


    /**
     * Same as setSessionMaxAliveTimeSeconds
     */
    public void setSessionMaxAliveTime(int sessionMaxAliveTime);


    /**
     * Sets the longest time (in seconds) that an expired session had been
     * alive.
     *
     * @param sessionMaxAliveTime Longest time (in seconds) that an expired
     * session had been alive.
     */
    public void setSessionMaxAliveTimeSeconds(int sessionMaxAliveTime);


    /**
     * Same as getSessionAverageAliveTimeSeconds
     */
    public int getSessionAverageAliveTime();


    /**
     * Gets the average time (in seconds) that expired sessions had been
     * alive.
     *
     * @return Average time (in seconds) that expired sessions had been
     * alive.
     */
    public int getSessionAverageAliveTimeSeconds();


    /**
     * Same as setSessionAverageAliveTimeSeconds
     */
    public void setSessionAverageAliveTime(int sessionAverageAliveTime);


    /**
     * Sets the average time (in seconds) that expired sessions had been
     * alive.
     *
     * @param sessionAverageAliveTime Average time (in seconds) that expired
     * sessions had been alive.
     */
    public void setSessionAverageAliveTimeSeconds(int sessionAverageAliveTime);


    // --------------------------------------------------------- Public Methods


    /**
     * Add this Session to the set of active Sessions for this Manager.
     *
     * @param session Session to be added
     */
    public void add(Session session);


    /**
     * Add a property change listener to this component.
     *
     * @param listener The listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Get a session from the recycled ones or create a new empty one.
     * The PersistentManager manager does not need to create session data
     * because it reads it from the Store.
     */                                                                         
    public Session createEmptySession();

    /**
     * Construct and return a new session object, based on the default
     * settings specified by this Manager's properties.  The session
     * id will be assigned by this method, and available via the getId()
     * method of the returned session.  If a new session cannot be created
     * for any reason, return <code>null</code>.
     *
     * @exception IllegalStateException if a new session cannot be
     *  instantiated for any reason
     */
    public Session createSession();


    // START S1AS8PE 4817642
    /**
     * Construct and return a new session object, based on the default
     * settings specified by this Manager's properties, using the specified
     * session id.
     *
     * @param sessionId the session id to assign to the new session
     *
     * @exception IllegalStateException if a new session cannot be
     *  instantiated for any reason
     *
     * @return the new session, or <code>null</code> if a session with the
     * requested id already exists
     */
    public Session createSession(String sessionId);
    // END S1AS8PE 4817642


    /**
     * Return the active Session, associated with this Manager, with the
     * specified session id (if any); otherwise return <code>null</code>.
     *
     * @param id The session id for the session to be returned
     *
     * @exception IllegalStateException if a new session cannot be
     *  instantiated for any reason
     * @exception IOException if an input/output error occurs while
     *  processing this request
     */
    public Session findSession(String id) throws IOException;


    /**
     * Return the set of active Sessions associated with this Manager.
     * If this Manager has no active Sessions, a zero-length array is returned.
     */
    public Session[] findSessions();


    /**
     * Load any currently active sessions that were previously unloaded
     * to the appropriate persistence mechanism, if any.  If persistence is not
     * supported, this method returns without doing anything.
     *
     * @exception ClassNotFoundException if a serialized class cannot be
     *  found during the reload
     * @exception IOException if an input/output error occurs
     */
    public void load() throws ClassNotFoundException, IOException;


    /**
     * Remove this Session from the active Sessions for this Manager.
     *
     * @param session Session to be removed
     */
    public void remove(Session session);


    /**
     * Remove a property change listener from this component.
     *
     * @param listener The listener to remove
     */
    public void removePropertyChangeListener(PropertyChangeListener listener);


    /**
     * Save any currently active sessions in the appropriate persistence
     * mechanism, if any.  If persistence is not supported, this method
     * returns without doing anything.
     *
     * @exception IOException if an input/output error occurs
     */
    public void unload() throws IOException;

     
    //PWC Extension
    //START OF RIMOD# 4820359 -- Support for iWS6.0 session managers
    /**
     * Perform any operations when the request is finished.
     */
    public void update(HttpSession session) throws Exception;
    //END OF RIMOD# 4820359

    //START OF RIMOD# 5056989 -- Support for iWS6.0 style session ids
    /**
     * Allow the caller to add a prefix to the session id.
     */
    public void setSessionIDPrefix(String prefix);
    //END OF RIMOD# 5056989

}
