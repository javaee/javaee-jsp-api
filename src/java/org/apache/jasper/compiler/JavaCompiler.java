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
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */

package org.apache.jasper.compiler;

import java.io.File;
import java.io.Writer;
import java.util.List;

import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;

interface JavaCompiler {

    /**
     * Start Java compilation
     * @param className Name of the class under compilation
     * @param pageNode Internal form for the page, used for error line mapping
     */
    public JavacErrorDetail[] compile(String className, Node.Nodes pageNodes)
        throws JasperException;

    /**
     * Get a Writer for the Java file.
     * The writer is used by JSP compiler.  This method allows the Java
     * compiler control where the Java file should be generated so it knows how
     * to handle the input for java compilation accordingly.
     */
    public Writer getJavaWriter(String javaFileName, String javaEncoding)
        throws JasperException;

    /**
     * Remove/save the generated Java File from/to disk
     */
    public void doJavaFile(boolean keep) throws JasperException;

    /**
     * Return the time the class file was generated.
     */
    public long getClassLastModified();

    /**
     * Save the generated class file to disk, if not already done.
     */
    public void saveClassFile(String className, String classFileName);

    /**
     * Java Compiler options.
     */
    public void setClassPath(List<File> cp);
    public void setDebug(boolean debug);
    public void setExtdirs(String exts);
    public void setTargetVM(String targetVM);
    public void setSourceVM(String sourceVM);

    /**
     * Initializations
     */
    public void init(JspCompilationContext ctxt,
                     ErrorDispatcher err,
                     boolean suppressLogging);
}
    
