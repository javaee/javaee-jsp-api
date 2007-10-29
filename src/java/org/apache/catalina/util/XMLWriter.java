

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

package org.apache.catalina.util;

import java.io.IOException;
import java.io.Writer;

/**
 * XMLWriter helper class.
 *
 * @author <a href="mailto:remm@apache.org">Remy Maucherat</a>
 */
public class XMLWriter {


    // -------------------------------------------------------------- Constants


    /**
     * Opening tag.
     */
    public static final int OPENING = 0;


    /**
     * Closing tag.
     */
    public static final int CLOSING = 1;


    /**
     * Element with no content.
     */
    public static final int NO_CONTENT = 2;


    // ----------------------------------------------------- Instance Variables


    /**
     * Buffer.
     */
    protected StringBuffer buffer = new StringBuffer();


    /**
     * Writer.
     */
    protected Writer writer = null;


    // ----------------------------------------------------------- Constructors


    /**
     * Constructor.
     */
    public XMLWriter() {
    }


    /**
     * Constructor.
     */
    public XMLWriter(Writer writer) {
        this.writer = writer;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Retrieve generated XML.
     *
     * @return String containing the generated XML
     */
    public String toString() {
        return buffer.toString();
    }


    /**
     * Write property to the XML.
     *
     * @param namespace Namespace
     * @param namespaceInfo Namespace info
     * @param name Property name
     * @param value Property value
     */
    public void writeProperty(String namespace, String namespaceInfo,
                              String name, String value) {
        writeElement(namespace, namespaceInfo, name, OPENING);
        buffer.append(value);
        writeElement(namespace, namespaceInfo, name, CLOSING);

    }


    /**
     * Write property to the XML.
     *
     * @param namespace Namespace
     * @param name Property name
     * @param value Property value
     */
    public void writeProperty(String namespace, String name, String value) {
        writeElement(namespace, name, OPENING);
        buffer.append(value);
        writeElement(namespace, name, CLOSING);
    }


    /**
     * Write property to the XML.
     *
     * @param namespace Namespace
     * @param name Property name
     */
    public void writeProperty(String namespace, String name) {
        writeElement(namespace, name, NO_CONTENT);
    }


    /**
     * Write an element.
     *
     * @param name Element name
     * @param namespace Namespace abbreviation
     * @param type Element type
     */
    public void writeElement(String namespace, String name, int type) {
        writeElement(namespace, null, name, type);
    }


    /**
     * Write an element.
     *
     * @param namespace Namespace abbreviation
     * @param namespaceInfo Namespace info
     * @param name Element name
     * @param type Element type
     */
    public void writeElement(String namespace, String namespaceInfo,
                             String name, int type) {
        if ((namespace != null) && (namespace.length() > 0)) {
            switch (type) {
            case OPENING:
                if (namespaceInfo != null) {
                    buffer.append("<" + namespace + ":" + name + " xmlns:"
                                  + namespace + "=\""
                                  + namespaceInfo + "\">");
                } else {
                    buffer.append("<" + namespace + ":" + name + ">");
                }
                break;
            case CLOSING:
                buffer.append("</" + namespace + ":" + name + ">\n");
                break;
            case NO_CONTENT:
            default:
                if (namespaceInfo != null) {
                    buffer.append("<" + namespace + ":" + name + " xmlns:"
                                  + namespace + "=\""
                                  + namespaceInfo + "\"/>");
                } else {
                    buffer.append("<" + namespace + ":" + name + "/>");
                }
                break;
            }
        } else {
            switch (type) {
            case OPENING:
                buffer.append("<" + name + ">");
                break;
            case CLOSING:
                buffer.append("</" + name + ">\n");
                break;
            case NO_CONTENT:
            default:
                buffer.append("<" + name + "/>");
                break;
            }
        }
    }


    /**
     * Write text.
     *
     * @param text Text to append
     */
    public void writeText(String text) {
        buffer.append(text);
    }


    /**
     * Write data.
     *
     * @param data Data to append
     */
    public void writeData(String data) {
        buffer.append("<![CDATA[" + data + "]]>");
    }


    /**
     * Write XML Header.
     */
    public void writeXMLHeader() {
        buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n");
    }


    /**
     * Send data and reinitializes buffer.
     */
    public void sendData()
        throws IOException {
        if (writer != null) {
            writer.write(buffer.toString());
            buffer = new StringBuffer();
        }
    }


}
