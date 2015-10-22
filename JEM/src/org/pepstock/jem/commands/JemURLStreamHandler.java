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
package org.pepstock.jem.commands;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.gfs.GfsFileType;
import org.pepstock.jem.node.configuration.ConfigKeys;

/**
 * Is able to manage JEM URL insid the JEM node (not outside). <br>
 * JEM URL should address files (or entry of file) inside of GFS.<br>
 * Here are possible syntax:<br>
 * <br>
 * jem:[gfstype]:jar:[path]![entry] : this is if you you want to access to a JAR entry<br>
 * <br>
 * jem:[gfstype]:[path] : this is if you you want to access to a file<br>
 *  
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 */
public final class JemURLStreamHandler extends URLStreamHandler {
	
	/**
	 * Default schemes separator
	 */
	public static final String SEMICOLONS = ":";
	// jar url protocol
	private static final String JAR_PROTOCOL = "jar:";
	
	/* (non-Javadoc)
	 * @see java.net.URLStreamHandler#openConnection(java.net.URL)
	 */
	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		// gets the complete URL
		String url = u.toString();
		// gets the substring after jem: and the other semicolon
		String subScheme = StringUtils.substringBetween(url, SEMICOLONS, SEMICOLONS);
		
		// the subscheme MUST be one of GFS types
		// if not, error
		if (!GfsFileType.VALUES.contains(subScheme.toLowerCase())){
			throw new MalformedURLException(SubmitMessage.JEMW014E.toMessage().getFormattedMessage(subScheme));
		}
		
		// contains the GFS folder to use
		final String folder;
		
		// basedon subscheme, gets the complete path of the GFS folder
		if (subScheme.equalsIgnoreCase(GfsFileType.BINARY_NAME)){
			folder = System.getProperty(ConfigKeys.JEM_BINARY_PATH_NAME);
		} else if (subScheme.equalsIgnoreCase(GfsFileType.CLASS_NAME)){
			folder = System.getProperty(ConfigKeys.JEM_CLASSPATH_PATH_NAME);
		} else if (subScheme.equalsIgnoreCase(GfsFileType.LIBRARY_NAME)){	
			folder = System.getProperty(ConfigKeys.JEM_LIBRARY_PATH_NAME);
		} else {
			folder = System.getProperty(ConfigKeys.JEM_SOURCE_PATH_NAME);
		}
		
		// creates the first part of URL...
		String firstPartofURL = JemURLStreamHandlerFactory.PROTOCOL + SEMICOLONS + subScheme + SEMICOLONS;
		// ... to get hte rest of URL to create a new URL
		String restOfUrl = StringUtils.substringAfter(url, firstPartofURL);
		
		URL newUrl = null;
		// checks if the rest is a JAR protocol
		if (restOfUrl.startsWith(JAR_PROTOCOL)){
			// creates the URL using the PATH information of JAR URL, adding the right folder of GFS (absolute path)
			File file = new File(folder, StringUtils.substringBetween(restOfUrl, JAR_PROTOCOL, "!"));
			// creates the complete JAR URL, adding the entry
			String jarURL = JAR_PROTOCOL+file.toURI().toURL()+"!"+StringUtils.substringAfter(restOfUrl, "!");
			newUrl = new URL(jarURL);
		} else {
			// creates the URL using the PATH information, adding the right folder of GFS (absolute path)
			File file = new File(folder, restOfUrl);
			newUrl = file.toURI().toURL();
		}
		// returns URL connection
		return newUrl.openConnection();
	}
}
