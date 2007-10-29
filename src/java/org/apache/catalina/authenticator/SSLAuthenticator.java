

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
import java.security.cert.X509Certificate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Globals;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.HttpResponse;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Realm;
import org.apache.catalina.Session;
import org.apache.catalina.deploy.LoginConfig;



/**
 * An <b>Authenticator</b> and <b>Valve</b> implementation of authentication
 * that utilizes SSL certificates to identify client users.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:04 $
 */

public class SSLAuthenticator
    extends AuthenticatorBase {


    // ------------------------------------------------------------- Properties


    /**
     * Descriptive information about this implementation.
     */
    protected static final String info =
        "org.apache.catalina.authenticator.SSLAuthenticator/1.0";


    /**
     * Return descriptive information about this Valve implementation.
     */
    public String getInfo() {

        return (this.info);

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Authenticate the user by checking for the existence of a certificate
     * chain, and optionally asking a trust manager to validate that we trust
     * this user.
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

        // Have we already authenticated someone?
        Principal principal =
            ((HttpServletRequest) request.getRequest()).getUserPrincipal();
        if (principal != null) {
            if (debug >= 1)
                log("Already authenticated '" + principal.getName() + "'");
            return (true);
        }

        // Retrieve the certificate chain for this client
        HttpServletResponse hres =
            (HttpServletResponse) response.getResponse();
        if (debug >= 1)
            log(" Looking up certificates");

        X509Certificate certs[] = (X509Certificate[])
            request.getRequest().getAttribute(Globals.CERTIFICATES_ATTR);
        if ((certs == null) || (certs.length < 1)) {
            certs = (X509Certificate[])
                request.getRequest().getAttribute(Globals.SSL_CERTIFICATE_ATTR);
        }
        if ((certs == null) || (certs.length < 1)) {
            if (debug >= 1)
                log("  No certificates included with this request");
            /* S1AS 4878272
            hres.sendError(HttpServletResponse.SC_BAD_REQUEST,
                           sm.getString("authenticator.certificates"));
            */
            // BEGIN S1AS 4878272
            hres.sendError(HttpServletResponse.SC_BAD_REQUEST);
            response.setDetailMessage(sm.getString("authenticator.certificates"));
            // END S1AS 4878272
            return (false);
        }

        // Authenticate the specified certificate chain
        principal = context.getRealm().authenticate(certs);
        if (principal == null) {
            if (debug >= 1)
                log("  Realm.authenticate() returned false");
            /* S1AS 4878272
            hres.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                           sm.getString("authenticator.unauthorized"));
            */
            // BEGIN S1AS 4878272
            hres.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            response.setDetailMessage(sm.getString("authenticator.unauthorized"));
            // END S1AS 4878272
            return (false);
        }

        // Cache the principal (if requested) and record this authentication
        register(request, response, principal, Constants.CERT_METHOD,
                 null, null);
        return (true);

    }


    // ------------------------------------------------------ Lifecycle Methods


    /**
     * Initialize the database we will be using for client verification
     * and certificate validation (if any).
     *
     * @exception LifecycleException if this component detects a fatal error
     *  that prevents this component from being used
     */
    public void start() throws LifecycleException {

        super.start();

    }


    /**
     * Finalize the database we used for client verification and
     * certificate validation (if any).
     *
     * @exception LifecycleException if this component detects a fatal error
     *  that prevents this component from being used
     */
    public void stop() throws LifecycleException {

        super.stop();

    }


}
