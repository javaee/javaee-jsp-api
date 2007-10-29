/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.catalina.realm;


import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;


/**
 * <p>Implementation of the JAAS <strong>CallbackHandler</code> interface,
 * used to negotiate delivery of the username and credentials that were
 * specified to our constructor.  No interaction with the user is required
 * (or possible).</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.2 $ $Date: 2005/04/29 01:27:42 $
 */

public class JAASCallbackHandler implements CallbackHandler {


    // ------------------------------------------------------------ Constructor


    /**
     * Construct a callback handler configured with the specified values.
     *
     * @param realm Our associated JAASRealm instance
     * @param username Username to be authenticated with
     * @param password Password to be authenticated with
     */
    public JAASCallbackHandler(JAASRealm realm, String username,
                               String password) {

        super();
        this.realm = realm;
        this.username = username;
        this.password = password;

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The password to be authenticated with.
     */
    protected String password = null;


    /**
     * The associated <code>JAASRealm</code> instance.
     */
    protected JAASRealm realm = null;


    /**
     * The username to be authenticated with.
     */
    protected String username = null;


    // --------------------------------------------------------- Public Methods


    /**
     * Retrieve the information requested in the provided Callbacks.  This
     * implementation only recognizes <code>NameCallback</code> and
     * <code>PasswordCallback</code> instances.
     *
     * @param callbacks The set of callbacks to be processed
     *
     * @exception IOException if an input/output error occurs
     * @exception UnsupportedCallbackException if the login method requests
     *  an unsupported callback type
     */
    public void handle(Callback callbacks[])
        throws IOException, UnsupportedCallbackException {

        for (int i = 0; i < callbacks.length; i++) {

            if (callbacks[i] instanceof NameCallback) {
                if (realm.getDebug() >= 3)
                    realm.log("Returning username " + username);
                ((NameCallback) callbacks[i]).setName(username);
            } else if (callbacks[i] instanceof PasswordCallback) {
                if (realm.getDebug() >= 3)
                    realm.log("Returning password " + password);
                  final char[] passwordcontents;
                  if (password != null) {
                      passwordcontents = password.toCharArray();
                  } else {
                      passwordcontents = new char[0];
                  }
                  ((PasswordCallback) callbacks[i]).setPassword
                      (passwordcontents);
            } else {
                throw new UnsupportedCallbackException(callbacks[i]);
            }


        }

    }


}
