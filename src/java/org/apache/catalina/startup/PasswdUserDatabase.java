

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


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Enumeration;


/**
 * Concrete implementation of the <strong>UserDatabase</code> interface
 * that processes the <code>/etc/passwd</code> file on a Unix system.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:08 $
 */

public final class PasswdUserDatabase
    implements UserDatabase {


    // --------------------------------------------------------- Constructors


    /**
     * Initialize a new instance of this user database component.
     */
    public PasswdUserDatabase() {

        super();

    }


    // --------------------------------------------------- Instance Variables


    /**
     * The pathname of the Unix password file.
     */
    private static final String PASSWORD_FILE = "/etc/passwd";


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

        BufferedReader reader = null;
        try {

            reader = new BufferedReader(new FileReader(PASSWORD_FILE));

            while (true) {

                // Accumulate the next line
                StringBuffer buffer = new StringBuffer();
                while (true) {
                    int ch = reader.read();
                    if ((ch < 0) || (ch == '\n'))
                        break;
                    buffer.append((char) ch);
                }
                String line = buffer.toString();
                if (line.length() < 1)
                    break;

                // Parse the line into constituent elements
                int n = 0;
                String tokens[] = new String[7];
                for (int i = 0; i < tokens.length; i++)
                    tokens[i] = null;
                while (n < tokens.length) {
                    String token = null;
                    int colon = line.indexOf(':');
                    if (colon >= 0) {
                        token = line.substring(0, colon);
                        line = line.substring(colon + 1);
                    } else {
                        token = line;
                        line = "";
                    }
                    tokens[n++] = token;
                }

                // Add this user and corresponding directory
                if ((tokens[0] != null) && (tokens[5] != null))
                    homes.put(tokens[0], tokens[5]);

            }

            reader.close();
            reader = null;

        } catch (Exception e) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException f) {
                    ;
                }
                reader = null;
            }
        }

    }


}
