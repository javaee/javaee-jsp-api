

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


package org.apache.catalina.startup;

import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.catalina.util.SchemaResolver;
import org.apache.catalina.util.StringManager;
import com.sun.org.apache.commons.digester.Digester;
import com.sun.org.apache.commons.digester.RuleSet;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * Wrapper class around the Digester that hide Digester's initialization details
 *
 * @author Jean-Francois Arcand
 */

public class DigesterFactory{

    /**
     * The XML entiry resolver used by the Digester.
     */
    private static SchemaResolver schemaResolver;

    /**
     * The path prefix for .xsd resources
     */
    private static String schemaResourcePrefix;

    /**
     * The path prefix for .dtd resources
     */
    private static String dtdResourcePrefix;


    /**
     * Create a <code>Digester</code> parser with no <code>Rule</code>
     * associated and XML validation turned off.
     */
    public static Digester newDigester(){
        return newDigester(false, false, null);
    }

    
    /**
     * Create a <code>Digester</code> parser with XML validation turned off.
    ???* @param rule an instance of <code>Rule</code??? used for parsing the xml.
     */
    public static Digester newDigester(RuleSet rule){
        return newDigester(false,false,rule);
    }


    /**
     * Sets the path prefix for .xsd resources
     */
    public static void setSchemaResourcePrefix(String prefix) {
        schemaResourcePrefix = prefix;
    }

    /**
     * Sets the path prefix for .dtd resources
     */
    public static void setDtdResourcePrefix(String prefix) {
        dtdResourcePrefix = prefix;
    }

    /**
     * Create a <code>Digester</code> parser.
     * @param xmlValidation turn on/off xml validation
     * @param xmlNamespaceAware turn on/off namespace validation
     * @param rule an instance of <code>Rule</code??? used for parsing the xml.
     */
    public static Digester newDigester(boolean xmlValidation,
                                       boolean xmlNamespaceAware,
                                       RuleSet rule) {

        URL url = null;
        Digester digester = new Digester();
        digester.setNamespaceAware(xmlNamespaceAware);
        digester.setValidating(xmlValidation);
        digester.setUseContextClassLoader(true);
        
        String parserName = 
                digester.getFactory().getClass().getName();
        if (parserName.indexOf("xerces")!=-1) {
            digester = patchXerces(digester);
        }

        schemaResolver = new SchemaResolver(digester);
        if (xmlValidation) {
            // Xerces 2.3 and up has a special way to turn on validation
            // for both DTD and Schema
            if (parserName.indexOf("xerces")!=-1) {
                turnOnXercesValidation(digester);
            } else {
                turnOnValidation(digester);
            }
        }
        registerLocalSchema();
        
        digester.setEntityResolver(schemaResolver);
        if ( rule != null )
            digester.addRuleSet(rule);

        return (digester);
    }


    /**
     * Patch Xerces for backward compatibility.
     */
    private static Digester patchXerces(Digester digester){
        // This feature is needed for backward compatibility with old DDs
        // which used Java encoding names such as ISO8859_1 etc.
        // with Crimson (bug 4701993). By default, Xerces does not
        // support ISO8859_1.
        try{
            digester.setFeature(
                "http://apache.org/xml/features/allow-java-encodings", true);
        } catch(ParserConfigurationException e){
                // log("contextConfig.registerLocalSchema", e);
        } catch(SAXNotRecognizedException e){
                // log("contextConfig.registerLocalSchema", e);
        } catch(SAXNotSupportedException e){
                // log("contextConfig.registerLocalSchema", e);
        }
        return digester;
    }


    /**
     * Utilities used to force the parser to use local schema, when available,
     * instead of the <code>schemaLocation</code> XML element.
     * @param The instance on which properties are set.
     * @return an instance ready to parse XML schema.
     */
    protected static void registerLocalSchema(){

        if (schemaResourcePrefix != null) {
            // Java EE 5
            register(
                    schemaResourcePrefix + Constants.JAVA_EE_SCHEMA_PUBLIC_ID_5,
                    Constants.JAVA_EE_SCHEMA_PUBLIC_ID_5);
            // J2EE
            register(schemaResourcePrefix + Constants.J2eeSchemaPublicId_14,
                     Constants.J2eeSchemaPublicId_14);
            // W3C
            register(schemaResourcePrefix + Constants.W3cSchemaPublicId_10,
                     Constants.W3cSchemaPublicId_10);
            // JSP
            register(schemaResourcePrefix + Constants.JspSchemaPublicId_20,
                     Constants.JspSchemaPublicId_20);
            register(schemaResourcePrefix + Constants.JSP_SCHEMA_PUBLIC_ID_21,
                     Constants.JSP_SCHEMA_PUBLIC_ID_21);
            // TLD
            register(schemaResourcePrefix + Constants.TldSchemaPublicId_20,
                     Constants.TldSchemaPublicId_20);
            register(schemaResourcePrefix + Constants.TLD_SCHEMA_PUBLIC_ID_21,
                     Constants.TLD_SCHEMA_PUBLIC_ID_21);
            // web.xml    
            register(schemaResourcePrefix + Constants.WebSchemaPublicId_24,
                     Constants.WebSchemaPublicId_24);
            register(schemaResourcePrefix + Constants.WebSchemaPublicId_25,
                     Constants.WebSchemaPublicId_25);
            // Web Service
            register(schemaResourcePrefix + Constants.J2eeWebServiceClientSchemaPublicId_11,
                     Constants.J2eeWebServiceClientSchemaPublicId_11);
	} else {
            // Java EE 5
            register(Constants.JAVA_EE_SCHEMA_RESOURCE_PATH_5,
                     Constants.JAVA_EE_SCHEMA_PUBLIC_ID_5);
            // J2EE
            register(Constants.J2eeSchemaResourcePath_14,
                     Constants.J2eeSchemaPublicId_14);
            // W3C
            register(Constants.W3cSchemaResourcePath_10,
                     Constants.W3cSchemaPublicId_10);
            // JSP
            register(Constants.JspSchemaResourcePath_20,
                     Constants.JspSchemaPublicId_20);
            register(Constants.JSP_SCHEMA_RESOURCE_PATH_21,
                     Constants.JSP_SCHEMA_PUBLIC_ID_21);
            // TLD
            register(Constants.TldSchemaResourcePath_20,
                     Constants.TldSchemaPublicId_20);
            register(Constants.TLD_SCHEMA_RESOURCE_PATH_21,
                     Constants.TLD_SCHEMA_PUBLIC_ID_21);
            // web.xml    
            register(Constants.WebSchemaResourcePath_24,
                     Constants.WebSchemaPublicId_24);
            register(Constants.WebSchemaResourcePath_25,
                     Constants.WebSchemaPublicId_25);
            // Web Service
            register(Constants.J2eeWebServiceClientSchemaResourcePath_11,
                     Constants.J2eeWebServiceClientSchemaPublicId_11);
        }

        if (dtdResourcePrefix != null) {
            // TLD
            register(dtdResourcePrefix + "web-jsptaglibrary_1_1.dtd",  
                     Constants.TldDtdPublicId_11);
            register(dtdResourcePrefix + "web-jsptaglibrary_1_2.dtd",
                     Constants.TldDtdPublicId_12);
            // web.xml    
            register(dtdResourcePrefix + "web-app_2_2.dtd",
                     Constants.WebDtdPublicId_22);
            register(dtdResourcePrefix + "web-app_2_3.dtd",
                     Constants.WebDtdPublicId_23);
	} else {
            // TLD
            register(Constants.TldDtdResourcePath_11,  
                     Constants.TldDtdPublicId_11);
            register(Constants.TldDtdResourcePath_12,
                     Constants.TldDtdPublicId_12);
            // web.xml    
            register(Constants.WebDtdResourcePath_22,
                     Constants.WebDtdPublicId_22);
            register(Constants.WebDtdResourcePath_23,
                     Constants.WebDtdPublicId_23);
        }
    }


    /**
     * Load the resource and add it to the 
     */
    protected static void register(String resourceURL, String resourcePublicId){

        URL url = DigesterFactory.class.getResource(resourceURL);
        schemaResolver.register(resourcePublicId , url.toString() );

    }


    /**
     * Turn on DTD and/or validation (based on the parser implementation)
     */
    protected static void turnOnValidation(Digester digester){
        URL url = DigesterFactory.class
                        .getResource(Constants.WebSchemaResourcePath_24);
        digester.setSchema(url.toString());     
    }


    /** 
     * Turn on schema AND DTD validation on Xerces parser.
     */
    protected static void turnOnXercesValidation(Digester digester){
        try{
            digester.setFeature(
                "http://apache.org/xml/features/validation/dynamic",
                true);
            digester.setFeature(
                "http://apache.org/xml/features/validation/schema",
                true);
        } catch(ParserConfigurationException e){
            // log("contextConfig.registerLocalSchema", e);
        } catch(SAXNotRecognizedException e){
            // log("contextConfig.registerLocalSchema", e);
        } catch(SAXNotSupportedException e){
            // log("contextConfig.registerLocalSchema", e);
        }
    }
}
