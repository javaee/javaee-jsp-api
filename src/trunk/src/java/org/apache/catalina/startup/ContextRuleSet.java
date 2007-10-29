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


package org.apache.catalina.startup;


import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Loader;
import org.apache.catalina.Wrapper;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.RuleSetBase;
import org.xml.sax.Attributes;


/**
 * <p><strong>RuleSet</strong> for processing the contents of a
 * Context or DefaultContext definition element.  To enable parsing of a
 * DefaultContext, be sure to specify a prefix that ends with "/Default".</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.2 $ $Date: 2005/04/29 01:28:09 $
 */

public class ContextRuleSet extends RuleSetBase {


    // ----------------------------------------------------- Instance Variables


    /**
     * The matching pattern prefix to use for recognizing our elements.
     */
    protected String prefix = null;


    // ------------------------------------------------------------ Constructor


    /**
     * Construct an instance of this <code>RuleSet</code> with the default
     * matching pattern prefix.
     */
    public ContextRuleSet() {

        this("");

    }


    /**
     * Construct an instance of this <code>RuleSet</code> with the specified
     * matching pattern prefix.
     *
     * @param prefix Prefix for matching pattern rules (including the
     *  trailing slash character)
     */
    public ContextRuleSet(String prefix) {

        super();
        this.namespaceURI = null;
        this.prefix = prefix;

    }


    // --------------------------------------------------------- Public Methods


    /**
     * <p>Add the set of Rule instances defined in this RuleSet to the
     * specified <code>Digester</code> instance, associating them with
     * our namespace URI (if any).  This method should only be called
     * by a Digester instance.</p>
     *
     * @param digester Digester instance to which the new Rule instances
     *  should be added.
     */
    public void addRuleInstances(Digester digester) {

        if (!isDefaultContext()) {
            digester.addObjectCreate(prefix + "Context",
                                     "org.apache.catalina.core.StandardContext",
                                     "className");
        } else {
            digester.addObjectCreate(prefix + "Context",
                                     "org.apache.catalina.core.StandardDefaultContext",
                                     "className");
        }
        digester.addSetProperties(prefix + "Context");
        if (!isDefaultContext()) {
            digester.addRule(prefix + "Context",
                             new CopyParentClassLoaderRule(digester));
            digester.addRule(prefix + "Context",
                             new LifecycleListenerRule
                                 (digester,
                                  "org.apache.catalina.startup.ContextConfig",
                                  "configClass"));
            digester.addRule(prefix + "Context",
                             new SetDocBaseRule(digester));
            digester.addSetNext(prefix + "Context",
                                "addChild",
                                "org.apache.catalina.Container");
        } else {
            digester.addSetNext(prefix + "Context",
                                "addDefaultContext",
                                "org.apache.catalina.DefaultContext");
        }

        digester.addCallMethod(prefix + "Context/InstanceListener",
                               "addInstanceListener", 0);

        digester.addObjectCreate(prefix + "Context/Listener",
                                 null, // MUST be specified in the element
                                 "className");
        digester.addSetProperties(prefix + "Context/Listener");
        digester.addSetNext(prefix + "Context/Listener",
                            "addLifecycleListener",
                            "org.apache.catalina.LifecycleListener");

        digester.addRule(prefix + "Context/Loader",
                         new CreateLoaderRule
                             (digester,
                              "org.apache.catalina.loader.WebappLoader",
                              "className"));
        digester.addSetProperties(prefix + "Context/Loader");
        digester.addSetNext(prefix + "Context/Loader",
                            "setLoader",
                            "org.apache.catalina.Loader");

        digester.addObjectCreate(prefix + "Context/Logger",
                                 null, // MUST be specified in the element
                                 "className");
        digester.addSetProperties(prefix + "Context/Logger");
        digester.addSetNext(prefix + "Context/Logger",
                            "setLogger",
                            "org.apache.catalina.Logger");

        digester.addObjectCreate(prefix + "Context/Manager",
                                 "org.apache.catalina.session.StandardManager",
                                 "className");
        digester.addSetProperties(prefix + "Context/Manager");
        digester.addSetNext(prefix + "Context/Manager",
                            "setManager",
                            "org.apache.catalina.Manager");

        digester.addObjectCreate(prefix + "Context/Manager/Store",
                                 null, // MUST be specified in the element
                                 "className");
        digester.addSetProperties(prefix + "Context/Manager/Store");
        digester.addSetNext(prefix + "Context/Manager/Store",
                            "setStore",
                            "org.apache.catalina.Store");

        digester.addObjectCreate(prefix + "Context/Parameter",
                                 "org.apache.catalina.deploy.ApplicationParameter");
        digester.addSetProperties(prefix + "Context/Parameter");
        digester.addSetNext(prefix + "Context/Parameter",
                            "addApplicationParameter",
                            "org.apache.catalina.deploy.ApplicationParameter");

        digester.addObjectCreate(prefix + "Context/Realm",
                                 null, // MUST be specified in the element
                                 "className");
        digester.addSetProperties(prefix + "Context/Realm");
        digester.addSetNext(prefix + "Context/Realm",
                            "setRealm",
                            "org.apache.catalina.Realm");

        digester.addObjectCreate(prefix + "Context/ResourceLink",
                                 "org.apache.catalina.deploy.ContextResourceLink");
        digester.addSetProperties(prefix + "Context/ResourceLink");
        digester.addSetNext(prefix + "Context/ResourceLink",
                            "addResourceLink",
                            "org.apache.catalina.deploy.ContextResourceLink");

        digester.addObjectCreate(prefix + "Context/Resources",
                                 "org.apache.naming.resources.FileDirContext",
                                 "className");
        digester.addSetProperties(prefix + "Context/Resources");
        digester.addSetNext(prefix + "Context/Resources",
                            "setResources",
                            "javax.naming.directory.DirContext");

        digester.addObjectCreate(prefix + "Context/Valve",
                                 null, // MUST be specified in the element
                                 "className");
        digester.addSetProperties(prefix + "Context/Valve");
        digester.addSetNext(prefix + "Context/Valve",
                            "addValve",
                            "org.apache.catalina.Valve");

        digester.addCallMethod(prefix + "Context/WrapperLifecycle",
                               "addWrapperLifecycle", 0);

        digester.addCallMethod(prefix + "Context/WrapperListener",
                               "addWrapperListener", 0);

    }


    // ------------------------------------------------------ Protected Methods


    /**
     * Are we processing a DefaultContext element?
     */
    protected boolean isDefaultContext() {

        return (prefix.endsWith("/Default"));

    }


}


// ----------------------------------------------------------- Private Classes


/**
 * Rule that creates a new <code>Loader</code> instance, with the parent
 * class loader associated with the top object on the stack (which must be
 * a <code>Container</code>), and pushes it on to the stack.
 */

final class CreateLoaderRule extends Rule {

    public CreateLoaderRule(Digester digester, String loaderClass,
                            String attributeName) {

        super(digester);
        this.loaderClass = loaderClass;
        this.attributeName = attributeName;

    }

    private String attributeName;

    private String loaderClass;

    public void begin(Attributes attributes) throws Exception {

        // Look up the required parent class loader
        Container container = (Container) digester.peek();
        ClassLoader parentClassLoader = container.getParentClassLoader();

        // Instantiate a new Loader implementation object
        String className = loaderClass;
        if (attributeName != null) {
            String value = attributes.getValue(attributeName);
            if (value != null)
                className = value;
        }
        Class clazz = Class.forName(className);
        Class types[] = { ClassLoader.class };
        Object args[] = { parentClassLoader };
        Constructor constructor = clazz.getDeclaredConstructor(types);
        Loader loader = (Loader) constructor.newInstance(args);

        // Push the new loader onto the stack
        digester.push(loader);
        if (digester.getDebug() >= 1)
            digester.log("new " + loader.getClass().getName());

    }

    public void end() throws Exception {

        Loader loader = (Loader) digester.pop();
        if (digester.getDebug() >= 1)
            digester.log("pop " + loader.getClass().getName());

    }


}
