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
package org.apache.jasper.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.Options;
import org.apache.jasper.servlet.JspServletWrapper;
import org.apache.jasper.util.SystemLogHandler;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PatternSet;
// START PWC 6441271
import org.apache.jasper.Constants;
import org.apache.tomcat.util.threads.ThreadPool;
// END PWC 6441271

/**
 * Main JSP compiler class. This class uses Ant for compiling.
 *
 * @author Anil K. Vijendran
 * @author Mandar Raje
 * @author Pierre Delisle
 * @author Kin-man Chung
 * @author Remy Maucherat
 * @author Mark Roth
 */
public class Compiler {
    /* GlassFish Issue 812
    private static com.sun.org.apache.commons.logging.Log log=
        com.sun.org.apache.commons.logging.LogFactory.getLog( Compiler.class );
    */
    // START GlassFish Issue 812
    protected static com.sun.org.apache.commons.logging.Log log=
        com.sun.org.apache.commons.logging.LogFactory.getLog( Compiler.class );
    // END GlassFish Issue 812

    // ----------------------------------------------------------------- Static


    /* PWC 6441271
    // Some javac are not thread safe; use a lock to serialize compilation, 
    static Object javacLock = new Object();
    */
    // START PWC 6441271
    // Use a threadpool and force it to 1 to simulate serialization
    private static ThreadPool threadPool = null;
    private static int minThreads = Constants.DEFAULT_MIN_THREADS;
    private static int maxThreads = Constants.DEFAULT_MAX_THREADS;
    private static String lineSeparator = System.getProperty("line.separator");
    // END PWC 6441271


    // ----------------------------------------------------- Instance Variables


    protected JspCompilationContext ctxt;

    /* GlassFish Issue 812
    private ErrorDispatcher errDispatcher;
    */
    // START GlassFish Issue 812
    protected ErrorDispatcher errDispatcher;
    // END GlassFish Issue 812
    private PageInfo pageInfo;
    private JspServletWrapper jsw;
    private JasperAntLogger logger;
    private TagFileProcessor tfp;

    // START GlassFish Issue 812
    private boolean jspcMode;
    // END GlassFish Issue 812

    protected Project project=null;

    protected Options options;

    protected Node.Nodes pageNodes;
    // ------------------------------------------------------------ Constructor


    /* GlassFish Issue 812
    public Compiler(JspCompilationContext ctxt) {
        this(ctxt, null);
    }


    public Compiler(JspCompilationContext ctxt, JspServletWrapper jsw) {
        this.jsw = jsw;
        this.ctxt = ctxt;
        this.options = ctxt.getOptions();
    }
    */

    // START GlassFish Issue 812
    public void init(JspCompilationContext ctxt, JspServletWrapper jsw) {
        this.jsw = jsw;
        this.ctxt = ctxt;
        this.options = ctxt.getOptions();
    }
    // END GlassFish Issue 812


    // Lazy eval - if we don't need to compile we probably don't need the project
    private Project getProject() {

        if( project!=null ) return project;

        // Initializing project
        project = new Project();
        logger = new JasperAntLogger();
        logger.setOutputPrintStream(System.out);
        logger.setErrorPrintStream(System.err);
	logger.setMessageOutputLevel(Project.MSG_INFO);
        project.addBuildListener( logger);
	if (System.getProperty("catalina.home") != null) {
            project.setBasedir( System.getProperty("catalina.home"));
        }
        
        if( options.getCompiler() != null ) {
            if( log.isDebugEnabled() )
                log.debug("Compiler " + options.getCompiler() );
            project.setProperty("build.compiler", options.getCompiler() );
        }
        project.init();
        return project;
    }

    class JasperAntLogger extends DefaultLogger {

        private StringBuffer reportBuf = new StringBuffer();

        protected void printMessage(final String message,
                                    final PrintStream stream,
                                    final int priority) {
        }

        protected void log(String message) {
            reportBuf.append(message);
            /* PWC 6441271
            reportBuf.append(System.getProperty("line.separator"));
            */
            // START PWC 6441271
            reportBuf.append(lineSeparator);
            // END PWC 6441271
        }

        protected String getReport() {
            String report = reportBuf.toString();
            reportBuf.setLength(0);
            return report;
        }
    }

    // --------------------------------------------------------- Public Methods


    /** 
     * Compile the jsp file into equivalent servlet in .java file
     * @return a smap for the current JSP page, if one is generated,
     *         null otherwise
     */
    private String[] generateJava() throws Exception {
        
        String[] smapStr = null;

        long t1, t2, t3, t4;

        t1 = t2 = t3 = t4 = 0;

        if (log.isDebugEnabled()) {
            t1 = System.currentTimeMillis();
        }

        // Setup page info area
        pageInfo = new PageInfo(new BeanRepository(ctxt.getClassLoader(),
                                                   errDispatcher),
                                ctxt.getJspFile());

        JspConfig jspConfig = options.getJspConfig();
        JspProperty jspProperty =
            jspConfig.findJspProperty(ctxt.getJspFile());

        /*
         * If the current uri is matched by a pattern specified in
         * a jsp-property-group in web.xml, initialize pageInfo with
         * those properties.
         */
        pageInfo.setELIgnored(JspUtil.booleanValue(
                                            jspProperty.isELIgnored()));
        pageInfo.setScriptingInvalid(JspUtil.booleanValue(
                                            jspProperty.isScriptingInvalid()));
        pageInfo.setTrimDirectiveWhitespaces(JspUtil.booleanValue(
                                            jspProperty.getTrimSpaces()));
        pageInfo.setDeferredSyntaxAllowedAsLiteral(JspUtil.booleanValue(
                                            jspProperty.getPoundAllowed()));
        if (jspProperty.getIncludePrelude() != null) {
            pageInfo.setIncludePrelude(jspProperty.getIncludePrelude());
        }
        if (jspProperty.getIncludeCoda() != null) {
	    pageInfo.setIncludeCoda(jspProperty.getIncludeCoda());
        }

        String javaFileName = ctxt.getServletJavaFileName();
        ServletWriter writer = null;

        try {
            // Setup the ServletWriter
            String javaEncoding = ctxt.getOptions().getJavaEncoding();
            OutputStreamWriter osw = null; 

            try {
                osw = new OutputStreamWriter(
                            new FileOutputStream(javaFileName), javaEncoding);
            } catch (UnsupportedEncodingException ex) {
                errDispatcher.jspError("jsp.error.needAlternateJavaEncoding",
                                       javaEncoding);
            }

            writer = new ServletWriter(new PrintWriter(osw));
            ctxt.setWriter(writer);

            // Reset the temporary variable counter for the generator.
            JspUtil.resetTemporaryVariableName();

	    // Parse the file
	    ParserController parserCtl = new ParserController(ctxt, this);
	    pageNodes = parserCtl.parse(ctxt.getJspFile());

	    if (ctxt.isPrototypeMode()) {
                // generate prototype .java file for the tag file
                Generator.generate(writer, this, pageNodes);
                writer.close();
                writer = null;
                return null;
            }

            // Validate and process attributes
            Validator.validate(this, pageNodes);

            if (log.isDebugEnabled()) {
                t2 = System.currentTimeMillis();
            }

            // Collect page info
            Collector.collect(this, pageNodes);

            // Compile (if necessary) and load the tag files referenced in
            // this compilation unit.
            tfp = new TagFileProcessor();
            tfp.loadTagFiles(this, pageNodes);

            if (log.isDebugEnabled()) {
                t3 = System.currentTimeMillis();
            }
        
            // Determine which custom tag needs to declare which scripting vars
            ScriptingVariabler.set(pageNodes, errDispatcher);

            // Optimizations by Tag Plugins
            TagPluginManager tagPluginManager = options.getTagPluginManager();
            tagPluginManager.apply(pageNodes, errDispatcher, pageInfo);

            // Optimization: concatenate contiguous template texts.
            TextOptimizer.concatenate(this, pageNodes);

            // Generate static function mapper codes.
            ELFunctionMapper.map(this, pageNodes);

            // generate servlet .java file
            Generator.generate(writer, this, pageNodes);
            writer.close();
            writer = null;

            // The writer is only used during the compile, dereference
            // it in the JspCompilationContext when done to allow it
            // to be GC'd and save memory.
            ctxt.setWriter(null);

            if (log.isDebugEnabled()) {
                t4 = System.currentTimeMillis();
                log.debug("Generated "+ javaFileName + " total="
                          + (t4-t1) + " generate=" + (t4-t3)
                          + " validate=" + (t2-t1));
            }

        } catch (Exception e) {
            if (writer != null) {
                try {
                    writer.close();
                    writer = null;
                } catch (Exception e1) {
                    // do nothing
                }
            }
            // Remove the generated .java file
            new File(javaFileName).delete();            
            throw e;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e2) {
                    // do nothing
                }
            }
        }
        
        // JSR45 Support
        if (! options.isSmapSuppressed()) {
            smapStr = SmapUtil.generateSmap(ctxt, pageNodes);
        }

        // If any proto type .java and .class files was generated,
        // the prototype .java may have been replaced by the current
        // compilation (if the tag file is self referencing), but the
        // .class file need to be removed, to make sure that javac would
        // generate .class again from the new .java file just generated.
        tfp.removeProtoTypeFiles(ctxt.getClassFileName());

        return smapStr;
    }


    /** 
     * Compile the servlet from .java file to .class file
     */
    /* GlassFish Issue 812
    private void generateClass(String[] smap)
     */
    // START GlassFish Issue 812
    protected void generateClass(String[] smap)
    // END GlassFish Issue 812
        throws FileNotFoundException, JasperException, Exception {

        long t1 = 0;
        if (log.isDebugEnabled()) {
            t1 = System.currentTimeMillis();
        }

        String javaEncoding = ctxt.getOptions().getJavaEncoding();
        String javaFileName = ctxt.getServletJavaFileName();
        String classpath = ctxt.getClassPath(); 

        String sep = System.getProperty("path.separator");

        StringBuffer errorReport = new StringBuffer();

        StringBuffer info=new StringBuffer();
        info.append("Compile: javaFileName=" + javaFileName + "\n" );
        info.append("    classpath=" + classpath + "\n" );

        // Start capturing the System.err output for this thread
        SystemLogHandler.setThread();

        // Initializing javac task
        getProject();
        Javac javac = (Javac) project.createTask("javac");

        // Initializing classpath
        Path path = new Path(project);
        /* PWC 1.2 6311155
        path.setPath(System.getProperty("java.class.path"));
        info.append("    cp=" + System.getProperty("java.class.path") + "\n");
        */
        // START PWC 1.2 6311155
        String sysClassPath = options.getSystemClassPath();
        path.setPath(sysClassPath);
        info.append("    cp=" + sysClassPath + "\n");
        // END PWC 1.2 6311155
        StringTokenizer tokenizer = new StringTokenizer(classpath, sep);
        while (tokenizer.hasMoreElements()) {
            String pathElement = tokenizer.nextToken();
            File repository = new File(pathElement);
            path.setLocation(repository);
            info.append("    cp=" + repository + "\n");
        }

        if( log.isDebugEnabled() )
            /* PWC 1.2 6311155
            log.debug( "Using classpath: " + System.getProperty("java.class.path") + sep
                       + classpath);
            */
            // START PWC 1.2 6311155
            log.debug("Using classpath: " + sysClassPath + sep + classpath);
            //  END PWC 1.2 6311155
        
        // Initializing sourcepath
        Path srcPath = new Path(project);
        srcPath.setLocation(options.getScratchDir());

        info.append("    work dir=" + options.getScratchDir() + "\n");

        // Initialize and set java extensions
        String exts = System.getProperty("java.ext.dirs");
        if (exts != null) {
            Path extdirs = new Path(project);
            extdirs.setPath(exts);
            javac.setExtdirs(extdirs);
            info.append("    extension dir=" + exts + "\n");
        }

        // Configure the compiler object
        javac.setEncoding(javaEncoding);
        javac.setClasspath(path);
        javac.setDebug(ctxt.getOptions().getClassDebugInfo());
        javac.setSrcdir(srcPath);
        javac.setOptimize(! ctxt.getOptions().getClassDebugInfo() );
        javac.setFork(ctxt.getOptions().getFork());
        info.append("    srcDir=" + srcPath + "\n" );

        // Set the Java compiler to use
        if (options.getCompiler() != null) {
            javac.setCompiler(options.getCompiler());
            info.append("    compiler=" + options.getCompiler() + "\n");
        }

        if (options.getCompilerTargetVM() != null) {
            javac.setTarget(options.getCompilerTargetVM());
            info.append("   compilerTargetVM=" + options.getCompilerTargetVM() + "\n");
        }

        if (options.getCompilerSourceVM() != null) {
            javac.setSource(options.getCompilerSourceVM());
            info.append("   compilerSourceVM=" + options.getCompilerSourceVM() + "\n");
        }

        // Build includes path
        PatternSet.NameEntry includes = javac.createInclude();

        includes.setName(ctxt.getJavaPath());
        info.append("    include="+ ctxt.getJavaPath() + "\n" );

        BuildException be = null;

        /* PWC 6441271
        try {
            if (ctxt.getOptions().getFork()) {
                javac.execute();
            } else {
                synchronized(javacLock) {
                    javac.execute();
                }
            }
        } catch (BuildException e) {
            be = e;
            if (!jspcMode) {
                log.error( "Javac exception ", e);
                log.error( "Env: " + info.toString());
            }
        }
        */
        // START PWC 6441271
        String errorCapture = null;
        if (ctxt.getOptions().getFork()) {
            try {
                javac.execute();
            } catch (BuildException e) {
                be = e;
                if (!jspcMode) {
                    log.error( "Javac exception ", e);
                    log.error( "Env: " + info.toString());
                }
            }
            errorReport.append(logger.getReport());
            // Stop capturing the System.err output for this thread
            errorCapture = SystemLogHandler.unsetThread();
        } else {
            errorReport.append(logger.getReport());
            errorCapture = SystemLogHandler.unsetThread();

            // Capture the current thread
            if (errorCapture != null) {
                errorReport.append(lineSeparator);
                errorReport.append(errorCapture);
            }

            JavacObj javacObj = new JavacObj(javac);
            synchronized(javacObj) {
                threadPool.run(javacObj);
                // Wait for the thread to complete
                try {
                    javacObj.wait();
                } catch (InterruptedException e) {
                    ;
                }
            }
            be = javacObj.getException();
            if (be != null) {
                if (!jspcMode) {
                    log.error( "Javac exception ", be);
                    log.error( "Env: " + info.toString());
                }
            }
            errorReport.append(logger.getReport());
            errorCapture = javacObj.getErrorCapture();
        }
        // END PWC 6441271
        /* PWC 6441271
        errorReport.append(logger.getReport());

        // Stop capturing the System.err output for this thread
        String errorCapture = SystemLogHandler.unsetThread();
        */
        if (errorCapture != null) {
            /* PWC 6441271
            errorReport.append(System.getProperty("line.separator"));
            */
            // START PWC 6441271
            errorReport.append(lineSeparator);
            // END PWC 6441271
            errorReport.append(errorCapture);
        }

        if (!ctxt.keepGenerated()) {
            File javaFile = new File(javaFileName);
            javaFile.delete();
        }

        if (be != null) {
            String errorReportString = errorReport.toString();
            if (!jspcMode) {
                log.error("Error compiling file: " + javaFileName + " "
                          + errorReportString);
            }
            JavacErrorDetail[] javacErrors = ErrorDispatcher.parseJavacErrors(
                        errorReportString, javaFileName, pageNodes);
            if (javacErrors != null) {
                errDispatcher.javacError(javacErrors);
            } else {
                errDispatcher.javacError(errorReportString, be);
            }
        }

        if (log.isDebugEnabled()) {
            long t2 = System.currentTimeMillis();
            log.debug("Compiled " + javaFileName + " " + (t2-t1) + "ms");
        }

	if (ctxt.isPrototypeMode()) {
	    return;
	}

        // JSR45 Support
        if (! options.isSmapSuppressed()) {
            SmapUtil.installSmap(smap);
        }

        // START CR 6373479
        if (jsw != null && jsw.getServletClassLastModifiedTime() <= 0) {
            File targetFile = new File(ctxt.getClassFileName());
            jsw.setServletClassLastModifiedTime(targetFile.lastModified());
        }
        // END CR 6373479
    }

    /** 
     * Compile the jsp file from the current engine context
     */
    public void compile()
        throws FileNotFoundException, JasperException, Exception
    {
        compile(true);
    }

    /**
     * Compile the jsp file from the current engine context.  As an side-
     * effect, tag files that are referenced by this page are also compiled.
     *
     * @param compileClass If true, generate both .java and .class file
     *                     If false, generate only .java file
     */
    public void compile(boolean compileClass)
        throws FileNotFoundException, JasperException, Exception
    {
        if (errDispatcher == null) {
            this.errDispatcher = new ErrorDispatcher(jspcMode);
        }

        try {
            String[] smap = generateJava();
            if (compileClass) {
                generateClass(smap);
            }
        } finally {
            if (tfp != null) {
                tfp.removeProtoTypeFiles(null);
            }
            // Make sure these object which are only used during the
            // generation and compilation of the JSP page get
            // dereferenced so that they can be GC'd and reduce the
            // memory footprint.
            tfp = null;
            errDispatcher = null;
            logger = null;
            project = null;
            /* SJSAS 6393940
            pageInfo = null;
            */
            // START SJSAS 6393940
            if (!jspcMode) {
                pageInfo = null;
            }
            // END SJSAS 6393940
            pageNodes = null;
            if (ctxt.getWriter() != null) {
                ctxt.getWriter().close();
                ctxt.setWriter(null);
            }
        }
    }

    /**
     * This is a protected method intended to be overridden by 
     * subclasses of Compiler. This is used by the compile method
     * to do all the compilation. 
     */
    public boolean isOutDated() {
        return isOutDated( true );
    }

    /**
     * Determine if a compilation is necessary by checking the time stamp
     * of the JSP page with that of the corresponding .class or .java file.
     * If the page has dependencies, the check is also extended to its
     * dependeants, and so on.
     * This method can by overidden by a subclasses of Compiler.
     * @param checkClass If true, check against .class file,
     *                   if false, check against .java file.
     */
    public boolean isOutDated(boolean checkClass) {

        String jsp = ctxt.getJspFile();
	
        if (jsw != null
                && (ctxt.getOptions().getModificationTestInterval() > 0)) {
 
            if (jsw.getLastModificationTest()
                    + (ctxt.getOptions().getModificationTestInterval() * 1000) 
                    > System.currentTimeMillis()) {
                return false;
            } else {
                jsw.setLastModificationTest(System.currentTimeMillis());
            }
        }

        long jspRealLastModified = 0;
        try {
            URL jspUrl = ctxt.getResource(jsp);
            if (jspUrl == null) {
                ctxt.incrementRemoved();
                return false;
            }
            URLConnection uc = jspUrl.openConnection();
            jspRealLastModified = uc.getLastModified();
            uc.getInputStream().close();
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

        long targetLastModified = 0;
        File targetFile;
        
        if( checkClass ) {
            targetFile = new File(ctxt.getClassFileName());
        } else {
            targetFile = new File(ctxt.getServletJavaFileName());
        }
        
        if (!targetFile.exists()) {
            return true;
        }

        targetLastModified = targetFile.lastModified();
        if (checkClass && jsw != null) {
            jsw.setServletClassLastModifiedTime(targetLastModified);
        }
        if (targetLastModified < jspRealLastModified) {
            if( log.isDebugEnabled() ) {
                log.debug("Compiler: outdated: " + targetFile + " " +
                    targetLastModified );
            }
            return true;
        }

        // determine if source dependent files (e.g. includes using include
        // directives) have been changed.
        if( jsw==null ) {
            return false;
        }

        List depends = jsw.getDependants();
        if (depends == null) {
            return false;
        }

        Iterator it = depends.iterator();
        while (it.hasNext()) {
            String include = (String)it.next();
            try {
                URL includeUrl = ctxt.getResource(include);
                if (includeUrl == null) {
                    return true;
                }

                URLConnection includeUconn = includeUrl.openConnection();
                long includeLastModified = includeUconn.getLastModified();
                includeUconn.getInputStream().close();

                if (includeLastModified > targetLastModified) {
                    // START GlassFish 750
                    if (include.endsWith(".tld")) {
                        ctxt.clearTaglibs();
                    }
                    // END GlassFish 750
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        }

        return false;

    }

    
    /**
     * Gets the error dispatcher.
     */
    public ErrorDispatcher getErrorDispatcher() {
	return errDispatcher;
    }


    /**
     * Gets the info about the page under compilation
     */
    public PageInfo getPageInfo() {
	return pageInfo;
    }


    /**
     * Sets the info about the page under compilation
     */
    public void setPageInfo(PageInfo pageInfo) {
	this.pageInfo = pageInfo;
    }


    public JspCompilationContext getCompilationContext() {
	return ctxt;
    }


    /**
     * Remove generated files
     */
    public void removeGeneratedFiles() {
        try {
            String classFileName = ctxt.getClassFileName();
            if (classFileName != null) {
                File classFile = new File(classFileName);
                if( log.isDebugEnabled() )
                    log.debug( "Deleting " + classFile );
                classFile.delete();
            }
        } catch (Exception e) {
            // Remove as much as possible, ignore possible exceptions
        }
        try {
            String javaFileName = ctxt.getServletJavaFileName();
            if (javaFileName != null) {
                File javaFile = new File(javaFileName);
                if( log.isDebugEnabled() )
                    log.debug( "Deleting " + javaFile );
                javaFile.delete();
            }
        } catch (Exception e) {
            // Remove as much as possible, ignore possible exceptions
        }
    }

    public void removeGeneratedClassFiles() {
        try {
            String classFileName = ctxt.getClassFileName();
            if (classFileName != null) {
                File classFile = new File(classFileName);
                if( log.isDebugEnabled() )
                    log.debug( "Deleting " + classFile );
                classFile.delete();
            }
        } catch (Exception e) {
            // Remove as much as possible, ignore possible exceptions
        }
    }

    // START PWC 6441271
    public static void startThreadPool() {
        threadPool = new ThreadPool();
        threadPool.setName("javac");
        if (maxThreads <= 0) 
            threadPool.setMaxThreads(1);
        else 
            threadPool.setMaxThreads(maxThreads);
        threadPool.setMaxSpareThreads(minThreads);
        threadPool.setMinSpareThreads(minThreads);
        threadPool.start();
    }

    public static void setMinThreads(int i) {
        minThreads = i;
    }

    public static void setMaxThreads(int i) {
        maxThreads = i;
    }

    // Implement java compilation in a separate java thread to 
    // avoid stack overflow problem (exposed by 64 -bit server)
    protected class JavacObj implements Runnable {
        
        Javac _javac = null;
        BuildException _be = null;
        String _errorCapture = null;

        public JavacObj(Javac javac) {
            _javac = javac;
        }

        public void run() {
            SystemLogHandler.setThread();
            try {
                _javac.execute();
            } catch  (BuildException e) {
                _be = e;
            } finally {
                _errorCapture = SystemLogHandler.unsetThread();
                synchronized(this) {
                    this.notify();
                }
            }
        }

        public BuildException getException() {
            return _be;
        }

        public String getErrorCapture() {
            return _errorCapture;
        }
    }
    // END PWC 6441271


    // START GlassFish Issue 812
    public void setJspcMode(boolean jspcMode) {
        this.jspcMode = jspcMode;
    }
    // END GlassFish Issue 812
}
