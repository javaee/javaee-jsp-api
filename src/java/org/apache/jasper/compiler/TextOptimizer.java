

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

import org.apache.jasper.JasperException;
import org.apache.jasper.Options;

/**
 */
public class TextOptimizer {

    /**
     * A visitor to concatenate contiguous template texts.
     */
    static class TextCatVisitor extends Node.Visitor {

        private Options options;
        private PageInfo pageInfo;
        private int textNodeCount = 0;
        private Node.TemplateText firstTextNode = null;
        private StringBuffer textBuffer;
        private final String emptyText = new String("");

        public TextCatVisitor(Compiler compiler) {
            options = compiler.getCompilationContext().getOptions();
            pageInfo = compiler.getPageInfo();
        }

        public void doVisit(Node n) throws JasperException {
            collectText();
        }

	/*
         * The following directis are ignored in text concatenation
         */

        public void visit(Node.PageDirective n) throws JasperException {
        }

        public void visit(Node.TagDirective n) throws JasperException {
        }

        public void visit(Node.TaglibDirective n) throws JasperException {
        }

        public void visit(Node.AttributeDirective n) throws JasperException {
        }

        public void visit(Node.VariableDirective n) throws JasperException {
        }

        /*
         * Don't concatenate text across body boundaries
         */
        public void visitBody(Node n) throws JasperException {
            super.visitBody(n);
            collectText();
        }

        public void visit(Node.TemplateText n) throws JasperException {

            if (( options.getTrimSpaces() ||
                     pageInfo.isTrimDirectiveWhitespaces()) &&
                  n.isAllSpace()) {
                n.setText(emptyText);
                return;
            }

            if (textNodeCount++ == 0) {
                firstTextNode = n;
                textBuffer = new StringBuffer(n.getText());
            } else {
                // Append text to text buffer
                textBuffer.append(n.getText());
                n.setText(emptyText);
            }
        }

        /**
         * This method breaks concatenation mode.  As a side effect it copies
         * the concatenated string to the first text node 
         */
        private void collectText() {

            if (textNodeCount > 1) {
                // Copy the text in buffer into the first template text node.
                firstTextNode.setText(textBuffer.toString());
            }
            textNodeCount = 0;
        }

    }

    public static void concatenate(Compiler compiler, Node.Nodes page)
            throws JasperException {

        TextCatVisitor v = new TextCatVisitor(compiler);
        page.visit(v);

	// Cleanup, in case the page ends with a template text
        v.collectText();
    }
}
