/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Copyright 1999-2001,2004 The Apache Software Foundation.
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


package org.apache.catalina.core;


import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Valve;
import org.apache.catalina.ValveContext;
import org.apache.catalina.util.StringManager;


/**
 * Standard implementation of a <code>ValveContext</code>.
 *
 * @author Craig R. McClanahan
 * @author Remy Maucherat
 *
 * <IMPLEMENTATION-NOTE>
???* This class is no longer used in PE 8.0. See bug 4665318
 * @author Jean-Francois Arcand
 * </IMPLEMENTATION-NOTE>
 */

public final class StandardValveContext
    implements ValveContext {


    // ----------------------------------------------------- Instance Variables


    /**
     * The string manager for this package.
     */
    protected static StringManager sm =
        StringManager.getManager(Constants.Package);


    protected String info = 
        "org.apache.catalina.core.StandardValveContext/1.0";
    protected int stage = 0;
    protected Valve basic = null;
    protected Valve valves[] = null;


    // ------------------------------------------------------------- Properties


    /**
     * Return descriptive information about this ValveContext 
     * implementation.
     */
    public String getInfo() {
        return info;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Cause the <code>invoke()</code> method of the next Valve that is 
     * part of the Pipeline currently being processed (if any) to be 
     * executed, passing on the specified request and response objects 
     * plus this <code>ValveContext</code> instance.  Exceptions thrown by
     * a subsequently executed Valve (or a Filter or Servlet at the 
     * application level) will be passed on to our caller.
     *
     * If there are no more Valves to be executed, an appropriate
     * ServletException will be thrown by this ValveContext.
     *
     * @param request The request currently being processed
     * @param response The response currently being created
     *
     * @exception IOException if thrown by a subsequent Valve, Filter, or
     *  Servlet
     * @exception ServletException if thrown by a subsequent Valve, Filter,
     *  or Servlet
     * @exception ServletException if there are no further Valves 
     *  configured in the Pipeline currently being processed
     */
    public final void invokeNext(Request request, Response response)
        throws IOException, ServletException {

        /** STARTS OF PE 4665318
        int subscript = stage;
        stage = stage + 1;

        // Invoke the requested Valve for the current request thread
        if (subscript < valves.length) {
            valves[subscript].invoke(request, response, this);
        } else if ((subscript == valves.length) && (basic != null)) {
            basic.invoke(request, response, this);
        } else {
            throw new ServletException
                (sm.getString("standardPipeline.noValve"));
        }
        */
        // END OF PE 4665318
    }


    // -------------------------------------------------------- Package Methods


    /**
     * Reset state.
     */
    void set(Valve basic, Valve valves[]) {
        stage = 0;
        this.basic = basic;
        this.valves = valves;
    }


}

