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
package org.pepstock.catalog.gdg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * It represents the root of GDG. Contains all information about the created
 * generation and the index of last one (gen 0).<br>
 * Is a properties files with a property with index of last gen and all other
 * records are:<br>
 * <br>
 * [generation]=[filename]<br>
 * where generation is a number with format ######. Allows to change the file
 * name without change the generation.<br>
 * Furthermore allows to have all generations without any scan on file systems
 * (usually very slow)
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class Root {

	/**
	 * Key on root properties used to save last generation
	 */
	public static final String LAST_GENERATION_PROPERTY = "jem.last.generation";

	/**
	 * File name of root properties
	 */
	public static final String ROOT_FILE_NAME = "root.properties";

	private File file = null;

	private int lastVersion = 0;

	private Properties properties = new Properties();

	/**
	 * Constructs the object using the directory argument as parent for root.<br>
	 * Root is called <code>root.properties</code>.
	 * 
	 * @param parent root file
	 * @throws IOException if I/O errors loading the data occurs
	 */
	public Root(File parent) throws IOException {
		this(parent, true);
	}

	/**
	 * Constructs the object using the directory argument as parent for root.<br>
	 * Root is called <code>root.properties</code>. Is load argument is passed
	 * true, loads properties file, otherwise means is in creation phase
	 * 
	 * @param parent root file
	 * @param load if <code>true</code>, load root values, otherwise not
	 * @throws IOException if I/O errors loading the data occurs
	 */
	protected Root(File parent, boolean load) throws IOException {
		// checks if is null
		if (parent == null) {
			throw new FileNotFoundException(GDGMessage.JEMD006E.toMessage().getMessage());
		}
		// checks if exists (MUST exists!)
		if (!parent.exists()) {
			throw new FileNotFoundException(GDGMessage.JEMD010E.toMessage().getFormattedMessage(parent.getAbsolutePath()));
		}
		// checks if i directory (MUST be!)
		if (!parent.isDirectory()) {
			throw new FileNotFoundException(GDGMessage.JEMD011E.toMessage().getFormattedMessage(parent.getAbsolutePath()));
		}

		// save file, adding root.properties to parent passed
		this.file = new File(parent, ROOT_FILE_NAME);

		// checks if has to load
		if (load) {
			// checks if exists
			if (!this.file.exists()) {
				throw new FileNotFoundException(GDGMessage.JEMD010E.toMessage().getFormattedMessage(this.file.getAbsolutePath()));
			}

			// load properties file
			FileInputStream fis = null;

			try {
				fis = new FileInputStream(this.file);
				properties.load(fis);
			} catch (IOException e) {
				throw e;
			} finally {
				if (fis != null) {
					fis.close();
				}
			}

			// checks if last generation property exists, if not, IO exception!
			if (properties.containsKey(LAST_GENERATION_PROPERTY)) {
				// get last generation in string format
				String lastGenStr = properties.getProperty(LAST_GENERATION_PROPERTY);
				try {
					// parse to integer. If is not a number, Exception!
					this.lastVersion = Integer.parseInt(lastGenStr);
				} catch (NumberFormatException nfe) {
					throw new IOException(GDGMessage.JEMD012E.toMessage().getFormattedMessage(lastGenStr), nfe);
				}
			} else {
				throw new IOException(GDGMessage.JEMD013E.toMessage().getMessage());
			}
		}
	}

	/**
	 * Returns the root file.
	 * 
	 * @return root file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Returns last generation of GDG, named generation 0.
	 * 
	 * @return genearation 0
	 */
	public int getLastVersion() {
		return lastVersion;
	}

	/**
	 * Sets the last generation, adding a relative position.
	 * 
	 * @param relative relative position (i.e. +1)
	 */
	public void setLastVersion(int relative) {
		properties.setProperty(LAST_GENERATION_PROPERTY, String.valueOf(lastVersion + relative));
	}

	/**
	 * Returns <code>true</code> if the generation (key of properties of root)
	 * exists.
	 * 
	 * @param key generation to search
	 * @return <code>true</code> if the generation (key of properties of root)
	 *         exists, otherwise <code>false</code>
	 */
	public boolean hasVersion(String key) {
		return properties.containsKey(key);
	}

	/**
	 * Returns root properties.
	 * 
	 * @return root properties
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Sets proeprties
	 * 
	 * @param properties the properties to set
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	/**
	 * Returns the file name of generation of passed key.
	 * 
	 * @param key generation to search
	 * @return file name of generation or <code>null</code> if generation
	 *         doesn't exist
	 */
	public String getVersion(String key) {
		return properties.getProperty(key);
	}

	/**
	 * Sets the file name for specific generation.
	 * 
	 * @param key key generation
	 * @param version file name of generation
	 */
	public void setVersion(String key, String version) {
		properties.setProperty(key, version);
	}

	/**
	 * Stores the information of properties into file.
	 * 
	 * @throws FileNotFoundException if the file does'n't exist anymore
	 * @throws IOException if I/O error occurs
	 */
	public void commit() throws FileNotFoundException, IOException {
		// load properties file
		FileOutputStream fos = null;
		// because in hadoop we cannot modify an existing file, in this case
		// root.properties must be deleted and recreated
		try {
			// checks if exist because 
			// when you're defining a new GDG could be it doesn't exists
			if (file.exists()){
				if (file.delete()){
					fos = new FileOutputStream(file);
					properties.store(fos, "new GDG version");
					fos.flush();
				} else {
					throw new IOException("Unable to delete "+file.getAbsolutePath());
				}
			} else {
				// if doesn't exists, creates a new one
				fos = new FileOutputStream(file);
				properties.store(fos, "new GDG version");
				fos.flush();
			}
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}

}