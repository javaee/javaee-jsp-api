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


package org.apache.catalina.util;


import org.apache.commons.digester.Digester;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.SAXException;

import org.apache.tomcat.util.IntrospectionUtils;

/**
 * This extended digester filters out ${...} tokens to replace them with
 * matching system properties.
 * 
 * @author Simon Kitching
 * @author Remy Maucherat
 */
public class CatalinaDigester extends Digester {


    // ---------------------------------------------------------- Static Fields


    private static class SystemPropertySource 
        implements IntrospectionUtils.PropertySource {
        public String getProperty( String key ) {
            return System.getProperty(key);
        }
    }

    protected static IntrospectionUtils.PropertySource source[] = 
        new IntrospectionUtils.PropertySource[] { new SystemPropertySource() };


    // ---------------------------------------------------------------- Methods


    /**
     * Invoke inherited implementation after applying variable
     * substitution to any attribute values containing variable
     * references. 
     */
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes list)
        throws SAXException {
        list = updateAttributes(list);
        super.startElement(namespaceURI, localName, qName, list);
    }


    /**
     * Invoke inherited implementation after applying variable substitution
     * to the character data contained in the current element.
     */
    public void endElement(String namespaceURI, String localName, String qName)
        throws SAXException  {
        bodyText = updateBodyText(bodyText);
        super.endElement(namespaceURI, localName, qName);
    }


    /**
     * Returns an attributes list which contains all the attributes
     * passed in, with any text of form "${xxx}" in an attribute value
     * replaced by the appropriate value from the system property.
     */
    private Attributes updateAttributes(Attributes list) {

        if (list.getLength() == 0) {
            return list;
        }
        
        AttributesImpl newAttrs = new AttributesImpl(list);
        int nAttributes = newAttrs.getLength();
        for (int i = 0; i < nAttributes; ++i) {
            String value = newAttrs.getValue(i);
            try {
                String newValue = 
                    IntrospectionUtils.replaceProperties(value, null, source);
                if (value != newValue) {
                    newAttrs.setValue(i, newValue);
                }
            }
            catch (Exception e) {
                // ignore - let the attribute have its original value
            }
        }

        return newAttrs;

    }


    /**
     * Return a new StringBuffer containing the same contents as the
     * input buffer, except that data of form ${varname} have been
     * replaced by the value of that var as defined in the system property.
     */
    private StringBuffer updateBodyText(StringBuffer bodyText) {
        String in = bodyText.toString();
        String out;
        try {
            out = IntrospectionUtils.replaceProperties(in, null, source);
        } catch(Exception e) {
            return bodyText; // return unchanged data
        }

        if (out == in)  {
            // No substitutions required. Don't waste memory creating
            // a new buffer
            return bodyText;
        } else {
            return new StringBuffer(out);
        }
    }


}

