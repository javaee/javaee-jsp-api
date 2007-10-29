

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
package org.apache.catalina.ssi;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import org.apache.catalina.util.DateTool;
import org.apache.catalina.util.URLEncoder;
import org.apache.catalina.util.Strftime;

/**
 * Allows the different SSICommand implementations to share data/talk to each other
 *
 * @author Bip Thelin
 * @author Amy Roh
 * @author Dan Sandberg
 * @version $Revision: 1.2 $, $Date: 2005/12/08 01:28:03 $
 */
public class SSIMediator {
    protected final static String DEFAULT_CONFIG_ERR_MSG = "[an error occurred while processing this directive]";
    protected final static String DEFAULT_CONFIG_TIME_FMT = "%A, %d-%b-%Y %T %Z";
    protected final static String DEFAULT_CONFIG_SIZE_FMT = "abbrev";
    protected static final URLEncoder urlEncoder = new URLEncoder();
    protected String configErrMsg = DEFAULT_CONFIG_ERR_MSG;
    protected String configTimeFmt = DEFAULT_CONFIG_TIME_FMT;
    protected String configSizeFmt = DEFAULT_CONFIG_SIZE_FMT;
    protected String className = getClass().getName();
    protected SSIExternalResolver ssiExternalResolver;
    protected Date lastModifiedDate;
    protected int debug;
    protected Strftime strftime;

    static {
	//We try to encode only the same characters that apache does
	urlEncoder.addSafeCharacter(',');
	urlEncoder.addSafeCharacter(':');
	urlEncoder.addSafeCharacter('-');
        urlEncoder.addSafeCharacter('_');
        urlEncoder.addSafeCharacter('.');
        urlEncoder.addSafeCharacter('*');
        urlEncoder.addSafeCharacter('/');
        urlEncoder.addSafeCharacter('!');
        urlEncoder.addSafeCharacter('~');
        urlEncoder.addSafeCharacter('\'');
        urlEncoder.addSafeCharacter('(');
        urlEncoder.addSafeCharacter(')');
    }

    public SSIMediator( SSIExternalResolver ssiExternalResolver, 
			Date lastModifiedDate,
			int debug ) {
	this.ssiExternalResolver = ssiExternalResolver;	
	this.lastModifiedDate = lastModifiedDate;
	this.debug = debug;

	setConfigTimeFmt( DEFAULT_CONFIG_TIME_FMT, true );
    }
    
    public void setConfigErrMsg( String configErrMsg ) {
	this.configErrMsg = configErrMsg;
    }

    public void setConfigTimeFmt( String configTimeFmt ) {
	setConfigTimeFmt( configTimeFmt, false );
    }

    public void setConfigTimeFmt( String configTimeFmt, boolean fromConstructor ) {
	this.configTimeFmt = configTimeFmt;

	//What's the story here with DateTool.LOCALE_US?? Why??
	this.strftime = new Strftime( configTimeFmt, DateTool.LOCALE_US );

	//Variables like DATE_LOCAL, DATE_GMT, and LAST_MODIFIED need to be updated when
	//the timefmt changes.  This is what Apache SSI does.
	setDateVariables( fromConstructor );
    }

    public void setConfigSizeFmt( String configSizeFmt ) {
	this.configSizeFmt = configSizeFmt;
    }

    public String getConfigErrMsg() {
	return configErrMsg;
    }

    public String getConfigTimeFmt() {
	return configTimeFmt;
    }

    public String getConfigSizeFmt() {
	return configSizeFmt;
    }

    public Collection getVariableNames() {
	Set variableNames = new HashSet();
	//These built-in variables are supplied by the mediator ( if not over-written by the user ) and always exist
	variableNames.add( "DATE_GMT" );
	variableNames.add( "DATE_LOCAL" );
	variableNames.add( "LAST_MODIFIED" );
	ssiExternalResolver.addVariableNames( variableNames );

	//Remove any variables that are reserved by this class
	Iterator iter = variableNames.iterator();
	while ( iter.hasNext() ) {
	    String name = (String) iter.next();
	    if ( isNameReserved( name ) ) {
		iter.remove();
	    }
	}
	return variableNames;	   
    }

    public long getFileSize( String path, boolean virtual ) throws IOException {
	return ssiExternalResolver.getFileSize( path, virtual );
    }

    public long getFileLastModified( String path, boolean virtual ) throws IOException {
	return ssiExternalResolver.getFileLastModified( path, virtual );
    }
    
    public String getFileText( String path, boolean virtual ) throws IOException {
	return ssiExternalResolver.getFileText( path, virtual );
    }

    protected boolean isNameReserved( String name ) {
	return name.startsWith( className + "." );
    }

    public String getVariableValue( String variableName ) {
	return getVariableValue( variableName, "none" );
    }

    public void setVariableValue( String variableName, String variableValue ) {
	if ( !isNameReserved( variableName ) ) {
	    ssiExternalResolver.setVariableValue( variableName, variableValue );
	}
    }

    public String getVariableValue( String variableName, String encoding ) {
	String lowerCaseVariableName = variableName.toLowerCase();
	String variableValue = null;

	if ( !isNameReserved( lowerCaseVariableName ) ) {
	    //Try getting it externally first, if it fails, try getting the 'built-in' value
	    variableValue = ssiExternalResolver.getVariableValue( variableName );
	    if ( variableValue == null ) {
		variableName = variableName.toUpperCase();
		variableValue = (String) ssiExternalResolver.getVariableValue( className + "." + variableName );
	    }
	    if ( variableValue != null ) {
		variableValue = encode( variableValue, encoding );
	    }
	}
	return variableValue;
    }

    protected String formatDate( Date date, TimeZone timeZone ) {
	String retVal;

	if ( timeZone != null ) {
	    //we temporarily change strftime.  Since SSIMediator is inherently single-threaded, this
	    //isn't a problem
	    TimeZone oldTimeZone = strftime.getTimeZone();
	    strftime.setTimeZone( timeZone );
	    retVal = strftime.format(date);    
	    strftime.setTimeZone( oldTimeZone );
	} else {
	    retVal = strftime.format(date);    
	}
	return retVal;
    }

    protected String encode( String value, String encoding ) {
	String retVal = null;

	if ( encoding.equalsIgnoreCase( "url" ) ) {
	    retVal = urlEncoder.encode( value );
	} else if ( encoding.equalsIgnoreCase( "none" ) ) {
	    retVal = value;
	} else if ( encoding.equalsIgnoreCase( "entity" ) ) {
	    //Not sure how this is really different than none
	    retVal = value;
	} else {
	    //This shouldn't be possible
	    throw new IllegalArgumentException("Unknown encoding: " + encoding);
	}
 	return retVal;
    }

    public void log( String message ) {
	ssiExternalResolver.log( message, null );
    }

    public void log( String message, Throwable throwable ) {
	ssiExternalResolver.log( message, throwable );
    }

    protected void setDateVariables( boolean fromConstructor ) {
	boolean alreadySet = ssiExternalResolver.getVariableValue( className + ".alreadyset" ) != null;
	//skip this if we are being called from the constructor, and this has already been set
	if ( !( fromConstructor && alreadySet ) ) {
	    ssiExternalResolver.setVariableValue( className + ".alreadyset", "true" );

	    Date date = new Date();
	    TimeZone timeZone = TimeZone.getTimeZone("GMT");
	    String retVal =  formatDate( date, timeZone );
	    
	    //If we are setting on of the date variables, we want to remove them from the user
	    //defined list of variables, because this is what Apache does
	    setVariableValue ( "DATE_GMT", null ); 
	    ssiExternalResolver.setVariableValue ( className + ".DATE_GMT", retVal ); 
	    
	    retVal = formatDate( date, null );
	    setVariableValue ( "DATE_LOCAL", null );
	    ssiExternalResolver.setVariableValue ( className + ".DATE_LOCAL", retVal );
	    
	    retVal = formatDate( lastModifiedDate, null );
	    setVariableValue ( "LAST_MODIFIED", null );
	    ssiExternalResolver.setVariableValue ( className + ".LAST_MODIFIED", retVal );
	}
    }
}
