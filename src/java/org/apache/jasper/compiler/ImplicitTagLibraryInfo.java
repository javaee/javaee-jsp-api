

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

import java.util.*;
import javax.servlet.jsp.tagext.FunctionInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagFileInfo;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.JasperException;

/**
 * Class responsible for generating an implicit tag library containing tag
 * handlers corresponding to the tag files in "/WEB-INF/tags/" or a 
 * subdirectory of it.
 *
 * @author Jan Luehe
 */
class ImplicitTagLibraryInfo extends TagLibraryInfo {

    private static final String WEB_INF_TAGS = "/WEB-INF/tags";
    private static final String TAG_FILE_SUFFIX = ".tag";
    private static final String TAGX_FILE_SUFFIX = ".tagx";
    private static final String TAGS_SHORTNAME = "tags";
    private static final String TLIB_VERSION = "1.0";
    private static final String JSP_VERSION = "2.0";

    // Maps tag names to tag file paths
    private Hashtable tagFileMap;

    private ParserController pc;
    private PageInfo pageInfo;
    private Vector vec;

    /**
     * Constructor.
     */
    public ImplicitTagLibraryInfo(JspCompilationContext ctxt,
				  ParserController pc,
				  String prefix,
				  String tagdir,
				  ErrorDispatcher err) throws JasperException {
        super(prefix, null);
	this.pc = pc;
        this.pageInfo = pc.getCompiler().getPageInfo();
	this.tagFileMap = new Hashtable();
	this.vec = new Vector();

        // Implicit tag libraries have no functions:
        this.functions = new FunctionInfo[0];

	tlibversion = TLIB_VERSION;
	jspversion = JSP_VERSION;

	if (!tagdir.startsWith(WEB_INF_TAGS)) {
	    err.jspError("jsp.error.invalid.tagdir", tagdir);
	}
	
	// Determine the value of the <short-name> subelement of the
	// "imaginary" <taglib> element
	if (tagdir.equals(WEB_INF_TAGS)
	        || tagdir.equals( WEB_INF_TAGS + "/")) {
	    shortname = TAGS_SHORTNAME;
	} else {
	    shortname = tagdir.substring(WEB_INF_TAGS.length());
	    shortname = shortname.replace('/', '-');
	}

	// Populate mapping of tag names to tag file paths
	Set dirList = ctxt.getResourcePaths(tagdir);
	if (dirList != null) {
	    Iterator it = dirList.iterator();
	    while (it.hasNext()) {
		String path = (String) it.next();
		if (path.endsWith(TAG_FILE_SUFFIX)
		        || path.endsWith(TAGX_FILE_SUFFIX)) {
		    /*
		     * Use the filename of the tag file, without the .tag or
		     * .tagx extension, respectively, as the <name> subelement
		     * of the "imaginary" <tag-file> element
		     */
		    String suffix = path.endsWith(TAG_FILE_SUFFIX) ?
			TAG_FILE_SUFFIX : TAGX_FILE_SUFFIX; 
		    String tagName = path.substring(path.lastIndexOf("/") + 1);
		    tagName = tagName.substring(0,
						tagName.lastIndexOf(suffix));
		    tagFileMap.put(tagName, path);
		}
	    }
	}
    }


    /**
     * Returns an array of TagLibraryInfo objects representing the entire set
     * of tag libraries (including this TagLibraryInfo) imported by taglib
     * directives in the translation unit that references this
     * TagLibraryInfo.
     *
     * If a tag library is imported more than once and bound to different
     * prefices, only the TagLibraryInfo bound to the first prefix must be
     * included in the returned array.
     *
     * @return Array of TagLibraryInfo objects representing the entire set
     * of tag libraries (including this TagLibraryInfo) imported by taglib
     * directives in the translation unit that references this TagLibraryInfo.
     *
     * @since 2.1
     */
    public TagLibraryInfo[] getTagLibraryInfos() {

        TagLibraryInfo[] taglibs = null;

        Collection c = pageInfo.getTaglibs();
        if (c != null) {
            Object[] objs = c.toArray();
            if (objs != null && objs.length > 0) {
                taglibs = new TagLibraryInfo[objs.length];
                for (int i=0; i<objs.length; i++) {
                    taglibs[i] = (TagLibraryInfo) objs[i];
                }
            }
        }

        return taglibs;
    }


    /**
     * Checks to see if the given tag name maps to a tag file path,
     * and if so, parses the corresponding tag file.
     *
     * @return The TagFileInfo corresponding to the given tag name, or null if
     * the given tag name is not implemented as a tag file
     */
    public TagFileInfo getTagFile(String shortName) {

	TagFileInfo tagFile = super.getTagFile(shortName);
	if (tagFile == null) {
	    String path = (String) tagFileMap.get(shortName);
	    if (path == null) {
		return null;
	    }

	    TagInfo tagInfo = null;
	    try {
		tagInfo = TagFileProcessor.parseTagFileDirectives(pc,
								  shortName,
								  path,
								  this);
	    } catch (JasperException je) {
		throw new RuntimeException(je.toString());
	    }

	    tagFile = new TagFileInfo(shortName, path, tagInfo);
	    vec.addElement(tagFile);

	    this.tagFiles = new TagFileInfo[vec.size()];
	    vec.copyInto(this.tagFiles);
	}

	return tagFile;
    }
}
