/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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


/**
 * @author Simone "Busy" Businaro
 * @version 1.0	
 *
 */
public final class GfsFileType {
		
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
	 * To avoid any instantiation
	 */
	private GfsFileType() {
	}
}