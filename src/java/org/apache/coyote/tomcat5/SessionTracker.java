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
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */
package org.apache.coyote.tomcat5;

import org.apache.catalina.Session;
import org.apache.catalina.SessionEvent;
import org.apache.catalina.SessionListener;

/**
 * Class responsible for keeping track of the total number of active sessions
 * associated with an HTTP request.
 *
 * <p>A given HTTP request may be associated with more than one active
 * session if it has been dispatched to one or more foreign contexts.
 *
 * <p>The number of active sessions being tracked is used to determine whether
 * or not any session information needs to be reflected in the response (e.g.,
 * in the form of a response cookie). If no active sessions are associated
 * with the request by the time the response is committed, no session
 * information will be reflected in the response.
 *
 * See GlassFish Issue 896 for additional details.
 */
public class SessionTracker implements SessionListener {

    // The number of currently tracked sessions
    private int count;

    // The session id that is shared by all tracked sessions
    private String trackedSessionId;

    private CoyoteResponse response;

    private static final String JSESSIONIDSSO = "JSESSIONIDSSO";
    private static final String SET_COOKIE = "Set-Cookie";

    /**
     * Processes the given session event, by unregistering this SessionTracker
     * as a session listener from the session that is the source of the event,
     * and by decrementing the counter of currently tracked sessions.
     *
     * @param event The session event
     */
    public void sessionEvent(SessionEvent event) {

        if (!Session.SESSION_DESTROYED_EVENT.equals(event.getType())) {
            return;
        }

        Session session = event.getSession();
           
        synchronized (this) {
            if (session.getIdInternal() != null
                    && session.getIdInternal().equals(trackedSessionId)) {
                count--;
                if (count == 0) {
                    trackedSessionId = null;
                    if (response != null
                            && response.getHeader(SET_COOKIE) != null) {
                        String ssoId = null;
                        String[] values = response.getHeaderValues(SET_COOKIE);
                        if (values != null) {
                            for (int i=0; i<values.length; i++) {
                                if (values[i].startsWith(JSESSIONIDSSO)) {
                                    ssoId = values[i];
                                    break;
                                }
                            }
                        }

                        response.removeCookie();
                        if (ssoId != null) {
                            response.addHeader(SET_COOKIE, ssoId);
                        }
                    }
                }
            }

            session.removeSessionListener(this);
        }
    }

    /**
     * Gets the number of active sessions that are being tracked.
     *
     * @return The number of active sessions being tracked
     */
    public int getActiveSessions() {
        return count;
    }

    /**
     * Gets the id of the sessions that are being tracked.
     *
     * Notice that since all sessions are associated with the same request,
     * albeit in different context, they all share the same id.
     *
     * @return The id of the sessions that are being tracked
     */
    public String getSessionId() {
        return trackedSessionId;
    }

    /**
     * Tracks the given session, by registering this SessionTracker as a
     * listener with the given session, and by incrementing the counter of
     * currently tracked sessions.
     *
     * @param session The session to track
     */
    public synchronized void track(Session session) {

        if (trackedSessionId == null) {
            trackedSessionId = session.getIdInternal();
        } else if (!trackedSessionId.equals(session.getIdInternal())) {
            throw new IllegalArgumentException("Should never reach here");
        }

        count++;

        session.addSessionListener(this);
    }

    /**
     * Associates the given response with this SessionTracker.
     *
     * If the number of tracked sessions drops to zero, this SessionTracker
     * will remove the Set-Cookie from the given response.
     *
     * @param response The response from which to remove the Set-Cookie
     * header if the number of tracked sessions drops to zero
     */
    public synchronized void setResponse(CoyoteResponse response) {
        this.response = response;
    }

    /**
     * Resets this session tracker.
     */
    public synchronized void reset() {
        count = 0;
        trackedSessionId = null;
        response = null;
    }

}
