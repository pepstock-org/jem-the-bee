/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea "Stock" Stocchero
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
package org.pepstock.jem.node.tasks.jndi;

/**
 * Contains only the key used in JNDI for DataDescriptionImpl object
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public final class StringRefAddrKeys {

	/**
	 * Key used in JNDI for DataDescriptionImpl object
	 */
	public static final String DATASTREAMS_KEY = "jem.datadescrition.object";
	
	/**
	 * Key used in JNDI for DataDescriptionImpl object
	 */
	public static final String JPPFTASK_KEY = "jem.jppf.task.object";

	/**
	 * Key used in JNDI for ChunksList object
	 */
	public static final String CHUNKS_KEY = "jem.chunks.object";

	/**
	 * Key used in JNDI for dataDescription chunked object
	 */
	public static final String CHUNKABLE_DATA_DESCRIPTION_KEY = "jem.chunkable.data.description.object";

	/**
	 * Key used in JNDI for dataDescription, for merging, object
	 */
	public static final String MERGED_DATA_DESCRIPTION_KEY = "jem.merged.data.description.object";

	/**
	 * Key used in JNDI for dataDescription, for merging, object
	 */
	public static final String TEMPORARY_FILE_KEY = "jem.temporary.file.object";
	
	/**
	 * Key used in JNDI for data paths object
	 */
	public static final String DATAPATHS_KEY = "jem.datapaths.object";

	/**
	 * To avoid any instantiation
	 */
	private StringRefAddrKeys() {
	}

}