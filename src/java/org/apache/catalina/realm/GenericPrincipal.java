

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


package org.apache.catalina.realm;


import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import org.apache.catalina.Realm;


/**
 * Generic implementation of <strong>java.security.Principal</strong> that
 * is available for use by <code>Realm</code> implementations.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:06 $
 */

public class GenericPrincipal implements Principal {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new Principal, associated with the specified Realm, for the
     * specified username and password.
     *
     * @param realm The Realm that owns this Principal
     * @param name The username of the user represented by this Principal
     * @param password Credentials used to authenticate this user
     */
    public GenericPrincipal(Realm realm, String name, String password) {

        this(realm, name, password, null);

    }


    /**
     * Construct a new Principal, associated with the specified Realm, for the
     * specified username and password, with the specified role names
     * (as Strings).
     *
     * @param realm The Realm that owns this principal
     * @param name The username of the user represented by this Principal
     * @param password Credentials used to authenticate this user
     * @param roles List of roles (must be Strings) possessed by this user
     */
    public GenericPrincipal(Realm realm, String name, String password,
                            List roles) {

        super();
        this.realm = realm;
        this.name = name;
        this.password = password;
        if (roles != null) {
            this.roles = new String[roles.size()];
            this.roles = (String[]) roles.toArray(this.roles);
            if (this.roles.length > 0)
                Arrays.sort(this.roles);
        }
    }

    public GenericPrincipal(String name, String password,
                            List roles) {

        super();
        this.name = name;
        this.password = password;
        if (roles != null) {
            this.roles = new String[roles.size()];
            this.roles = (String[]) roles.toArray(this.roles);
            if (this.roles.length > 0)
                Arrays.sort(this.roles);
        }
    }

    // ------------------------------------------------------------- Properties


    /**
     * The username of the user represented by this Principal.
     */
    protected String name = null;

    public String getName() {
        return (this.name);
    }


    /**
     * The authentication credentials for the user represented by
     * this Principal.
     */
    protected String password = null;

    public String getPassword() {
        return (this.password);
    }


    /**
     * The Realm with which this Principal is associated.
     */
    protected Realm realm = null;

    public Realm getRealm() {
        return (this.realm);
    }

    void setRealm( Realm realm ) {
        this.realm=realm;
    }


    /**
     * The set of roles associated with this user.
     */
    protected String roles[] = new String[0];

    public String[] getRoles() {
        return (this.roles);
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Does the user represented by this Principal possess the specified role?
     *
     * @param role Role to be tested
     */
    public boolean hasRole(String role) {

        if("*".equals(role)) // Special 2.4 role meaning everyone
            return true;
        if (role == null)
            return (false);
        return (Arrays.binarySearch(roles, role) >= 0);

    }


    /**
     * Return a String representation of this object, which exposes only
     * information that should be public.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("GenericPrincipal[");
        sb.append(this.name);
        sb.append("(");
        for( int i=0;i<roles.length; i++ ) {
            sb.append( roles[i]).append(",");
        }
        sb.append(")]");
        return (sb.toString());

    }


}
