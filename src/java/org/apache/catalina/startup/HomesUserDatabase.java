

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


package org.apache.catalina.startup;


import java.io.File;
import java.util.Hashtable;
import java.util.Enumeration;


/**
 * Concrete implementation of the <strong>UserDatabase</code> interface
 * considers all directories in a directory whose pathname is specified
 * to our constructor to be "home" directories for those users.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:08 $
 */

public final class HomesUserDatabase
    implements UserDatabase {


    // --------------------------------------------------------- Constructors


    /**
     * Initialize a new instance of this user database component.
     */
    public HomesUserDatabase() {

        super();

    }


    // --------------------------------------------------- Instance Variables


    /**
     * The set of home directories for all defined users, keyed by username.
     */
    private Hashtable homes = new Hashtable();


    /**
     * The UserConfig listener with which we are associated.
     */
    private UserConfig userConfig = null;


    // ----------------------------------------------------------- Properties


    /**
     * Return the UserConfig listener with which we are associated.
     */
    public UserConfig getUserConfig() {

        return (this.userConfig);

    }


    /**
     * Set the UserConfig listener with which we are associated.
     *
     * @param userConfig The new UserConfig listener
     */
    public void setUserConfig(UserConfig userConfig) {

        this.userConfig = userConfig;
        init();

    }


    // ------------------------------------------------------- Public Methods


    /**
     * Return an absolute pathname to the home directory for the specified user.
     *
     * @param user User for which a home directory should be retrieved
     */
    public String getHome(String user) {

        return ((String) homes.get(user));

    }


    /**
     * Return an enumeration of the usernames defined on this server.
     */
    public Enumeration getUsers() {

        return (homes.keys());

    }


    // ------------------------------------------------------ Private Methods


    /**
     * Initialize our set of users and home directories.
     */
    private void init() {

        String homeBase = userConfig.getHomeBase();
        File homeBaseDir = new File(homeBase);
        if (!homeBaseDir.exists() || !homeBaseDir.isDirectory())
            return;
        String homeBaseFiles[] = homeBaseDir.list();

        for (int i = 0; i < homeBaseFiles.length; i++) {
            File homeDir = new File(homeBaseDir, homeBaseFiles[i]);
            if (!homeDir.isDirectory() || !homeDir.canRead())
                continue;
            homes.put(homeBaseFiles[i], homeDir.toString());
        }


    }


}
