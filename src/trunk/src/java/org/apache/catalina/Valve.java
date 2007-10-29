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
 * <p>A <b>Valve</b> is a request processing component associated with a
 * particular Container.  A series of Valves are generally associated with
 * each other into a Pipeline.  The detailed contract for a Valve is included
 * in the description of the <code>invoke()</code> method below.</p>
 *
 * <b>HISTORICAL NOTE</b>:  The "Valve" name was assigned to this concept
 * because a valve is what you use in a real world pipeline to control and/or
 * modify flows through it.
 *
 * @author Craig R. McClanahan
 * @author Gunnar Rjnning
 * @author Peter Donald
 * @version $Revision: 1.2 $ $Date: 2005/04/29 01:26:45 $
 */

public interface Valve {



    // START OF IASRI 4665318
    // ----------------------------------------------------- Manifest Constants


    /**
     * A valve returns this value to indicate (to the pipeline) that the next
     * valve in the pipeline can be invoked.
     */
    public static final int INVOKE_NEXT = 1;


    /**
     * A valve returns this value to indicate that no further processing
     * of the request should take place (along the rest of the pipeline).
     * All valves that are function as 'basic' valves return this value
     * as they are the 'last' valve in the pipeline. A valve (such as an
     * authenticator) may return this value to stop a request from being
     * processed further because the user/password could not be verified.
     */
    public static final int END_PIPELINE = 2;


    // END OF IASRI 4665318
    //-------------------------------------------------------------- Properties


    /**
     * Return descriptive information about this Valve implementation.
     */
    public String getInfo();


    //---------------------------------------------------------- Public Methods


    /**
     * <p>Perform request processing as required by this Valve.</p>
     *
     * <p>An individual Valve <b>MAY</b> perform the following actions, in
     * the specified order:</p>
     * <ul>
     * <li>Examine and/or modify the properties of the specified Request and
     *     Response.
     * <li>Examine the properties of the specified Request, completely generate
     *     the corresponding Response, and return control to the caller.
     * <li>Examine the properties of the specified Request and Response, wrap
     *     either or both of these objects to supplement their functionality,
     *     and pass them on.
     * <li>If the corresponding Response was not generated (and control was not
     *     returned, call the next Valve in the pipeline (if there is one) by
     *     executing <code>context.invokeNext()</code>.
     * <li>Examine, but not modify, the properties of the resulting Response
     *     (which was created by a subsequently invoked Valve or Container).
     * </ul>
     *
     * <p>A Valve <b>MUST NOT</b> do any of the following things:</p>
     * <ul>
     * <li>Change request properties that have already been used to direct
     *     the flow of processing control for this request (for instance,
     *     trying to change the virtual host to which a Request should be
     *     sent from a pipeline attached to a Host or Context in the
     *     standard implementation).
     * <li>Create a completed Response <strong>AND</strong> pass this
     *     Request and Response on to the next Valve in the pipeline.
     * <li>Consume bytes from the input stream associated with the Request,
     *     unless it is completely generating the response, or wrapping the
     *     request before passing it on.
     * <li>Modify the HTTP headers included with the Response after the
     *     <code>invokeNext()</code> method has returned.
     * <li>Perform any actions on the output stream associated with the
     *     specified Response after the <code>invokeNext()</code> method has
     *     returned.
     * </ul>
     *
     * @param request The servlet request to be processed
     * @param response The servlet response to be created
     * @param context The valve context used to invoke the next valve
     *  in the current processing pipeline
     *
     * @exception IOException if an input/output error occurs, or is thrown
     *  by a subsequently invoked Valve, Filter, or Servlet
     * @exception ServletException if a servlet error occurs, or is thrown
     *  by a subsequently invoked Valve, Filter, or Servlet
     */
    /** START IASRI 4665318
    public void invoke(Request request, Response response,
                       ValveContext context)
        throws IOException, ServletException;
    */
    // START OF IASRI 4665318
    /**
     * <li>In order to reduce the stack depth, a ValveContext is no longer
     *     passed into the <code>invoke()</code> method. Instead, the method
     *     returns either INVOKE_NEXT or END_PIPELINE to the caller
     *     (<code>Pipeline.invoke()</code>). This return code is used to
     *     determine whether the next valve in the pipeline should be
     *     invoked.
     *
     * @return <code>INVOKE_NEXT</code> or <code>END_PIPELINE</code>
     */
    public int invoke(Request request, Response response)
        throws IOException, ServletException;


    /**
     * <p>Perform post-request processing as required by this Valve.</p>
     *
     * @param request The servlet request to be processed
     * @param response The servlet response to be created
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void postInvoke(Request request, Response response)
        throws IOException, ServletException;
    // END OF IASRI 4665318


}

