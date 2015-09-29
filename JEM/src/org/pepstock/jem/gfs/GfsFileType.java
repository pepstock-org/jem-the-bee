/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Simone "Busy" Businaro
    This program is free software: you can redistibute it and/or modify
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
package org.pepstock.jem.gfs;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * @author Simone "Busy" Businaro
 * @version 1.0	
 *
 */
public final class GfsFileType {
	
	/**
	 * for the GFS/data folder
	 */
	public static final int NO_TYPE = -1;
		
	/**
	 * for the GFS/data folder
	 */
	public static final int DATA = 0;

	/**
	 * for the GFS/library folder
	 */
	public static final int LIBRARY = 1;
	
	/**
	 * for the GFS/source folder
	 */
	public static final int SOURCE = 2;
	
	/**
	 * for the GFS/classpath folder
	 */
	public static final int CLASS = 3;
	
	/**
	 * for the GFS/binary folder
	 */
	public static final int BINARY = 4;
	
	/**
	 * for the GFS/data folder
	 */
	public static final String DATA_NAME = "Data";

	/**
	 * for the GFS/library folder
	 */
	public static final String LIBRARY_NAME = "Library";
	
	/**
	 * for the GFS/source folder
	 */
	public static final String SOURCE_NAME = "Source";
	
	/**
	 * for the GFS/classpath folder
	 */
	public static final String CLASS_NAME = "Class";
	
	/**
	 * for the GFS/binary folder
	 */
	public static final String BINARY_NAME = "Binary";
	
	/**
	 * Array with all disposition
	 */
	public static final List<String> VALUES = Collections.unmodifiableList(Arrays.asList(DATA_NAME.toLowerCase(), 
			LIBRARY_NAME.toLowerCase(), SOURCE_NAME.toLowerCase(), CLASS_NAME.toLowerCase(), BINARY_NAME.toLowerCase()));
	

	/**
	 * To avoid any instantiation
	 */
	private GfsFileType() {
	}
	
	/**
	 * Returns the type of path of GFS folder.
	 * @param name data name (DATA, SOURCE, LIBRARY, CLASS, BINARY).
	 * @return the type of path of GFS folder.
	 */
	public static final int getType(String name){
		if (GfsFileType.DATA_NAME.equalsIgnoreCase(name)) {
			return GfsFileType.DATA;
		} else if (GfsFileType.LIBRARY_NAME.equalsIgnoreCase(name)) {
			return GfsFileType.LIBRARY;
		} else if (GfsFileType.SOURCE_NAME.equalsIgnoreCase(name)) {
			return GfsFileType.SOURCE;
		} else if (GfsFileType.CLASS_NAME.equalsIgnoreCase(name)) {
			return GfsFileType.CLASS;
		} else if (GfsFileType.BINARY_NAME.equalsIgnoreCase(name)) {
			return GfsFileType.BINARY;
		}
		return NO_TYPE;
	}
	
	/**
	 * Returns the name of path of GFS folder.
	 * @param type data type (DATA, SOURCE, LIBRARY, CLASS, BINARY).
	 * @return the name of path of GFS folder.
	 */
	public static final String getName(int type){
		String name = null;
		switch (type) {
		case GfsFileType.DATA:
			name = GfsFileType.DATA_NAME;
			break;
		case GfsFileType.LIBRARY:
			name = GfsFileType.LIBRARY_NAME;
			break;
		case GfsFileType.SOURCE:
			name = GfsFileType.SOURCE_NAME;
			break;
		case GfsFileType.CLASS:
			name = GfsFileType.CLASS_NAME;
			break;
		case GfsFileType.BINARY:
			name = GfsFileType.BINARY_NAME;
			break;
		default:
			name = null;
			break;
		}
		return name;
	}
}