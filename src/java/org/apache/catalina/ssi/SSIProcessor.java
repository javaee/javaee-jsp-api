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
package org.apache.catalina.ssi;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import org.apache.catalina.util.IOTools;

/**
 * The entry point to SSI processing.  This class does the actual parsing, delegating to the SSIMediator, SSICommand, and
 * SSIExternalResolver as necessary[
 * 
 * @author Dan Sandberg
 * @version $Revision: 1.2 $, $Date: 2005/04/29 01:28:04 $
 *
 */
public class SSIProcessor {
    /** The start pattern */
    protected final static String COMMAND_START = "<!--#";

    /** The end pattern */
    protected final static String COMMAND_END = "-->";
    protected final static int BUFFER_SIZE = 4096;

    protected SSIExternalResolver ssiExternalResolver;
    protected HashMap commands = new HashMap();
    protected int debug;
    
    public SSIProcessor( SSIExternalResolver ssiExternalResolver, int debug ) {
	this.ssiExternalResolver = ssiExternalResolver;
	this.debug = debug;
	addBuiltinCommands();
    }

    protected void addBuiltinCommands() {
	addCommand( "config", new SSIConfig() );
	addCommand( "echo", new SSIEcho() );
	addCommand( "exec", new SSIExec() );
	addCommand( "include", new SSIInclude() );
	addCommand( "flastmod", new SSIFlastmod() );
	addCommand( "fsize", new SSIFsize() );
	addCommand( "printenv", new SSIPrintenv() );
	addCommand( "set", new SSISet() );
    }

    public void addCommand( String name, SSICommand command ) {
	commands.put( name, command );
    }

    /**
     * Process a file with server-side commands, reading from reader and writing the processed
     * version to writer.
     *
     * NOTE: We really should be doing this in a streaming way rather than converting it to an array first.
     *
     * @param reader the reader to read the file containing SSIs from
     * @param writer the writer to write the file with the SSIs processed.
     * @throws IOException when things go horribly awry. Should be unlikely since
     *                     the SSICommand usually catches 'normal' IOExceptions.
     */
    public void process( Reader reader, Date lastModifiedDate, PrintWriter writer ) throws IOException {
	SSIMediator ssiMediator = new SSIMediator( ssiExternalResolver, 
						   lastModifiedDate,
						   debug );

	StringWriter stringWriter = new StringWriter();
	IOTools.flow( reader, stringWriter );
	String fileContents = stringWriter.toString();
	stringWriter = null;

        int index = 0;
	boolean inside = false;
        StringBuffer command = new StringBuffer();
	try {
	    while (index < fileContents.length()) {
		char c = fileContents.charAt( index );
		if ( !inside ) {
		    if ( c == COMMAND_START.charAt( 0 ) && charCmp( fileContents, index, COMMAND_START ) ) {
			inside = true;
			index += COMMAND_START.length();
			command.setLength( 0 ); //clear the command string
		    } else {
			writer.write( c );
			index++;
		    }
		} else {
		    if ( c == COMMAND_END.charAt( 0 ) && charCmp( fileContents, index, COMMAND_END ) ) {
			inside = false;
			index += COMMAND_END.length();
			String strCmd = parseCmd(command);
			if ( debug > 0 ) {
			    ssiExternalResolver.log( "SSIProcessor.process -- processing command: " + strCmd, null );
			}
			String[] paramNames = parseParamNames(command, strCmd.length());
			String[] paramValues = parseParamValues(command, strCmd.length());
			
			//We need to fetch this value each time, since it may change during the loop
			String configErrMsg = ssiMediator.getConfigErrMsg();		    
			SSICommand ssiCommand = (SSICommand) commands.get(strCmd.toLowerCase());
			if ( ssiCommand != null ) {
			    if ( paramNames.length==paramValues.length ) {			    
				ssiCommand.process( ssiMediator, paramNames, paramValues, writer );
			    } else {
				ssiExternalResolver.log( "Parameter names count does not match parameter values count on command: " + strCmd, null );
				writer.write( configErrMsg );
			    }
			} else {
			    ssiExternalResolver.log( "Unknown command: " + strCmd, null);
			    writer.write( configErrMsg );
			}
		    } else {
			command.append( c );
			index++;		   		    		    
		    }
		}
	    }
	} catch ( SSIStopProcessingException e ) {
	    //If we are here, then we have already stopped processing, so all is good
	}	
    }

    /**
     * Parse a StringBuffer and take out the param type token.
     * Called from <code>requestHandler</code>
     * @param cmd a value of type 'StringBuffer'
     * @return a value of type 'String[]'
     */
    protected String[] parseParamNames(StringBuffer cmd, int start) {
        int bIdx = start;
        int i = 0;
        int quotes = 0;
        boolean inside = false;
        StringBuffer retBuf = new StringBuffer();

        while(bIdx < cmd.length()) {
            if(!inside) {
                while(bIdx < cmd.length()&&isSpace(cmd.charAt(bIdx)))
                    bIdx++;

                if(bIdx>=cmd.length())
                    break;

                inside=!inside;
            } else {
                while(bIdx < cmd.length()&&cmd.charAt(bIdx)!='=') {
                    retBuf.append(cmd.charAt(bIdx));
                    bIdx++;
                }

                retBuf.append('"');
                inside=!inside;
                quotes=0;

                while(bIdx < cmd.length()&&quotes!=2) {
                    if(cmd.charAt(bIdx)=='"')
                            quotes++;

                    bIdx++;
                }
            }
        }

        StringTokenizer str = new StringTokenizer(retBuf.toString(), "\"");
        String[] retString = new String[str.countTokens()];

        while(str.hasMoreTokens()) {
            retString[i++] = str.nextToken().trim();
        }

        return retString;
    }

    /**
     * Parse a StringBuffer and take out the param token.
     * Called from <code>requestHandler</code>
     * @param cmd a value of type 'StringBuffer'
     * @return a value of type 'String[]'
     */
    protected String[] parseParamValues(StringBuffer cmd, int start) {
        int bIdx = start;
        int i = 0;
        int quotes = 0;
        boolean inside = false;
        StringBuffer retBuf = new StringBuffer();

        while(bIdx < cmd.length()) {
            if(!inside) {
                while(bIdx < cmd.length()&&
                      cmd.charAt(bIdx)!='"')
                    bIdx++;

                if(bIdx>=cmd.length())
                    break;

                inside=!inside;
            } else {
                while(bIdx < cmd.length() && cmd.charAt(bIdx)!='"') {
                    retBuf.append(cmd.charAt(bIdx));
                    bIdx++;
                }

                retBuf.append('"');
                inside=!inside;
            }

            bIdx++;
        }

        StringTokenizer str = new StringTokenizer(retBuf.toString(), "\"");
        String[] retString = new String[str.countTokens()];

        while(str.hasMoreTokens()) {
            retString[i++] = str.nextToken();
        }

        return retString;
    }

    /**
     * Parse a StringBuffer and take out the command token.
     * Called from <code>requestHandler</code>
     * @param cmd a value of type 'StringBuffer'
     * @return a value of type 'String', or null if there is none
     */
    private String parseCmd(StringBuffer cmd) {
	int firstLetter = -1;
	int lastLetter = -1;
	for ( int i=0; i < cmd.length(); i++ ) {
	    char c = cmd.charAt( i );
	    if ( Character.isLetter( c ) ) {
		if ( firstLetter == -1 ) {
		    firstLetter = i;
		}
		lastLetter = i;
	    } else if ( isSpace( c ) ) {
		if ( lastLetter > -1 ) {
		    break;
		}
	    } else {
		break;
	    }
	}

	String command = null;
	if ( firstLetter != -1 ) {
	    command = cmd.substring( firstLetter, lastLetter + 1 );
	}
        return command;
    }

    protected boolean charCmp(String buf, int index, String command) {
	return buf.regionMatches( index, command, 0, command.length() );
    }

    protected boolean isSpace(char c) {
        return c==' '||c=='\n'||c=='\t'||c=='\r';
    }
}
