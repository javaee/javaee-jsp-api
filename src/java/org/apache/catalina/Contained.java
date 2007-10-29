

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


package org.apache.catalina;


/**
 * <p>Decoupling interface which specifies that an implementing class is
 * associated with at most one <strong>Container</strong> instance.</p>
 *
 * @author Craig R. McClanahan
 * @author Peter Donald
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:01 $
 */

public interface Contained {


    //-------------------------------------------------------------- Properties


    /**
     * Return the <code>Container</code> with which this instance is associated
     * (if any); otherwise return <code>null</code>.
     */
    public Container getContainer();


    /**
     * Set the <code>Container</code> with which this instance is associated.
     *
     * @param container The Container instance with which this instance is to
     *  be associated, or <code>null</code> to disassociate this instance
     *  from any Container
     */
    public void setContainer(Container container);


}
