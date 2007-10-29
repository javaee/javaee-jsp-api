

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

import java.io.IOException;
import java.io.PrintWriter;

/**
 * This is what is used to generate servlets. 
 *
 * @author Anil K. Vijendran
 * @author Kin-man Chung
 */
public class ServletWriter {
    public static final int TAB_WIDTH = 2;
    public static final String SPACES = "                              ";

    // Current indent level:
    private int indent = 0;
    private int virtual_indent = 0;

    // The sink writer:
    PrintWriter writer;
    
    // servlet line numbers start from 1
    private int javaLine = 1;


    public ServletWriter(PrintWriter writer) {
	this.writer = writer;
    }

    public void close() throws IOException {
	writer.close();
    }

    
    // -------------------- Access informations --------------------

    public int getJavaLine() {
        return javaLine;
    }


    // -------------------- Formatting --------------------

    public void pushIndent() {
	virtual_indent += TAB_WIDTH;
	if (virtual_indent >= 0 && virtual_indent <= SPACES.length())
	    indent = virtual_indent;
    }

    public void popIndent() {
	virtual_indent -= TAB_WIDTH;
	if (virtual_indent >= 0 && virtual_indent <= SPACES.length())
	    indent = virtual_indent;
    }

    /**
     * Print a standard comment for echo outputed chunk.
     * @param start The starting position of the JSP chunk being processed. 
     * @param stop  The ending position of the JSP chunk being processed. 
     */
    public void printComment(Mark start, Mark stop, char[] chars) {
        if (start != null && stop != null) {
            println("// from="+start);
            println("//   to="+stop);
        }
        
        if (chars != null)
            for(int i = 0; i < chars.length;) {
                printin();
                print("// ");
                while (chars[i] != '\n' && i < chars.length)
                    writer.print(chars[i++]);
            }
    }

    /**
     * Prints the given string followed by '\n'
     */
    public void println(String s) {
        javaLine++;
	writer.println(s);
    }

    /**
     * Prints a '\n'
     */
    public void println() {
        javaLine++;
	writer.println("");
    }

    /**
     * Prints the current indention
     */
    public void printin() {
	writer.print(SPACES.substring(0, indent));
    }

    /**
     * Prints the current indention, followed by the given string
     */
    public void printin(String s) {
	writer.print(SPACES.substring(0, indent));
	writer.print(s);
    }

    /**
     * Prints the current indention, and then the string, and a '\n'.
     */
    public void printil(String s) {
        javaLine++;
	writer.print(SPACES.substring(0, indent));
	writer.println(s);
    }

    /**
     * Prints the given char.
     *
     * Use println() to print a '\n'.
     */
    public void print(char c) {
	writer.print(c);
    }

    /**
     * Prints the given int.
     */
    public void print(int i) {
	writer.print(i);
    }

    /**
     * Prints the given string.
     *
     * The string must not contain any '\n', otherwise the line count will be
     * off.
     */
    public void print(String s) {
	writer.print(s);
    }

    /**
     * Prints the given string.
     *
     * If the string spans multiple lines, the line count will be adjusted
     * accordingly.
     */
    public void printMultiLn(String s) {
        int index = 0;

        // look for hidden newlines inside strings
        while ((index=s.indexOf('\n',index)) > -1 ) {
            javaLine++;
            index++;
        }

	writer.print(s);
    }
}
