

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

package org.apache.jasper.xmlparser;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jasper.Constants;
import org.apache.jasper.JasperException;
import org.apache.jasper.compiler.Localizer;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * XML parsing utilities for processing web application deployment
 * descriptor and tag library descriptor files.  FIXME - make these
 * use a separate class loader for the parser to be used.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.3 $ $Date: 2006/02/17 00:39:24 $
 */

public class ParserUtils {

    // Logger
    static Log log = LogFactory.getLog(ParserUtils.class);

    /**
     * An error handler for use when parsing XML documents.
     */
    static ErrorHandler errorHandler = new MyErrorHandler();

    /**
     * An entity resolver for use when parsing XML documents.
     */
    static EntityResolver entityResolver = new MyEntityResolver();

    // Turn off for JSP 2.0 until switch over to using xschema.
    public static boolean validating = false;

    // START PWC 6386258
    static final String[] CACHED_DTD_RESOURCE_PATHS = {
        Constants.TAGLIB_DTD_RESOURCE_PATH_11,
        Constants.TAGLIB_DTD_RESOURCE_PATH_12,
        Constants.WEBAPP_DTD_RESOURCE_PATH_22,
        Constants.WEBAPP_DTD_RESOURCE_PATH_23,
    };

    static final String[] CACHED_SCHEMA_RESOURCE_PATHS = {
        Constants.TAGLIB_SCHEMA_RESOURCE_PATH_20,
        Constants.TAGLIB_SCHEMA_RESOURCE_PATH_21,
        Constants.WEBAPP_SCHEMA_RESOURCE_PATH_24,
        Constants.WEBAPP_SCHEMA_RESOURCE_PATH_25,
    };
    // END PWC 6386258


    // --------------------------------------------------------- Public Methods


    // START PWC 6386258
    /**
     * Sets the path prefix for .xsd resources
     */
    public static void setSchemaResourcePrefix(String prefix) {
        for (int i=0; i<CACHED_SCHEMA_RESOURCE_PATHS.length; i++) {
            String path = CACHED_SCHEMA_RESOURCE_PATHS[i];
            int index = path.lastIndexOf('/');
            if (index != -1) {
                CACHED_SCHEMA_RESOURCE_PATHS[i] =
                    prefix + path.substring(index+1);
            }
        }
    }

    /**
     * Sets the path prefix for .dtd resources
     */
    public static void setDtdResourcePrefix(String prefix) {
        for (int i=0; i<CACHED_DTD_RESOURCE_PATHS.length; i++) {
            String path = CACHED_DTD_RESOURCE_PATHS[i];
            int index = path.lastIndexOf('/');
            if (index != -1) {
                CACHED_DTD_RESOURCE_PATHS[i] =
                    prefix + path.substring(index+1);
            }
        }
    }
    // END PWC 6386258


    /**
     * Parse the specified XML document, and return a <code>TreeNode</code>
     * that corresponds to the root node of the document tree.
     *
     * @param uri URI of the XML document being parsed
     * @param is Input source containing the deployment descriptor
     *
     * @exception JasperException if an input/output error occurs
     * @exception JasperException if a parsing error occurs
     */
    public TreeNode parseXMLDocument(String uri, InputSource is)
        throws JasperException {

        Document document = null;

        // Perform an XML parse of this document, via JAXP
        try {
            DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(validating);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(entityResolver);
            builder.setErrorHandler(errorHandler);
            document = builder.parse(is);
	} catch (ParserConfigurationException ex) {
            throw new JasperException
                (Localizer.getMessage("jsp.error.parse.xml", uri), ex);
	} catch (SAXParseException ex) {
            throw new JasperException
                (Localizer.getMessage("jsp.error.parse.xml.line",
				      uri,
				      Integer.toString(ex.getLineNumber()),
				      Integer.toString(ex.getColumnNumber())),
		 ex);
	} catch (SAXException sx) {
            throw new JasperException
                (Localizer.getMessage("jsp.error.parse.xml", uri), sx);
        } catch (IOException io) {
            throw new JasperException
                (Localizer.getMessage("jsp.error.parse.xml", uri), io);
	}

        // Convert the resulting document to a graph of TreeNodes
        return (convert(null, document.getDocumentElement()));
    }


    /**
     * Parse the specified XML document, and return a <code>TreeNode</code>
     * that corresponds to the root node of the document tree.
     *
     * @param uri URI of the XML document being parsed
     * @param is Input stream containing the deployment descriptor
     *
     * @exception JasperException if an input/output error occurs
     * @exception JasperException if a parsing error occurs
     */
    public TreeNode parseXMLDocument(String uri, InputStream is)
            throws JasperException {

        return (parseXMLDocument(uri, new InputSource(is)));
    }


    // ------------------------------------------------------ Protected Methods


    /**
     * Create and return a TreeNode that corresponds to the specified Node,
     * including processing all of the attributes and children nodes.
     *
     * @param parent The parent TreeNode (if any) for the new TreeNode
     * @param node The XML document Node to be converted
     */
    protected TreeNode convert(TreeNode parent, Node node) {

        // Construct a new TreeNode for this node
        TreeNode treeNode = new TreeNode(node.getNodeName(), parent);

        // Convert all attributes of this node
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            int n = attributes.getLength();
            for (int i = 0; i < n; i++) {
                Node attribute = attributes.item(i);
                treeNode.addAttribute(attribute.getNodeName(),
                                      attribute.getNodeValue());
            }
        }

        // Create and attach all children of this node
        NodeList children = node.getChildNodes();
        if (children != null) {
            int n = children.getLength();
            for (int i = 0; i < n; i++) {
                Node child = children.item(i);
                if (child instanceof Comment)
                    continue;
                if (child instanceof Text) {
                    String body = ((Text) child).getData();
                    if (body != null) {
                        body = body.trim();
                        if (body.length() > 0)
                            treeNode.setBody(body);
                    }
                } else {
                    TreeNode treeChild = convert(treeNode, child);
                }
            }
        }
        
        // Return the completed TreeNode graph
        return (treeNode);
    }
}


// ------------------------------------------------------------ Private Classes

class MyEntityResolver implements EntityResolver {
    
    public InputSource resolveEntity(String publicId, String systemId)
	throws SAXException
    {
	for (int i=0; i<Constants.CACHED_DTD_PUBLIC_IDS.length; i++) {
	    String cachedDtdPublicId = Constants.CACHED_DTD_PUBLIC_IDS[i];
	    if (cachedDtdPublicId.equals(publicId)) {
                /* PWC 6386258
		String resourcePath = Constants.CACHED_DTD_RESOURCE_PATHS[i];
                */
                // START PWC 6386258
                String resourcePath = ParserUtils.CACHED_DTD_RESOURCE_PATHS[i];
                // END PWC 6386258
		InputStream input =
		    this.getClass().getResourceAsStream(resourcePath);
		if (input == null) {
		    throw new SAXException(
                        Localizer.getMessage("jsp.error.internal.filenotfound",
					     resourcePath));
		}
		InputSource isrc = new InputSource(input);
		return isrc;
	    }
	}
        if (ParserUtils.log.isDebugEnabled())
            ParserUtils.log.debug("Resolve entity failed"  + publicId + " "
                                  + systemId );
	ParserUtils.log.error(
            Localizer.getMessage("jsp.error.parse.xml.invalidPublicId",
            publicId));
        return null;
    }
}

class MyErrorHandler implements ErrorHandler {
    public void warning(SAXParseException ex)
	throws SAXException
    {
        if (ParserUtils.log.isDebugEnabled())
            ParserUtils.log.debug("ParserUtils: warning ", ex );
	// We ignore warnings
    }

    public void error(SAXParseException ex)
	throws SAXException
    {
	throw ex;
    }

    public void fatalError(SAXParseException ex)
	throws SAXException
    {
	throw ex;
    }
}
