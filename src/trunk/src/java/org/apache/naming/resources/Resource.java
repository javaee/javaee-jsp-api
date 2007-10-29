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

package org.apache.naming.resources;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.nio.ByteBuffer;
/**
 * Encapsultes the contents of a resource.
 * 
 * @author <a href="mailto:remm@apache.org">Remy Maucherat</a>
 * @version $Revision: 1.3 $
 */
public class Resource {
    
    
    // ----------------------------------------------------------- Constructors
    
    
    public Resource() {
    }
    
    
    public Resource(InputStream inputStream) {
        setContent(inputStream);
    }
    
    
    public Resource(byte[] binaryContent) {
        setContent(binaryContent);
    }
    
    
    // ----------------------------------------------------- Instance Variables
    
    
    /**
     * Binary content.
     */
    protected byte[] binaryContent = null;
    
    
    /**
     * Input stream.
     */
    protected InputStream inputStream = null;
    
    
    // ------------------------------------------------------------- Properties
    
    
    /**
     * Content accessor.
     * 
     * @return InputStream
     */
    public InputStream streamContent()
        throws IOException {
        // START SJSAS 6231069
        /*if (binaryContent != null) {
            return new ByteArrayInputStream(binaryContent);
        }*/
        // END SJSAS 6231069
        inputStream.reset();
        return inputStream;
    }
    
    
    /**
     * Content accessor.
     * 
     * @return binary content
     */
    public byte[] getContent() {
        // START SJSAS 6231069
        if (binaryContent == null && inputStream != null){
            try{
                inputStream.reset();
                binaryContent = new byte[inputStream.available()];
                inputStream.read(binaryContent);
            } catch (IOException ex){}
        }
        // END SJSAS 6231069     
        return binaryContent;
    }
    
    
    /**
     * Content mutator.
     * 
     * @param inputStream New input stream
     */
    public void setContent(InputStream inputStream) {
        this.inputStream = inputStream;
    }
    
    
    /**
     * Content mutator.
     * 
     * @param binaryContent New bin content
     */
    public void setContent(byte[] binaryContent) {
        this.binaryContent = binaryContent;
    }
    
   
    // START SJSAS 6231069
    /**
     * Return the byteBuffer used to map the resource, to possibly be used for 
     * direct file serving. Implementations which support this should override
     * it to return the byteBuffer
     * 
     * @return the mapped byteBuffer representing the resource.
     */
    public ByteBuffer getResourceMappedBuffer() throws IOException{
        return null;
    }
    // END SJSAS 6231069
}
