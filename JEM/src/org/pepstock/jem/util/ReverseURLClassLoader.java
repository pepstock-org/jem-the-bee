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
/**
 * First tries to load a class by itself, and only then delegates to parent.
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class ReverseURLClassLoader extends URLClassLoader {
	
	private List<JarFile> bootstrap = new LinkedList<JarFile>();

    /**
     * @param urls
     * @param parent 
     */
    public ReverseURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        loadBootstrapFiles();
    }
    
    @Override
    protected synchronized Class<?> loadClass(String classname, boolean resolve)
            throws ClassNotFoundException {
        Class<?> theClass = findLoadedClass(classname);
        if (theClass != null) {
            return theClass;
        }
        if (isBootStrapClass(classname)){
        	theClass = findSystemClass(classname);
        } else {
            try {
                theClass = findClass(classname);
            } catch (ClassNotFoundException cnfe) {
                theClass =  getParent().loadClass(classname);
            }
        }
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
        
        URL[] urls = getURLs();
        for (int i=0; i<urls.length; i++){
            url = getResourceURL(urls[i], name);
            if (url != null) {
                return url;		
            }
        }
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
        
        URL[] urls = getURLs();
        for (int i=0; i<urls.length; i++){
            is = getResourceStream(urls[i], name);
            if (is != null) {
                return is;
            }
        }
        if (is == null){
        	is = super.getResourceAsStream(name); 
        }
        return is;
	}
	
	   /**
     * Returns the URL of a given resource in the given file which may
     * either be a directory or a zip file.
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
		try {
			File file = new File(url.toURI());
			if (file.isDirectory()) {
				File resource = new File(file, resourceName);
				if (resource.exists()) {
					return resource.toURI().toURL();
				}
			} else if (file.exists()){
				JarFile jFile = new JarFile(file);
				JarEntry entry = jFile.getJarEntry(resourceName);
				if (entry != null) {
					return new URL("jar:" + file.toURI().toURL()
							+ "!/" + entry);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	   /**
     * Returns an inputstream to a given resource in the given file which may
     * either be a directory or a zip file.
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
		try {
			File file = new File(url.toURI());
			if (file.isDirectory()) {
				File resource = new File(file, resourceName);
				if (resource.exists()) {
					return new FileInputStream(resource);
				}
			} else if (file.exists()){
				JarFile jFile = new JarFile(file);
				JarEntry entry = jFile.getJarEntry(resourceName);
				if (entry != null) {
					return jFile.getInputStream(entry);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;    	
    }

	/**
	 * @param classname
	 * @return
	 */
    private boolean isBootStrapClass(String classname) {
    	String classNameFile = StringUtils.replace(classname, ".", "/").concat(".class");
    	for (JarFile file : bootstrap){
    		if (file.getEntry(classNameFile) != null){
    			return true;
    		}
    	}
    	return false;
    }
    
    private void loadBootstrapFiles(){
    	RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
    	if (runtime.isBootClassPathSupported()){
    		String longFiles = ManagementFactory.getRuntimeMXBean().getBootClassPath();
    		String[] files = StringUtils.split(longFiles, File.pathSeparator);
    		for (int i=0; i<files.length; i++){
    			try {
	                JarFile jFile = new JarFile(files[i]);
	                bootstrap.add(jFile);
                } catch (IOException e) {
	                //e.printStackTrace();
                }
    		}
    		
    	}
    }
}
