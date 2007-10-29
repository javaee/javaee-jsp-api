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


package org.apache.catalina;


import java.io.IOException;
import javax.servlet.ServletException;


/**
 * <p>A <b>ValveContext</b> is the mechanism by which a Valve can trigger the
 * execution of the next Valve in a Pipeline, without having to know anything
 * about the internal implementation mechanisms.  An instance of a class
 * implementing this interface is passed as a parameter to the
 * <code>Valve.invoke()</code> method of each executed Valve.</p>
 *
 * <p><strong>IMPLEMENTATION NOTE</strong>: It is up to the implementation of
 * ValveContext to ensure that simultaneous requests being processed (by
 * separate threads) through the same Pipeline do not interfere with each
 * other's flow of control.</p>
 *
 * @author Craig R. McClanahan
 * @author Gunnar Rjnning
 * @author Peter Donald
 * @version $Revision: 1.2 $ $Date: 2005/04/29 01:26:45 $
 */

public interface ValveContext {


    //-------------------------------------------------------------- Properties


    /**
     * Return descriptive information about this ValveContext implementation.
     */
    public String getInfo();


    //---------------------------------------------------------- Public Methods


    /**
     * Cause the <code>invoke()</code> method of the next Valve that is part of
     * the Pipeline currently being processed (if any) to be executed, passing
     * on the specified request and response objects plus this
     * <code>ValveContext</code> instance.  Exceptions thrown by a subsequently
     * executed Valve (or a Filter or Servlet at the application level) will be
     * passed on to our caller.
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
     * @exception ServletException if there are no further Valves configured
     *  in the Pipeline currently being processed
     */
    public void invokeNext(Request request, Response response)
        throws IOException, ServletException;


}
