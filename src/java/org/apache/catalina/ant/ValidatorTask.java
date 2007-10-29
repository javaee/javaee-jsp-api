

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


package org.apache.catalina.ant;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import org.apache.commons.digester.Digester;
import org.xml.sax.InputSource;

import org.apache.catalina.startup.Constants;
import org.apache.catalina.startup.ContextConfig;


/**
 * Task for validating a web application deployment descriptor, using XML 
 * schema validation.
 *
 * @author Remy Maucherat
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/27 22:55:01 $
 * @since 5.0
 */

public class ValidatorTask extends Task {


    // ----------------------------------------------------- Instance Variables


    // ------------------------------------------------------------- Properties


    /**
     * The path to the webapp directory.
     */
    protected String path = null;

    public String getPath() {
        return (this.path);
    }

    public void setPath(String path) {
        this.path = path;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Execute the specified command.  This logic only performs the common
     * attribute validation required by all subclasses; it does not perform
     * any functional logic directly.
     *
     * @exception BuildException if a validation error occurs
     */
    public void execute() throws BuildException {

        if (path == null) {
            throw new BuildException("Must specify 'path'");
        }

        File file = new File(path, Constants.ApplicationWebXml);
        if ((!file.exists()) || (!file.canRead())) {
            throw new BuildException("Cannot find web.xml");
        }

        Digester digester = ContextConfig.createWebXmlDigester(true, true);
        try {
            file = file.getCanonicalFile();
            InputStream stream = 
                new BufferedInputStream(new FileInputStream(file));
            InputSource is = new InputSource(file.toURL().toExternalForm());
            is.setByteStream(stream);
            digester.parse(is);
        } catch (Throwable t) {
            throw new BuildException("Validation failure", t);
        }

    }


}
