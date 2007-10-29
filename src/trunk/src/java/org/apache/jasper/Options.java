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

package org.apache.jasper;

import java.io.File;

import org.apache.jasper.compiler.JspConfig;
import org.apache.jasper.compiler.TagPluginManager;
import org.apache.jasper.compiler.TldLocationsCache;

/**
 * A class to hold all init parameters specific to the JSP engine. 
 *
 * @author Anil K. Vijendran
 * @author Hans Bergsten
 * @author Pierre Delisle
 */
public interface Options {

    /**
     * Returns true if Jasper issues a compilation error instead of a runtime
     * Instantiation error if the class attribute specified in useBean action
     * is invalid.
     */
    public boolean getErrorOnUseBeanInvalidClassAttribute();

    /**
     * Are we keeping generated code around?
     */
    public boolean getKeepGenerated();

    /**
     * Returns true if tag handler pooling is enabled, false otherwise.
     */
    public boolean isPoolingEnabled();

    /**
     * Are we supporting HTML mapped servlets?
     */
    public boolean getMappedFile();

    /**
     * Should errors be sent to client or thrown into stderr?
     */
    public boolean getSendErrorToClient();
 
    /**
     * Should we include debug information in compiled class?
     */
    public boolean getClassDebugInfo();

    /**
     * Background compile thread check interval in seconds
     */
    public int getCheckInterval();

    /**
     * Is Jasper being used in development mode?
     */
    public boolean getDevelopment();

    /**
     * Is the generation of SMAP info for JSR45 debugging suppressed?
     */
    public boolean isSmapSuppressed();

    /**
     * Indicates whether SMAP info for JSR45 debugging should be dumped to a
     * file.
     * Ignored is suppressSmap() is true
     */
    public boolean isSmapDumped();

    /**
     * Should white spaces between directives or actions be trimmed?
     */
    public boolean getTrimSpaces();

    /**
     * Class ID for use in the plugin tag when the browser is IE. 
     */
    public String getIeClassId();

    /**
     * What is my scratch dir?
     */
    public File getScratchDir();

    /**
     * What classpath should I use while compiling the servlets
     * generated from JSP files?
     */
    public String getClassPath();

    // START PWC 1.1 6281941
    public String getSource();
    // END PWC 1.1 6281941

    /**
     * Compiler to use.
     */
    public String getCompiler();

    /**
     * The cache for the location of the TLD's
     * for the various tag libraries 'exposed'
     * by the web application.
     * A tag library is 'exposed' either explicitely in 
     * web.xml or implicitely via the uri tag in the TLD 
     * of a taglib deployed in a jar file (WEB-INF/lib).
     *
     * @return the instance of the TldLocationsCache
     * for the web-application.
     */
    public TldLocationsCache getTldLocationsCache();

    /**
     * Java platform encoding to generate the JSP
     * page servlet.
     */
    public String getJavaEncoding();

    /**
     * boolean flag to tell Ant whether to fork JSP page compilations.
     */
    public boolean getFork();

    /**
     * Obtain JSP configuration informantion specified in web.xml.  
     */
    public JspConfig getJspConfig();

    /**
     * Is generation of X-Powered-By response header enabled/disabled?
     */
    public boolean isXpoweredBy();

    /**
     * Obtain a Tag Plugin Manager
     */
    public TagPluginManager getTagPluginManager();

    /**
     * Are Text strings to be generated as char arrays?
     */
    public boolean genStringAsCharArray();

    /**
     * Modification test interval.
     */
    public int getModificationTestInterval();

    // BEGIN S1AS 6181923
    /**
     * Returns the value of the usePrecompiled (or use-precompiled) init
     * param.
     */
    public boolean getUsePrecompiled();
    // END S1AS 6181923

    // START SJSWS
    /**
     * Gets initial capacity of HashMap which maps JSPs to their corresponding
     * servlets.
     */
    public int getInitialCapacity();
    // END SJSWS 

}
