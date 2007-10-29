

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


package org.apache.catalina.authenticator;


import java.io.IOException;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.HttpResponse;
import org.apache.catalina.Realm;
import org.apache.catalina.Session;
import org.apache.catalina.deploy.LoginConfig;

import org.apache.catalina.realm.GenericPrincipal;


/**
 * An <b>Authenticator</b> and <b>Valve</b> implementation that checks
 * only security constraints not involving user authentication.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.2 $ $Date: 2005/06/11 03:10:16 $
 */

public final class NonLoginAuthenticator
    extends AuthenticatorBase {


    // ----------------------------------------------------- Instance Variables

    //START SJSAS 6202703
    /**
     * Principal name of nonlogin principal.
     */
    private static final String NONLOGIN_PRINCIPAL_NAME = "nonlogin-principal";
    
    /**
     * A dummy principal that is set to request in authenticate method.
     */
    private final GenericPrincipal NONLOGIN_PRINCIPAL =
        new GenericPrincipal(NONLOGIN_PRINCIPAL_NAME, (String) null, 
                            (java.util.List) null);
    //END SJSAS 6202703
    
    /**
     * Descriptive information about this implementation.
     */
    private static final String info =
        "org.apache.catalina.authenticator.NonLoginAuthenticator/1.0";


    // ------------------------------------------------------------- Properties


    /**
     * Return descriptive information about this Valve implementation.
     */
    public String getInfo() {

        return (this.info);

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Authenticate the user making this request, based on the specified
     * login configuration.  Return <code>true</code> if any specified
     * constraint has been satisfied, or <code>false</code> if we have
     * created a response challenge already.
     *
     * @param request Request we are processing
     * @param response Response we are creating
     * @param login Login configuration describing how authentication
     *              should be performed
     *
     * @exception IOException if an input/output error occurs
     */
    public boolean authenticate(HttpRequest request,
                                HttpResponse response,
                                LoginConfig config)
        throws IOException {

        if (debug >= 1)
            log("User authentication is not required");
        
        //START SJSAS 6202703
        request.setUserPrincipal(NONLOGIN_PRINCIPAL);
        //END SJSAS 6202703
        return (true);


    }


}
