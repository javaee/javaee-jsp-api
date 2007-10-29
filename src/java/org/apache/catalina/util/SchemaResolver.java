

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


import java.util.HashMap;

import com.sun.org.apache.commons.digester.Digester;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
// START PWC 6457880
import java.net.URI;
import java.net.URISyntaxException;
// END PWC 6457880

/**
 * This class implements a local SAX's <code>EntityResolver</code>. All
 * DTDs and schemas used to validate the web.xml file will re-directed
 * to a local file stored in the servlet-api.jar and jsp-api.jar.
 *
 * @author Jean-Francois Arcand
 */
public class SchemaResolver implements EntityResolver {

    /**
     * The disgester instance for which this class is the entity resolver.
     */
    protected Digester digester;


    /**
     * The URLs of dtds and schemas that have been registered, keyed by the
     * public identifier that corresponds.
     */
    protected HashMap entityValidator = new HashMap();


    /**
     * The public identifier of the DTD we are currently parsing under
     * (if any).
     */
    protected String publicId = null;


    /**
     * Extension to make the difference between DTD and Schema.
     */
    protected String schemaExtension = "xsd";

    // START PWC 6457880
    /**
     * Attribute value used to turn on/off network access of dtd/schema
     */
    private static boolean forceLocalSchema = false;
    // END PWC 6457880

    /**
     * Create a new <code>EntityResolver</code> that will redirect
     * all remote dtds and schema to a locat destination.
     * @param schemaLocation the XML Schema used to validate xml instance.
     */
    public SchemaResolver(Digester digester) {
        this.digester = digester;
    }


    /**
     * Register the specified DTD/Schema URL for the specified public
     * identifier. This must be called before the first call to
     * <code>parse()</code>.
     *
     * When adding a schema file (*.xsd), only the name of the file
     * will get added. If two schemas with the same name are added,
     * only the last one will be stored.
     *
     * @param publicId Public identifier of the DTD to be resolved
     * @param entityURL The URL to use for reading this DTD
     */
     public void register(String publicId, String entityURL) {
         String key = publicId;
         if (publicId.indexOf(schemaExtension) != -1)
             key = publicId.substring(publicId.lastIndexOf('/')+1);
         entityValidator.put(key, entityURL);
     }


    /**
     * Resolve the requested external entity.
     *
     * @param publicId The public identifier of the entity being referenced
     * @param systemId The system identifier of the entity being referenced
     *
     * @exception SAXException if a parsing exception occurs
     *
     */
    public InputSource resolveEntity(String publicId, String systemId)
        throws SAXException {

        if (publicId != null) {
            this.publicId = publicId;
            digester.setPublicId(publicId);
        }

        // Has this system identifier been registered?
        String entityURL = null;
        if (publicId != null) {
            entityURL = (String) entityValidator.get(publicId);
        }

        // Redirect the schema location to a local destination
        String key = null;
        if (entityURL == null && systemId != null) {
            key = systemId.substring(systemId.lastIndexOf('/')+1);
            entityURL = (String)entityValidator.get(key);
        }

/* PWC 6457880
        if (entityURL == null) {
           return (null);
        }

*/
        // START PWC 6457880
        if (entityURL == null) {
            if (forceLocalSchema) {
                URI u;
                try {
                    u  = new URI(systemId);
                } catch (URISyntaxException e) {
                    throw new SAXException(e);
                }
                String scheme = u.getScheme();
                // if the scheme is local, let the digester look it up
                // otherwise, throw an exception
                if (scheme != null && (scheme.equals("file") ||
                    scheme.equals("jar"))) {
                    return (null);
                }
                else {
                    throw new SAXException("Unable to find local schema for "+key);
                }
            }
            else {
                return (null);
            }
        }
        // END PWC 6457880
        try {
            return (new InputSource(entityURL));
        } catch (Exception e) {
            throw new SAXException(e);
        }

    }

    // START PWC 6457880
    public static void setForceLocalSchema(boolean flag) {
        forceLocalSchema = flag;
    }
    // END PWC 6457880
}
