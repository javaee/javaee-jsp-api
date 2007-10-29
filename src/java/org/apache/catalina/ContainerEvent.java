

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


import java.util.EventObject;


/**
 * General event for notifying listeners of significant changes on a Container.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:01 $
 */

public final class ContainerEvent
    extends EventObject {


    /**
     * The Container on which this event occurred.
     */
    private Container container = null;


    /**
     * The event data associated with this event.
     */
    private Object data = null;


    /**
     * The event type this instance represents.
     */
    private String type = null;


    /**
     * Construct a new ContainerEvent with the specified parameters.
     *
     * @param container Container on which this event occurred
     * @param type Event type
     * @param data Event data
     */
    public ContainerEvent(Container container, String type, Object data) {

        super(container);
        this.container = container;
        this.type = type;
        this.data = data;

    }


    /**
     * Return the event data of this event.
     */
    public Object getData() {

        return (this.data);

    }


    /**
     * Return the Container on which this event occurred.
     */
    public Container getContainer() {

        return (this.container);

    }


    /**
     * Return the event type of this event.
     */
    public String getType() {

        return (this.type);

    }


    /**
     * Return a string representation of this event.
     */
    public String toString() {

        return ("ContainerEvent['" + getContainer() + "','" +
                getType() + "','" + getData() + "']");

    }


}
