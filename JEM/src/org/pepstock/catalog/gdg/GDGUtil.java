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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.log.LogAppl;

/**
 * Contains some methods to access by root to generations of a GDG.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class GDGUtil {

	// format of GDG : pathname/#####. In JCL : pathname(####)
	private static final String GDG_FORMAT = "{0}({1,number,integer})";
	
	// format of GDG : pathname/#####. In JCL : pathname(+####)
	private static final String GDG_WITH_PLUS_FORMAT = "{0}(+{1,number,integer})";

	private static MessageFormat FORMAT = new MessageFormat(GDG_FORMAT);
	
	private static MessageFormat FORMAT_WITH_PLUS = new MessageFormat(GDG_WITH_PLUS_FORMAT);

	/**
	 * To avoid any instantiation
	 */
	private GDGUtil() {
		
	}

	/**
	 * Returns the generation index, passing the relative index.
	 * 
	 * @param root gdg root
	 * @param offset relative position
	 * @return generation index string representation (format #####)
	 */
	public static String getGenerationIndex(Root root, int offset) {
		return createGenerationIndex(root.getLastVersion(), offset);
	}

	/**
	 * Returns the generation index, passing the relative index and last version
	 * (gen 0).
	 * 
	 * @param lastVersion generation 0 of GDGD
	 * @param offset relative position
	 * @return generation index string representation (format #####)
	 */
	private static String createGenerationIndex(int lastVersion, int offset) {
		// gets the index adding last version and offset (could be negative)
		int index = lastVersion + offset;

		// if index is less than zero, out ot bounds
		if (index < 0){
			throw new IndexOutOfBoundsException(GDGMessage.JEMD005E.toMessage().getFormattedMessage(offset));
		}
		// formats the result, padding left with 0
		return StringUtils.leftPad(String.valueOf(index), 5, "0");
	}

	/**
	 * Parses a string, dividing the file name and relative position of
	 * generation
	 * 
	 * @param name filename and relative generation
	 * @return array of 2 elements: filename and generation
	 * @throws ParseException
	 */
	public static Object[] isGDG(String name) throws ParseException {
		Object[] objects = null;
		try {
			objects = FORMAT.parse(name);
		} catch (Exception ex){
			// ignore
			LogAppl.getInstance().ignore(ex.getMessage(), ex);
			objects = FORMAT_WITH_PLUS.parse(name);	
		}
		return objects;
	}

	/**
	 * Returns the generation file name. The generation filename is always the index (without any renaming is #####).
	 * 
	 * @param root gdg root
	 * @param offset relative position
	 * @return generation file name
	 */
	public static String getGeneration(Root root, int offset) {
		// gets generation key in format #####
		String key = createGenerationIndex(root.getLastVersion(), offset);
		String generation = null;
		// checks if generation key already exists, checking inside the root
		// properties
		if (root.hasVersion(key)) {
			// gets generation file name
			generation = root.getVersion(key);
		} else {
			// new generation!! gets root file name and adds "." and generation
			// key
			generation = key;
		}
		return generation;
	}
	/**
	 * Creates a new GDG, creating the complete path and root file properties.
	 * 
	 * @param parent path of GDG
	 * @return new root created
	 * @throws IOException if I/O occurs
	 */
	public static Root createGDG(File parent) throws IOException{
		return createGDG(parent, false);
	}	
	/**
	 * Creates a new GDG, creating the complete path and root file properties.
	 * 
	 * @param parent path of GDG
	 * @param createEmptyGeneration if it must create a empty generation 0
	 * @return new root created
	 * @throws IOException if I/O occurs
	 */
	public static Root createGDG(File parent, boolean createEmptyGeneration) throws IOException{
		// checks if is null
		if (parent == null){
			throw new FileNotFoundException(GDGMessage.JEMD006E.toMessage().getMessage());
		}
		// checks if exists (MUST exists!)
		if (parent.exists()){
			throw new FileNotFoundException(GDGMessage.JEMD007E.toMessage().getFormattedMessage(parent.getAbsolutePath()));
		}
		
		Root root = null;
		// creates all directories
		if (parent.mkdirs()){
			// creates a new Root without loading because is new
			root = new Root(parent, false);

			// if creates a empty generation 0
			if (createEmptyGeneration){
				// get key
				String key = GDGUtil.getGenerationIndex(root, 0);
				// gets generation file name
				String generation = GDGUtil.getGeneration(root, 0);

				// creates the file instance using the directory of Root and
				// generation as file name
				File newFile = new File(root.getFile().getParentFile(), generation);
				if (!newFile.createNewFile()){
					throw new FileNotFoundException(GDGMessage.JEMD008E.toMessage().getFormattedMessage(newFile.getAbsolutePath()));
				}
				// add to properties the file name
				root.setVersion(key, newFile.getName());
			}
			// sets version 0
			root.setLastVersion(0);
			// save the properties file
			root.commit();
		} else {
			// if was not able to create all directories 
			throw new FileNotFoundException(GDGMessage.JEMD009E.toMessage().getFormattedMessage(parent.getAbsolutePath()));
		}
		return root;
	}
}