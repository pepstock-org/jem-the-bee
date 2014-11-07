/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.pepstock.jem.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.log.LogAppl;
/**
 * Custom class loader. First of all it tries to load a class by itself, and only then delegates to parent.<br>
 * To search a class, works following this path:<br>
 * <ul>
 * <li> Bootstrap classpath
 * <li> list of the files of this class loader
 * <li> parent class loader
 * </ul>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class ReverseURLClassLoader extends URLClassLoader {
	
	private List<JarFile> bootstrap = new LinkedList<JarFile>();

    /**
     * Constructor with all files (passed by URL) and classvloader parent. loads all bootstrap files
     * 
     * @param urls list of files
     * @param parent class loader parent
     */
    public ReverseURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        // loads bootstrap files
        loadBootstrapFiles();
    }
    
    
    
    /* (non-Javadoc)
	 * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
	 */
	@Override
	protected synchronized Class<?> loadClass(String classname, boolean resolve) throws ClassNotFoundException {
		// checks if the class is already loaded
        Class<?> theClass = findLoadedClass(classname);
        if (theClass != null) {
            return theClass;
        }
        // checks if the class name is in the bootstrap classpath (like java.*)
        if (isBootStrapClass(classname)){
        	theClass = findSystemClass(classname);
        } else {
        	// if not in bootstrap, searches in the list of URL passed 
            try {
                theClass = findClass(classname);
            } catch (ClassNotFoundException cnfe) {
            	LogAppl.getInstance().ignore(cnfe.getMessage(), cnfe);
            	// if not found again, goes to the parent classloader
                theClass =  getParent().loadClass(classname);
            }
        }
        // if it must resolve
        if (resolve) {
            resolveClass(theClass);
        }

        return theClass;
    }
    
    
    /* (non-Javadoc)
	 * @see java.lang.ClassLoader#getResource(java.lang.String)
	 */
	@Override
	public URL getResource(String name) {
        // we need to search the components of the path to see if
        // we can find the class we want.
        URL url = null;
        
        // scans all URLS
        URL[] urls = getURLs();
        for (int i=0; i<urls.length; i++){
        	// checks inside the files
            url = getResourceURL(urls[i], name);
            // if found, return URL
            if (url != null) {
                return url;		
            }
        }
        // if not found, asks to the parent
        if (url == null){
        	url =  super.getResource(name); 
        }
		return url;
	}

	/* (non-Javadoc)
	 * @see java.lang.ClassLoader#getResourceAsStream(java.lang.String)
	 */
	@Override
	public InputStream getResourceAsStream(String name) {
        // we need to search the components of the path to see if
        // we can find the class we want.
        InputStream is = null;
        
        // scans all URLS
        URL[] urls = getURLs();
        for (int i=0; i<urls.length; i++){
        	// checks inside the files
            is = getResourceStream(urls[i], name);
            // if found, return input stream
            if (is != null) {
                return is;
            }
        }
        // if not found, asks to the parent
        if (is == null){
        	is = super.getResourceAsStream(name); 
        }
        return is;
	}
	
   /**
     * Returns the URL of a given resource in the given file which may
     * either be a directory or a jar file.
     *
     * @param file The file (directory or jar) in which to search for
     *             the resource. Must not be <code>null</code>.
     * @param resourceName The name of the resource for which a stream
     *                     is required. Must not be <code>null</code>.
     *
     * @return a stream to the required resource or <code>null</code> if the
     *         resource cannot be found in the given file object.
     */
	private URL getResourceURL(URL url, String resourceName) {
		JarFile jFile = null;
		try {
			// gets the file from URL
			File file = new File(url.toURI());
			// if is a directory, then checks on the file system
			// where URL id the parent file and resource name is the file
			if (file.isDirectory()) {
				File resource = new File(file, resourceName);
				// checks if exists
				if (resource.exists()) {
					// returns URL
					return resource.toURI().toURL();
				}
			} else if (file.exists()){
				// if here, the URL must be a link to a JAR file
				jFile = new JarFile(file);
				// searches in the JAR for the resource name
				JarEntry entry = jFile.getJarEntry(resourceName);
				// if found return the JAR URL
				if (entry != null) {
					return new URL("jar:" + file.toURI().toURL()
							+ "!/" + entry);
				}
			}
		} catch (Exception e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
		} finally {
			// closes the JAR file if open
			if (jFile != null){
				try {
					jFile.close();
				} catch (IOException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
		return null;
	}
	/**
     * Returns an input stream to a given resource in the given file which may
     * either be a directory or a jar file.
     *
     * @param url the file (directory or jar) in which to search for the
     *             resource. Must not be <code>null</code>.
     * @param resourceName The name of the resource for which a stream is
     *                     required. Must not be <code>null</code>.
     *
     * @return a stream to the required resource or <code>null</code> if
     *         the resource cannot be found in the given file.
     */
    private InputStream getResourceStream(URL url, String resourceName) {
    	JarFile jFile = null;
		try {
			// gets the file from URL
			File file = new File(url.toURI());
			// if is a directory, then checks on the file system
			// where URL id the parent file and resource name is the file
			if (file.isDirectory()) {
				File resource = new File(file, resourceName);
				// checks if exists
				if (resource.exists()) {
					// returns inpu stream
					return new FileInputStream(resource);
				}
			} else if (file.exists()){
				// if here, the URL must be a link to a JAR file
				jFile = new JarFile(file);
				// searches in the JAR for the resource name
				JarEntry entry = jFile.getJarEntry(resourceName);
				// if found return the JAR InputStream
				if (entry != null) {
					// FINDBUGS: it's correct do not close the jar file
					// otherwise the stream will be closed
					return jFile.getInputStream(entry);
				}
			}
		} catch (Exception e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
		}
		return null;    	
    }

	/**
	 * Checks if the class name passed is in the boot strap classpath (like java.* or javax.*) 
	 * @param classname class name to search
	 * @return <code>true</code> if the class name is defined inside of boot strap class path
	 */
    private boolean isBootStrapClass(String classname) {
    	// replace . with / because inside of JAR the classes are stored as in a file system
    	String classNameFile = StringUtils.replace(classname, ".", "/").concat(".class");
    	// scans all JAR to check if the class is inside of jars
    	for (JarFile file : bootstrap){
    		if (file.getEntry(classNameFile) != null){
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Loads all JARS of the bootstrap classpath. If JRE/jDK doesn't support bootstrap, could create 
     * issues. Uses java.management to get these information
     */
    private void loadBootstrapFiles(){
    	// 
    	RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
    	// checks if bootstrap classpath is supporte
    	if (runtime.isBootClassPathSupported()){
    		// gets all files
    		String longFiles = ManagementFactory.getRuntimeMXBean().getBootClassPath();
    		// splits by path separator
    		String[] files = StringUtils.split(longFiles, File.pathSeparator);
    		// reads all JAR files
    		for (int i=0; i<files.length; i++){
    			try {
	                JarFile jFile = new JarFile(files[i]);
	                bootstrap.add(jFile);
                } catch (IOException e) {
                	LogAppl.getInstance().ignore(e.getMessage(), e);
                }
    		}
    	}
    }
}
