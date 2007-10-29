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
package org.apache.catalina.ssi;


/**
 * This class is used by SSIMediator and SSIConditional to keep track of state
 * information necessary to process the nested conditional commands ( if, elif,
 * else, endif ).
 * 
 * @version $Revision: 467222 $
 * @author Dan Sandberg
 * @author Paul Speed
 */
class SSIConditionalState {
    /**
     * Set to true if the current conditional has already been completed, i.e.:
     * a branch was taken.
     */
    boolean branchTaken = false;
    /**
     * Counts the number of nested false branches.
     */
    int nestingCount = 0;
    /**
     * Set to true if only conditional commands ( if, elif, else, endif )
     * should be processed.
     */
    boolean processConditionalCommandsOnly = false;
}