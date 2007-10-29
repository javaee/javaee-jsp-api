

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


package org.apache.naming;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.Context;

/**
 * Handles the associations :
 * <ul>
 * <li>Catalina context name with the NamingContext</li>
 * <li>Calling thread with the NamingContext</li>
 * </ul>
 *
 * @author Remy Maucherat
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:15 $
 */

public class ContextBindings {


    // -------------------------------------------------------------- Variables


    /**
     * Bindings name - naming context. Keyed by name.
     */
    private static Hashtable contextNameBindings = new Hashtable();


    /**
     * Bindings thread - naming context. Keyed by thread id.
     */
    private static Hashtable threadBindings = new Hashtable();


    /**
     * Bindings thread - name. Keyed by thread id.
     */
    private static Hashtable threadNameBindings = new Hashtable();


    /**
     * Bindings class loader - naming context. Keyed by CL id.
     */
    private static Hashtable clBindings = new Hashtable();


    /**
     * Bindings class loader - name. Keyed by CL id.
     */
    private static Hashtable clNameBindings = new Hashtable();


    /**
     * The string manager for this package.
     */
    protected static StringManager sm = 
        StringManager.getManager(Constants.Package);


    // --------------------------------------------------------- Public Methods


    /**
     * Binds a context name.
     * 
     * @param name Name of the context
     * @param context Associated naming context instance
     */
    public static void bindContext(Object name, Context context) {
        bindContext(name, context, null);
    }


    /**
     * Binds a context name.
     * 
     * @param name Name of the context
     * @param context Associated naming context instance
     * @param token Security token
     */
    public static void bindContext(Object name, Context context, 
                                   Object token) {
        if (ContextAccessController.checkSecurityToken(name, token))
            contextNameBindings.put(name, context);
    }


    /**
     * Unbind context name.
     * 
     * @param name Name of the context
     */
    public static void unbindContext(Object name) {
        unbindContext(name, null);
    }


    /**
     * Unbind context name.
     * 
     * @param name Name of the context
     * @param token Security token
     */
    public static void unbindContext(Object name, Object token) {
        if (ContextAccessController.checkSecurityToken(name, token))
            contextNameBindings.remove(name);
    }


    /**
     * Retrieve a naming context.
     * 
     * @param name Name of the context
     */
    static Context getContext(Object name) {
        return (Context) contextNameBindings.get(name);
    }


    /**
     * Binds a naming context to a thread.
     * 
     * @param name Name of the context
     */
    public static void bindThread(Object name) 
        throws NamingException {
        bindThread(name, null);
    }


    /**
     * Binds a naming context to a thread.
     * 
     * @param name Name of the context
     * @param token Security token
     */
    public static void bindThread(Object name, Object token) 
        throws NamingException {
        if (ContextAccessController.checkSecurityToken(name, token)) {
            Context context = (Context) contextNameBindings.get(name);
            if (context == null)
                throw new NamingException
                    (sm.getString("contextBindings.unknownContext", name));
            threadBindings.put(Thread.currentThread(), context);
            threadNameBindings.put(Thread.currentThread(), name);
        }
    }


    /**
     * Unbinds a naming context to a thread.
     * 
     * @param name Name of the context
     */
    public static void unbindThread(Object name) {
        unbindThread(name, null);
    }


    /**
     * Unbinds a naming context to a thread.
     * 
     * @param name Name of the context
     * @param token Security token
     */
    public static void unbindThread(Object name, Object token) {
        if (ContextAccessController.checkSecurityToken(name, token)) {
            threadBindings.remove(Thread.currentThread());
            threadNameBindings.remove(Thread.currentThread());
        }
    }


    /**
     * Retrieves the naming context bound to a thread.
     */
    public static Context getThread()
        throws NamingException {
        Context context = 
            (Context) threadBindings.get(Thread.currentThread());
        if (context == null)
            throw new NamingException
                (sm.getString("contextBindings.noContextBoundToThread"));
        return context;
    }


    /**
     * Retrieves the naming context name bound to a thread.
     */
    static Object getThreadName()
        throws NamingException {
        Object name = threadNameBindings.get(Thread.currentThread());
        if (name == null)
            throw new NamingException
                (sm.getString("contextBindings.noContextBoundToThread"));
        return name;
    }


    /**
     * Tests if current thread is bound to a context.
     */
    public static boolean isThreadBound() {
        return (threadBindings.containsKey(Thread.currentThread()));
    }


    /**
     * Binds a naming context to a class loader.
     * 
     * @param name Name of the context
     */
    public static void bindClassLoader(Object name) 
        throws NamingException {
        bindClassLoader(name, null);
    }


    /**
     * Binds a naming context to a thread.
     * 
     * @param name Name of the context
     * @param token Security token
     */
    public static void bindClassLoader(Object name, Object token) 
        throws NamingException {
        bindClassLoader
            (name, token, Thread.currentThread().getContextClassLoader());
    }


    /**
     * Binds a naming context to a thread.
     * 
     * @param name Name of the context
     * @param token Security token
     */
    public static void bindClassLoader(Object name, Object token, 
                                       ClassLoader classLoader) 
        throws NamingException {
        if (ContextAccessController.checkSecurityToken(name, token)) {
            Context context = (Context) contextNameBindings.get(name);
            if (context == null)
                throw new NamingException
                    (sm.getString("contextBindings.unknownContext", name));
            clBindings.put(classLoader, context);
            clNameBindings.put(classLoader, name);
        }
    }


    /**
     * Unbinds a naming context to a class loader.
     * 
     * @param name Name of the context
     */
    public static void unbindClassLoader(Object name) {
        unbindClassLoader(name, null);
    }


    /**
     * Unbinds a naming context to a class loader.
     * 
     * @param name Name of the context
     * @param token Security token
     */
    public static void unbindClassLoader(Object name, Object token) {
        unbindClassLoader(name, token, 
                          Thread.currentThread().getContextClassLoader());
    }


    /**
     * Unbinds a naming context to a class loader.
     * 
     * @param name Name of the context
     * @param token Security token
     */
    public static void unbindClassLoader(Object name, Object token, 
                                         ClassLoader classLoader) {
        if (ContextAccessController.checkSecurityToken(name, token)) {
            Object n = clNameBindings.get(classLoader);
            if ((n==null) || !(n.equals(name))) {
                return;
            }
            clBindings.remove(classLoader);
            clNameBindings.remove(classLoader);
        }
    }


    /**
     * Retrieves the naming context bound to a class loader.
     */
    public static Context getClassLoader()
        throws NamingException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Context context = null;
        do {
            context = (Context) clBindings.get(cl);
            if (context != null) {
                return context;
            }
        } while ((cl = cl.getParent()) != null);
        throw new NamingException
            (sm.getString("contextBindings.noContextBoundToCL"));
    }


    /**
     * Retrieves the naming context name bound to a class loader.
     */
    static Object getClassLoaderName()
        throws NamingException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Object name = null;
        do {
            name = clNameBindings.get(cl);
            if (name != null) {
                return name;
            }
        } while ((cl = cl.getParent()) != null);
        throw new NamingException
            (sm.getString("contextBindings.noContextBoundToCL"));
    }


    /**
     * Tests if current class loader is bound to a context.
     */
    public static boolean isClassLoaderBound() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        do {
            if (clBindings.containsKey(cl)) {
                return true;
            }
        } while ((cl = cl.getParent()) != null);
        return false;
    }


}
