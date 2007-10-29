

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
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.util.Base64;
import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;



/**
 * An <b>Authenticator</b> and <b>Valve</b> implementation of HTTP BASIC
 * Authentication, as outlined in RFC 2617:  "HTTP Authentication: Basic
 * and Digest Access Authentication."
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.5 $ $Date: 2006/10/19 22:48:17 $
 */

public class BasicAuthenticator
    extends AuthenticatorBase {
    private static Log log = LogFactory.getLog(BasicAuthenticator.class);



    // ----------------------------------------------------- Instance Variables


    /**
     * The Base64 helper object for this class.
     */
    protected static final Base64 base64Helper = new Base64();


    /**
     * Descriptive information about this implementation.
     */
    protected static final String info =
        "org.apache.catalina.authenticator.BasicAuthenticator/1.0";


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

        // Have we already authenticated someone?
        Principal principal =
            ((HttpServletRequest) request.getRequest()).getUserPrincipal();
        if (principal != null) {
            if (log.isDebugEnabled())
                log.debug("Already authenticated '" + principal.getName() + "'");
            return (true);
        }

        // Validate any credentials already included with this request
        HttpServletRequest hreq =
            (HttpServletRequest) request.getRequest();
        HttpServletResponse hres =
            (HttpServletResponse) response.getResponse();
        String authorization = request.getAuthorization();

        /* IASRI 4868073 
        String username = parseUsername(authorization);
        String password = parsePassword(authorization);
        principal = context.getRealm().authenticate(username, password);
        if (principal != null) {
            register(request, response, principal, Constants.BASIC_METHOD,
                     username, password);
            return (true);
        }
        */
        // BEGIN IASRI 4868073
        // Only attempt to parse and validate the authorization if one was
        // sent by the client. No reason to attempt to login with null
        // authorization which must fail anyway. With basic auth this
        // scenario always occurs first so this is a common case. This
        // will also prevent logging the audit message for failure to
        // authenticate null user (since login failures are always logged
        // per psarc req).

        if (authorization != null) {
            String username = parseUsername(authorization);
            String password = parsePassword(authorization);
            principal = context.getRealm().authenticate(username, password);
            if (principal != null) {
                register(request, response, principal, Constants.BASIC_METHOD,
                         username, password);
                String ssoId = (String) request.getNote(
                    Constants.REQ_SSOID_NOTE);
                if (ssoId != null) {
                    getSession(request, true);
                }
                return (true);
            }
        }
        // END IASRI 4868073

        // Send an "unauthorized" response and an appropriate challenge
        String realmName = config.getRealmName();
        if (realmName == null)
            realmName = hreq.getServerName() + ":" + hreq.getServerPort();
    //        if (debug >= 1)
    //            log("Challenging for realm '" + realmName + "'");
        hres.setHeader("WWW-Authenticate",
                       "Basic realm=\"" + realmName + "\"");
        hres.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        //      hres.flushBuffer();
        return (false);

    }


    // ------------------------------------------------------ Protected Methods


    /**
     * Parse the username from the specified authorization credentials.
     * If none can be found, return <code>null</code>.
     *
     * @param authorization Authorization credentials from this request
     */
    protected String parseUsername(String authorization) {

        if (authorization == null)
            return (null);
        if (!authorization.toLowerCase().startsWith("basic "))
            return (null);
        authorization = authorization.substring(6).trim();

        // Decode and parse the authorization credentials
        String unencoded =
          new String(base64Helper.decode(authorization.getBytes()));
        int colon = unencoded.indexOf(':');
        if (colon < 0)
            return (null);
        String username = unencoded.substring(0, colon);
        //        String password = unencoded.substring(colon + 1).trim();
        return (username);

    }


    /**
     * Parse the password from the specified authorization credentials.
     * If none can be found, return <code>null</code>.
     *
     * @param authorization Authorization credentials from this request
     */
    protected String parsePassword(String authorization) {

        if (authorization == null)
            return (null);
        if (!authorization.startsWith("Basic "))
            return (null);
        authorization = authorization.substring(6).trim();

        // Decode and parse the authorization credentials
        String unencoded =
          new String(base64Helper.decode(authorization.getBytes()));
        int colon = unencoded.indexOf(':');
        if (colon < 0)
            return (null);
        //        String username = unencoded.substring(0, colon).trim();
        String password = unencoded.substring(colon + 1);
        return (password);

    }



}
