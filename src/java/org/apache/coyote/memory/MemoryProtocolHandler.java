

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
package org.apache.coyote.memory;

import java.io.IOException;

import org.apache.tomcat.util.buf.ByteChunk;

import org.apache.coyote.Adapter;
import org.apache.coyote.InputBuffer;
import org.apache.coyote.OutputBuffer;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.Request;
import org.apache.coyote.Response;


/**
 * Abstract the protocol implementation, including threading, etc.
 * Processor is single threaded and specific to stream-based protocols,
 * will not fit Jk protocols like JNI.
 *
 * @author Remy Maucherat
 */
public class MemoryProtocolHandler
    implements ProtocolHandler {


    // ------------------------------------------------------------- Properties


    /**
     * Pass config info.
     */
    public void setAttribute(String name, Object value) {
    }

    public Object getAttribute(String name) {
        return null;
    }


    /**
     * Associated adapter.
     */
    protected Adapter adapter = null;

    /**
     * The adapter, used to call the connector.
     */
    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }

    public Adapter getAdapter() {
        return (adapter);
    }


    /**
     * Hook to easily retrieve the protocol handler.
     */
    protected static MemoryProtocolHandler protocolHandler = null;

    public static MemoryProtocolHandler getProtocolHandler() {
        return protocolHandler;
    }


    // ------------------------------------------------ ProtocolHandler Methods


    /**
     * Init the protocol.
     */
    public void init()
        throws Exception {
        protocolHandler = this;
    }


    /**
     * Start the protocol.
     */
    public void start()
        throws Exception {
    }


    public void destroy()
        throws Exception {
        protocolHandler = null;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Process specified request.
     */
    public void process(Request request, ByteChunk input,
                        Response response, ByteChunk output)
        throws Exception {

        InputBuffer inputBuffer = new ByteChunkInputBuffer(input);
        OutputBuffer outputBuffer = new ByteChunkOutputBuffer(output);
        request.setInputBuffer(inputBuffer);
        response.setOutputBuffer(outputBuffer);

        adapter.service(request, response);

    }


    // --------------------------------------------- ByteChunkInputBuffer Class


    protected class ByteChunkInputBuffer
        implements InputBuffer {

        protected ByteChunk input = null;

        public ByteChunkInputBuffer(ByteChunk input) {
            this.input = input;
        }

        public int doRead(ByteChunk chunk, Request request) 
            throws IOException {
            return input.substract(chunk);
        }

    }


    // -------------------------------------------- ByteChunkOuptutBuffer Class


    protected class ByteChunkOutputBuffer
        implements OutputBuffer {

        protected ByteChunk output = null;

        public ByteChunkOutputBuffer(ByteChunk output) {
            this.output = output;
        }

        public int doWrite(ByteChunk chunk, Response response) 
            throws IOException {
            output.append(chunk);
            return chunk.getLength();
        }

    }


}
