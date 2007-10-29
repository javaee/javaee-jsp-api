/*
 * Copyright 2003-2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. 
 */
 

package org.apache.catalina;


/**
 * Defines interface of classes which implement audit functionality.
 *
 * <P>An <b>Auditor</b> class can be registered with a Context and
 * will receive notification of all auditable events processed by the
 * Authenticator of that context.
 *
 * <P> IASRI 4823322
 *
 * @author Jyri J. Virkki
 * @version $Revision: 1.2 $
 *
 */

public interface Auditor
{
    
    /**
     * Notify auditor of an authentication event.
     *
     * <P>This method will get invoked on every login attempt whether
     * it was approved or denied by the authentication infrastructure.
     *
     * @param user the user for whom authentication was processed
     * @param realm the realm which handled the authentication
     * @param succcess true if the authentication succeeded, false if denied
     *
     */
    public void authentication(String user, String realm, boolean success);

    
    /**
     * Notify auditor of a servlet container invocation.
     *
     * <P>This method will get invoked on every request whether it
     * was permitted or not by the authorization infrastructure.
     *     
     * @param req the HttpRequest
     * @param success true if the invocation was allowed, false if denied.
     *
     */
    public void webInvocation(HttpRequest req, boolean success);

    
}
