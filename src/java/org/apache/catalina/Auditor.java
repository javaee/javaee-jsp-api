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
 * @version $Revision: 1.1.1.1 $
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
