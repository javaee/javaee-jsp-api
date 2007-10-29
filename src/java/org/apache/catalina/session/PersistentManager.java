

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


package org.apache.catalina.session;

/**
 * Implementation of the <b>Manager</b> interface that makes use of
 * a Store to swap active Sessions to disk. It can be configured to
 * achieve several different goals:
 *
 * <li>Persist sessions across restarts of the Container</li>
 * <li>Fault tolerance, keep sessions backed up on disk to allow
 *     recovery in the event of unplanned restarts.</li>
 * <li>Limit the number of active sessions kept in memory by
 *     swapping less active sessions out to disk.</li>
 *
 * @version $Revision: 1.2 $
 * @author Kief Morris (kief@kief.com)
 */

public final class PersistentManager extends PersistentManagerBase {


    // ----------------------------------------------------- Instance Variables


    /**
     * The descriptive information about this implementation.
     */
    private static final String info = "PersistentManager/1.0";


    /**
     * The descriptive name of this Manager implementation (for logging).
     */
    protected static final String name = "PersistentManager";


    // ------------------------------------------------------------- Properties


    /**
     * Return descriptive information about this Manager implementation and
     * the corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getInfo() {

        return (this.info);

    }

    /**
     * Return the descriptive short name of this Manager implementation.
     */
    public String getName() {

        return (name);

    }
 }

