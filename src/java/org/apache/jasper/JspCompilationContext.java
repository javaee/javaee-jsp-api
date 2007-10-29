

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

package org.apache.jasper;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
// START GlassFish 750
import java.util.concurrent.ConcurrentHashMap;
// END GlassFish 750
import java.util.Hashtable;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.jsp.tagext.TagInfo;
// START GlassFish 750
import javax.servlet.jsp.tagext.TagLibraryInfo;
// END GlassFish 750

import org.apache.jasper.compiler.Compiler;
import org.apache.jasper.compiler.JspRuntimeContext;
import org.apache.jasper.compiler.JspUtil;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.compiler.ServletWriter;
import org.apache.jasper.servlet.JasperLoader;
import org.apache.jasper.servlet.JspServletWrapper;

/**
 * A place holder for various things that are used through out the JSP
 * engine. This is a per-request/per-context data structure. Some of
 * the instance variables are set at different points.
 *
 * Most of the path-related stuff is here - mangling names, versions, dirs,
 * loading resources and dealing with uris. 
 *
 * @author Anil K. Vijendran
 * @author Harish Prabandham
 * @author Pierre Delisle
 * @author Costin Manolache
 * @author Kin-man Chung
 */
public class JspCompilationContext {

    // START GlassFish Issue 812
    private static Class jdtCompilerClass;
    // END GlassFish Issue 812

    private Hashtable tagFileJarUrls;
    private boolean isPackagedTagFile;

    private String className;
    private String jspUri;
    private boolean isErrPage;
    private String basePackageName;
    private String derivedPackageName;
    private String servletJavaFileName;
    private String javaPath;
    private String classFileName;
    private String contentType;
    private ServletWriter writer;
    private Options options;
    private JspServletWrapper jsw;
    private Compiler jspCompiler;
    private String classPath;

    private String baseURI;
    private String baseOutputDir;
    private String outputDir;
    private ServletContext context;
    private URLClassLoader loader;

    private JspRuntimeContext rctxt;

    private int removed = 0;

    private URLClassLoader jspLoader;
    private URL baseUrl;
    private Class servletClass;

    private boolean isTagFile;
    private boolean protoTypeMode;
    private TagInfo tagInfo;
    private URL tagFileJarUrl;

    // START GlassFish 750
    private ConcurrentHashMap<String, TagLibraryInfo> taglibs;
    // END GlassFish 750

    // START GlassFish Issue 812
    static {
        try {
            jdtCompilerClass = Class.forName(
                "org.apache.jasper.compiler.JDTCompiler");
        } catch (ClassNotFoundException e) {
            // do nothing
        }
    }
    // END GlassFish Issue 812

    // jspURI _must_ be relative to the context
    public JspCompilationContext(String jspUri,
                                 boolean isErrPage,
                                 Options options,
                                 ServletContext context,
                                 JspServletWrapper jsw,
                                 JspRuntimeContext rctxt)
            throws JasperException {

        this.jspUri = canonicalURI(jspUri);
        this.isErrPage = isErrPage;
        this.options = options;
        this.jsw = jsw;
        this.context = context;

        this.baseURI = jspUri.substring(0, jspUri.lastIndexOf('/') + 1);
        // hack fix for resolveRelativeURI
        if (baseURI == null) {
            baseURI = "/";
        } else if (baseURI.charAt(0) != '/') {
            // strip the basde slash since it will be combined with the
            // uriBase to generate a file
            baseURI = "/" + baseURI;
        }
        if (baseURI.charAt(baseURI.length() - 1) != '/') {
            baseURI += '/';
        }

        this.rctxt = rctxt;
        this.tagFileJarUrls = new Hashtable();
        this.basePackageName = Constants.JSP_PACKAGE_NAME;

        // START GlassFish 750
        taglibs = (ConcurrentHashMap<String, TagLibraryInfo>)
            context.getAttribute(Constants.JSP_TAGLIBRARY_CACHE);
        // END GlassFish 750
    }

    public JspCompilationContext(String tagfile,
                                 TagInfo tagInfo, 
                                 Options options,
                                 ServletContext context,
                                 JspServletWrapper jsw,
                                 JspRuntimeContext rctxt,
                                 URL tagFileJarUrl)
            throws JasperException {
        this(tagfile, false, options, context, jsw, rctxt);
        this.isTagFile = true;
        this.tagInfo = tagInfo;
        this.tagFileJarUrl = tagFileJarUrl;
        if (tagFileJarUrl != null) {
            isPackagedTagFile = true;
        }
    }

    /* ==================== Methods to override ==================== */


    // START GlassFish 750
    /**
     * Adds the given tag library with the given URI to the context-wide
     * tag library cache.
     *
     * @param uri The tag library URI
     * @param taglib The tag library to add
     */
    public void addTaglib(String uri, TagLibraryInfo taglib) {
        taglibs.put(uri, taglib);
    }

    /**
     * Gets the context-wide tag library cache.
     *
     * @return The context-wide tag library cache
     */
    public ConcurrentHashMap<String, TagLibraryInfo> getTaglibs() {
        return taglibs;
    }

    /**
     * Clears the context-wide tag library cache.
     */
    public void clearTaglibs() {
        taglibs.clear();
    }
    // END GlassFish 750


    /** ---------- Class path and loader ---------- */

    /**
     * The classpath that is passed off to the Java compiler. 
     */
    public String getClassPath() {
        if( classPath != null )
            return classPath;
        return rctxt.getClassPath();
    }

    /**
     * The classpath that is passed off to the Java compiler. 
     */
    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    /**
     * What class loader to use for loading classes while compiling
     * this JSP?
     */
    public ClassLoader getClassLoader() {
        if( loader != null )
            return loader;
        return rctxt.getParentClassLoader();
    }

    public void setClassLoader(URLClassLoader loader) {
        this.loader = loader;
    }

    /** ---------- Input/Output  ---------- */
    
    /**
     * The output directory to generate code into.  The output directory
     * is make up of the scratch directory, which is provide in Options,
     * plus the directory derived from the package name.
     */
    public String getOutputDir() {
	if (outputDir == null) {
	    createOutputDir();
	}

        return outputDir;
    }

    /**
     * Create a "Compiler" object based on some init param data. This
     * is not done yet. Right now we're just hardcoding the actual
     * compilers that are created. 
     */
    public Compiler createCompiler(boolean jspcMode) throws JasperException {
        if (jspCompiler != null ) {
            return jspCompiler;
        }

        /* GlassFish Issue 812
        jspCompiler = new Compiler(this, jsw);
        */
        // START GlassFish Issue 812
        if (jdtCompilerClass != null) {
            try {
                jspCompiler = (Compiler) jdtCompilerClass.newInstance();
            } catch (Exception e) {
                // Fallback
                jspCompiler = new Compiler();
                jspCompiler.setJspcMode(jspcMode);
            }
        } else {
            jspCompiler = new Compiler();
            jspCompiler.setJspcMode(jspcMode);
        }
        jspCompiler.init(this, jsw);
        // END GlassFish Issue 812

        return jspCompiler;
    }

    public Compiler getCompiler() {
        return jspCompiler;
    }

    /** ---------- Access resources in the webapp ---------- */

    /** 
     * Get the full value of a URI relative to this compilations context
     * uses current file as the base.
     */
    public String resolveRelativeUri(String uri) {
        // sometimes we get uri's massaged from File(String), so check for
        // a root directory deperator char
        if (uri.startsWith("/") || uri.startsWith(File.separator)) {
            return uri;
        } else {
            return baseURI + uri;
        }
    }

    /**
     * Gets a resource as a stream, relative to the meanings of this
     * context's implementation.
     * @return a null if the resource cannot be found or represented 
     *         as an InputStream.
     */
    public java.io.InputStream getResourceAsStream(String res)
           throws JasperException {
        return context.getResourceAsStream(canonicalURI(res));
    }


    public URL getResource(String res) throws MalformedURLException {
        try {
            return context.getResource(canonicalURI(res));
        } catch (JasperException ex) {
            throw new MalformedURLException(ex.getMessage());
        }
    }

    public Set getResourcePaths(String path)
           throws JasperException {
        return context.getResourcePaths(canonicalURI(path));
    }

    /** 
     * Gets the actual path of a URI relative to the context of
     * the compilation.
     */
    public String getRealPath(String path) {
        if (context != null) {
            return context.getRealPath(path);
        }
        return path;
    }

    /**
     * Returns the tag-file-name-to-JAR-file map of this compilation unit,
     * which maps tag file names to the JAR files in which the tag files are
     * packaged.
     *
     * The map is populated when parsing the tag-file elements of the TLDs
     * of any imported taglibs. 
     */
    public Hashtable getTagFileJarUrls() {
        return this.tagFileJarUrls;
    }

    /**
     * Returns the JAR file in which the tag file for which this
     * JspCompilationContext was created is packaged, or null if this
     * JspCompilationContext does not correspond to a tag file, or if the
     * corresponding tag file is not packaged in a JAR.
     */
    public URL getTagFileJarUrl() {
        return this.tagFileJarUrl;
    }

    /* ==================== Common implementation ==================== */

    /**
     * Just the class name (does not include package name) of the
     * generated class. 
     */
    public String getServletClassName() {

        if (className != null) {
            return className;
        }

        if (isTagFile) {
            className = tagInfo.getTagClassName();
            int lastIndex = className.lastIndexOf('.');
            if (lastIndex != -1) {
                className = className.substring(lastIndex + 1);
            }
        } else {
            int iSep = jspUri.lastIndexOf('/') + 1;
            className = JspUtil.makeJavaIdentifier(jspUri.substring(iSep));
        }
        return className;
    }

    public void setServletClassName(String className) {
        this.className = className;
    }
    
    /**
     * Path of the JSP URI. Note that this is not a file name. This is
     * the context rooted URI of the JSP file. 
     */
    public String getJspFile() {
        return jspUri;
    }

    /**
     * Are we processing something that has been declared as an
     * errorpage? 
     */
    public boolean isErrorPage() {
        return isErrPage;
    }

    public void setErrorPage(boolean isErrPage) {
        this.isErrPage = isErrPage;
    }

    public boolean isTagFile() {
        return isTagFile;
    }

    public TagInfo getTagInfo() {
        return tagInfo;
    }

    public void setTagInfo(TagInfo tagi) {
        tagInfo = tagi;
    }

    /**
     * True if we are compiling a tag file in prototype mode.
     * ie we only generate codes with class for the tag handler with empty
     * method bodies.
     */
    public boolean isPrototypeMode() {
        return protoTypeMode;
    }

    public void setPrototypeMode(boolean pm) {
        protoTypeMode = pm;
    }

    /**
     * Package name for the generated class is make up of the base package
     * name, which is user settable, and the derived package name.  The
     * derived package name directly mirrors the file heirachy of the JSP page.
     */
    public String getServletPackageName() {
        String dPackageName = getDerivedPackageName();
	if (dPackageName.length() == 0) {
            return basePackageName;
        }
        return basePackageName + '.' + getDerivedPackageName();
    }

    private String getDerivedPackageName() {
	if (derivedPackageName == null) {
            int iSep = jspUri.lastIndexOf('/');
            derivedPackageName = (iSep > 0) ?
                JspUtil.makeJavaPackage(jspUri.substring(1,iSep)) : "";
         }
        return derivedPackageName;
    }
	    
    /**
     * The package name into which the servlet class is generated.
     */
    public void setServletPackageName(String servletPackageName) {
        this.basePackageName = servletPackageName;
    }

    /**
     * Full path name of the Java file into which the servlet is being
     * generated. 
     */
    public String getServletJavaFileName() {

        if (servletJavaFileName == null) {
            servletJavaFileName =
		getOutputDir() + getServletClassName() + ".java";
        } else {
            // Make sure output dir exists
            makeOutputDir();
        }
        return servletJavaFileName;
    }

    public void setServletJavaFileName(String servletJavaFileName) {
        this.servletJavaFileName = servletJavaFileName;
    }

    /**
     * Get hold of the Options object for this context. 
     */
    public Options getOptions() {
        return options;
    }

    public ServletContext getServletContext() {
        return context;
    }

    public JspRuntimeContext getRuntimeContext() {
        return rctxt;
    }

    /**
     * Path of the Java file relative to the work directory.
     */
    public String getJavaPath() {

        if (javaPath != null) {
            return javaPath;
        }

        if (isTagFile()) {
	    String tagName = tagInfo.getTagClassName();
            javaPath = tagName.replace('.', '/') + ".java";
        } else {
            javaPath = getServletPackageName().replace('.', '/') + '/' +
                       getServletClassName() + ".java";
	}
        return javaPath;
    }

    public String getClassFileName() {

        if (classFileName == null) {
            classFileName = getOutputDir() + getServletClassName() + ".class";
        } else {
            // Make sure output dir exists
            makeOutputDir();
        }
        return classFileName;
    }

    /**
     * Get the content type of this JSP.
     *
     * Content type includes content type and encoding.
     */
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Where is the servlet being generated?
     */
    public ServletWriter getWriter() {
        return writer;
    }

    public void setWriter(ServletWriter writer) {
        this.writer = writer;
    }

    /**
     * Gets the 'location' of the TLD associated with the given taglib 'uri'.
     * 
     * @return An array of two Strings: The first element denotes the real
     * path to the TLD. If the path to the TLD points to a jar file, then the
     * second element denotes the name of the TLD entry in the jar file.
     * Returns null if the given uri is not associated with any tag library
     * 'exposed' in the web application.
     */
    public String[] getTldLocation(String uri) throws JasperException {
        String[] location = 
            getOptions().getTldLocationsCache().getLocation(uri);
        return location;
    }

    /**
     * Are we keeping generated code around?
     */
    public boolean keepGenerated() {
        return getOptions().getKeepGenerated();
    }

    // ==================== Removal ==================== 

    public void incrementRemoved() {
        if (removed > 1) {
            jspCompiler.removeGeneratedFiles();
            if( rctxt != null )
                rctxt.removeWrapper(jspUri);
        }
        removed++;
    }

    public boolean isRemoved() {
        if (removed > 1 ) {
            return true;
        }
        return false;
    }

    // ==================== Compile and reload ====================
    
    public void compile() throws JasperException, FileNotFoundException {
        createCompiler(false);
        if (isPackagedTagFile || jspCompiler.isOutDated()) {
            try {
                jspCompiler.compile();
                jsw.setReload(true);
                jsw.setCompilationException(null);
            } catch (JasperException ex) {
                // Cache compilation exception
                jsw.setCompilationException(ex);
                throw ex;
            } catch (Exception ex) {
                ex.printStackTrace();
                JasperException je = new JasperException(
                            Localizer.getMessage("jsp.error.unable.compile"),
                            ex);
                // Cache compilation exception
                jsw.setCompilationException(je);
                throw je;
            }
        }
    }

    // ==================== Manipulating the class ====================

    public Class load() 
        throws JasperException
    {
        try {
            /* GlassFish Issue 812
            // START S1AS 6181923
            if (!options.getUsePrecompiled()) {
            // END S1AS 6181923
                jspLoader = new JasperLoader(new URL[] {baseUrl},
                                             getClassLoader(),
                                             rctxt.getPermissionCollection(),
                                             rctxt.getCodeSource());
            // START S1AS 6181923
            }
            // END S1AS 6181923
            */
            // START GlassFish Issue 812
            getJspLoader();
            // END GlassFish Issue 812

            String name;
            if (isTagFile()) {
                name = tagInfo.getTagClassName();
            } else {
                name = getServletPackageName() + "." + getServletClassName();
            }

            // START S1AS 6181923
            if (options.getUsePrecompiled()) {
                servletClass = getClassLoader().loadClass(name);
            } else {
            // END S1AS 6181923
                servletClass = jspLoader.loadClass(name);
            // START S1AS 6181923
            }
            // END S1AS 6181923
        } catch (ClassNotFoundException cex) {
            throw new JasperException(Localizer.getMessage("jsp.error.unable.load"),
                                      cex);
        } catch (Exception ex) {
            throw new JasperException(Localizer.getMessage("jsp.error.unable.compile"),
                                      ex);
        }
        removed = 0;
        return servletClass;
    }


    // START GlassFish Issue 812
    public ClassLoader getJspLoader() {
        // START S1AS 6181923
        if (!options.getUsePrecompiled()) {
        // END S1AS 6181923
            jspLoader = new JasperLoader(new URL[] {baseUrl},
                                         getClassLoader(),
                                         rctxt.getPermissionCollection(),
                                         rctxt.getCodeSource());
        // START S1AS 6181923
        }
        // END S1AS 6181923

        return jspLoader;
    }
    // END GlassFish Issue 812

    // ==================== Private methods ==================== 

    static Object outputDirLock = new Object();

    private void makeOutputDir() {
        synchronized(outputDirLock) {
            File outDirFile = new File(outputDir);
            outDirFile.mkdirs();
        }
    }

    private void createOutputDir() {
        String path = null;
        if (isTagFile()) {
	    String tagName = tagInfo.getTagClassName();
            path = tagName.replace('.', '/');
	    path = path.substring(0, path.lastIndexOf('/'));
        } else {
            path = getServletPackageName().replace('.', '/');
	}

        try {
            // Append servlet or tag handler path to scratch dir
            baseUrl = options.getScratchDir().toURL();
            String outUrlString = baseUrl.toString() + '/' + path;
            URL outUrl = new URL(outUrlString);
            outputDir = outUrl.getFile() + File.separator;
            makeOutputDir();
        } catch (Exception e) {
            throw new IllegalStateException("No output directory: " +
                                            e.getMessage());
        }
    }
    
    private static final boolean isPathSeparator(char c) {
       return (c == '/' || c == '\\');
    }

    private static final String canonicalURI(String s) 
           throws JasperException {
       if (s == null) return null;
       StringBuffer result = new StringBuffer();
       final int len = s.length();
       int pos = 0;
       while (pos < len) {
           char c = s.charAt(pos);
           if ( isPathSeparator(c) ) {
               /*
                * multiple path separators.
                * 'foo///bar' -> 'foo/bar'
                */
               while (pos+1 < len && isPathSeparator(s.charAt(pos+1))) {
                   ++pos;
               }

               if (pos+1 < len && s.charAt(pos+1) == '.') {
                   /*
                    * a single dot at the end of the path - we are done.
                    */
                   if (pos+2 >= len) break;

                   switch (s.charAt(pos+2)) {
                       /*
                        * self directory in path
                        * foo/./bar -> foo/bar
                        */
                   case '/':
                   case '\\':
                       pos += 2;
                       continue;

                       /*
                        * two dots in a path: go back one hierarchy.
                        * foo/bar/../baz -> foo/baz
                        */
                   case '.':
                       // only if we have exactly _two_ dots.
                       if (pos+3 < len && isPathSeparator(s.charAt(pos+3))) {
                           pos += 3;
                           int separatorPos = result.length()-1;
                           if (separatorPos < 0) {
                               throw new JasperException(
                                   Localizer.getMessage("jsp.error.badpath",s));
                           }
                           while (separatorPos >= 0 && 
                                  ! isPathSeparator(result
                                                    .charAt(separatorPos))) {
                               --separatorPos;
                           }
                           if (separatorPos >= 0)
                               result.setLength(separatorPos);
                           continue;
                       }
                   }
               }
           }
           result.append(c);
           ++pos;
       }
       return result.toString();
    }
}

