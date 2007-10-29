

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


package org.apache.catalina.session;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.ArrayList;
//HERCULES:added
import java.util.Hashtable;
//HERCULES:added
import javax.servlet.ServletContext;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Loader;
import org.apache.catalina.Session;
import org.apache.catalina.Store;
import org.apache.catalina.Container;
import org.apache.catalina.util.CustomObjectInputStream;


/**
 * Concrete implementation of the <b>Store</b> interface that utilizes
 * a file per saved Session in a configured directory.  Sessions that are
 * saved are still subject to being expired based on inactivity.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.2 $ $Date: 2005/12/08 01:27:57 $
 */

public final class FileStore
    extends StoreBase implements Store {


    // ----------------------------------------------------- Constants


    /**
     * The extension to use for serialized session filenames.
     */
    private static final String FILE_EXT = ".session";


    // ----------------------------------------------------- Instance Variables


    /**
     * The pathname of the directory in which Sessions are stored.
     * This may be an absolute pathname, or a relative path that is
     * resolved against the temporary work directory for this application.
     */
    private String directory = ".";


    /**
     * A File representing the directory in which Sessions are stored.
     */
    private File directoryFile = null;
    
    
    /**
     * A utility class used to call into services from IOUtils
     * HERCULES: addition
     */
    private IOUtilsCaller webUtilsCaller = null;     


    /**
     * The descriptive information about this implementation.
     */
    private static final String info = "FileStore/1.0";

    /**
     * Name to register for this Store, used for logging.
     */
    private static final String storeName = "fileStore";

    /**
     * Name to register for the background thread.
     */
    private static final String threadName = "FileStore";
    
    /**
    * Our write-through cache of session objects
    * HERCULES: addition
    */
    protected Hashtable sessions = new Hashtable();     


    // ------------------------------------------------------------- Properties


    /**
     * Return the directory path for this Store.
     */
    public String getDirectory() {

        return (directory);

    }


    /**
     * Set the directory path for this Store.
     *
     * @param path The new directory path
     */
    public void setDirectory(String path) {

        String oldDirectory = this.directory;
        this.directory = path;
        this.directoryFile = null;
        support.firePropertyChange("directory", oldDirectory,
                                   this.directory);

    }
    
    /**
     * get the utility class used to call into services from IOUtils
     * HERCULES: addition
     */
    protected IOUtilsCaller getWebUtilsCaller() {
        if(webUtilsCaller == null) {
            WebIOUtilsFactory factory = new WebIOUtilsFactory();
            webUtilsCaller = factory.createWebIOUtil();            
        }
        return webUtilsCaller;
    }    


    /**
     * Return descriptive information about this Store implementation and
     * the corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getInfo() {

        return (info);

    }

    /**
     * Return the thread name for this Store.
     */
    public String getThreadName() {
        return(threadName);
    }

    /**
     * Return the name for this Store, used for logging.
     */
    public String getStoreName() {
        return(storeName);
    }


    /**
     * Return the number of Sessions present in this Store.
     *
     * @exception IOException if an input/output error occurs
     */
    public int getSize() throws IOException {

        // Acquire the list of files in our storage directory
        File file = directory();
        if (file == null) {
            return (0);
        }
        String files[] = file.list();

        // Figure out which files are sessions
        int keycount = 0;
        for (int i = 0; i < files.length; i++) {
            if (files[i].endsWith(FILE_EXT)) {
                keycount++;
            }
        }
        return (keycount);

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Remove all of the Sessions in this Store.
     *
     * @exception IOException if an input/output error occurs
     */
    public void clear()
        throws IOException {

        String[] keys = keys();
        for (int i = 0; i < keys.length; i++) {
            remove(keys[i]);
        }

    }


    /**
     * Return an array containing the session identifiers of all Sessions
     * currently saved in this Store.  If there are no such Sessions, a
     * zero-length array is returned.
     *
     * @exception IOException if an input/output error occurred
     */
    public String[] keys() throws IOException {

        // Acquire the list of files in our storage directory
        File file = directory();
        if (file == null) {
            return (new String[0]);
        }
        String files[] = file.list();

        // Build and return the list of session identifiers
        ArrayList list = new ArrayList();
        int n = FILE_EXT.length();
        for (int i = 0; i < files.length; i++) {
            if (files[i].endsWith(FILE_EXT)) {
                list.add(files[i].substring(0, files[i].length() - n));
            }
        }
        return ((String[]) list.toArray(new String[list.size()]));

    }


    /**
     * Load and return the Session associated with the specified session
     * identifier from this Store, without removing it.  If there is no
     * such stored Session, return <code>null</code>.
     *
     * @param id Session identifier of the session to load
     *
     * @exception ClassNotFoundException if a deserialization error occurs
     * @exception IOException if an input/output error occurs
     */
    public Session load(String id)
        throws ClassNotFoundException, IOException {
            
        //HERCULES:addition
        // Check to see if it's in our cache first
        Session sess = (Session)sessions.get(id);
        if ( sess != null ) {
            return sess;
        }
        //HERCULES:addition            

        // Open an input stream to the specified pathname, if any
        File file = file(id);
        if (file == null) {
            return (null);
        }

        if (! file.exists()) {
            return (null);
        }
        if (debug >= 1) {
            log(sm.getString(getStoreName()+".loading",
                             id, file.getAbsolutePath()));
        }

        FileInputStream fis = null;
        ObjectInputStream ois = null;
        Loader loader = null;
        ClassLoader classLoader = null;
        try {
            fis = new FileInputStream(file.getAbsolutePath());
            BufferedInputStream bis = new BufferedInputStream(fis);
            Container container = manager.getContainer();
            if (container != null)
                loader = container.getLoader();
            if (loader != null)
                classLoader = loader.getClassLoader();
            //HERCULES:mod begin
            /* these 4 lines replaced
            if (classLoader != null)
                ois = new CustomObjectInputStream(bis, classLoader);
            else
                ois = new ObjectInputStream(bis);
             */ 
            //requires change to EJBUtils also
            //this next line was Hercules 7.0EE code
            //ois = EJBUtils.getInputStream(bis, classLoader, true, true);
            if (classLoader != null) {
                IOUtilsCaller caller = this.getWebUtilsCaller();
                if(webUtilsCaller != null) {
                    try {
                        ois = webUtilsCaller.createObjectInputStream(bis, true, classLoader);
                    } catch (Exception ex) {}
                } else {
                    ois = new CustomObjectInputStream(bis, classLoader); 
                }
            }
            if (ois == null) {
                ois = new ObjectInputStream(bis); 
            }
            //end HERCULES:mod
        } catch (FileNotFoundException e) {
            if (debug >= 1)
                log("No persisted data file found");
            return (null);
        } catch (IOException e) {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException f) {
                    ;
                }
                ois = null;
            }
            throw e;
        }

        try {
            StandardSession session =
                StandardSession.deserialize(ois, manager);
            session.setManager(manager);
            //HERCULES: addition
            // Put it in the cache
            sessions.put(session.getIdInternal(), session);     
            //HERCULES: addition            
            return (session);
        } finally {
            // Close the input stream
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException f) {
                    ;
                }
            }
        }
    }


    /**
     * Remove the Session with the specified session identifier from
     * this Store, if present.  If no such Session is present, this method
     * takes no action.
     *
     * @param id Session identifier of the Session to be removed
     *
     * @exception IOException if an input/output error occurs
     */
    public void remove(String id) throws IOException {

        File file = file(id);
        if (file == null) {
            return;
        }
        if (debug >= 1) {
            log(sm.getString(getStoreName()+".removing",
                             id, file.getAbsolutePath()));
        }
        //HERCULES: addition
        // Take it out of the cache 
        sessions.remove(id);        
        //HERCULES: addition        
        file.delete();

    }


    /**
     * Save the specified Session into this Store.  Any previously saved
     * information for the associated session identifier is replaced.
     *
     * @param session Session to be saved
     *
     * @exception IOException if an input/output error occurs
     */
    public void save(Session session) throws IOException {

        // Open an output stream to the specified pathname, if any
        File file = file(session.getIdInternal());
        if (file == null) {
            return;
        }
        if (debug >= 1) {
            log(sm.getString(getStoreName()+".saving",
                             session.getIdInternal(), file.getAbsolutePath()));
        }
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(file.getAbsolutePath());
            //oos = new ObjectOutputStream(new BufferedOutputStream(fos));           
            //Hercules: modification replaces previous line
            //requires mod to EJBUtils also
            //oos = EJBUtils.getOutputStream(new BufferedOutputStream(fos), true);
            IOUtilsCaller caller = this.getWebUtilsCaller();
            if(webUtilsCaller != null) {
                try {
                    oos = webUtilsCaller.createObjectOutputStream(new BufferedOutputStream(fos), true);
                } catch (Exception ex) {}
            }
            //use normal ObjectOutputStream if there is a failure during stream creation
            if(oos == null) {
                oos = new ObjectOutputStream(new BufferedOutputStream(fos)); 
            }
            //end Hercules             
        } catch (IOException e) {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException f) {
                    ;
                }
            }
            throw e;
        }

        try {
            oos.writeObject(session);
        } finally {
            oos.close();
        }

    }


    // -------------------------------------------------------- Private Methods


    /**
     * Return a File object representing the pathname to our
     * session persistence directory, if any.  The directory will be
     * created if it does not already exist.
     */
    private File directory() {

        if (this.directory == null) {
            return (null);
        }
        if (this.directoryFile != null) {
            // NOTE:  Race condition is harmless, so do not synchronize
            return (this.directoryFile);
        }
        File file = new File(this.directory);
        if (!file.isAbsolute()) {
            Container container = manager.getContainer();
            if (container instanceof Context) {
                ServletContext servletContext =
                    ((Context) container).getServletContext();
                File work = (File)
                    servletContext.getAttribute(Globals.WORK_DIR_ATTR);
                file = new File(work, this.directory);
            } else {
                throw new IllegalArgumentException
                    ("Parent Container is not a Context");
            }
        }
        if (!file.exists() || !file.isDirectory()) {
            file.delete();
            file.mkdirs();
        }
        this.directoryFile = file;
        return (file);

    }


    /**
     * Return a File object representing the pathname to our
     * session persistence file, if any.
     *
     * @param id The ID of the Session to be retrieved. This is
     *    used in the file naming.
     */
    private File file(String id) {

        if (this.directory == null) {
            return (null);
        }
        String filename = id + FILE_EXT;
        File file = new File(directory(), filename);
        return (file);

    }


}
