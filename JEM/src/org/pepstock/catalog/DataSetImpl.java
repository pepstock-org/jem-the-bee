/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.catalog;

import java.io.File;
import java.io.Serializable;

/**
 * Represents the file that the task has to use during the execution.<br>
 * It has 2 file instances, because for GDG needs 2 file instances.<br>
 * For all dataset kinds file is equals to real file but only for GDG, file is
 * root of gdg and real file name is complete file with generation.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class DataSetImpl implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name = null;

	private String realName = null;

	private int offset = Integer.MIN_VALUE;

	private String key = null;

	private File file = null;

	private File realFile = null;

	private  String dataSource = null;

	private int type = DataSetType.TEMPORARY;

	/**
	 * Empty constructor
	 */
	public DataSetImpl() {
	}

	/**
	 * Returns the name of dataset. In case of GDG, returns the GDG root
	 * 
	 * @return the name of dataset
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of dataset. In case of GDG, sets the GDG root
	 * 
	 * @param name the name of dataset
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns dataset type.
	 * 
	 * @see DataSetType
	 * @return dataset type
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets dataset type.
	 * 
	 * @see DataSetType
	 * @param type dataset type
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * Returns the name of dataset. In case of GDG, returns the GDG generation
	 * required.
	 * 
	 * @return full dataset name
	 */
	public String getRealName() {
		return (realName == null) ? name : realName;
	}

	/**
	 * Checks if has real name.
	 * 
	 * @return <code>true</code> if real name instance is not null
	 */
	public boolean hasRealName() {
		return this.realName != null;
	}

	/**
	 * Sets the name of dataset. In case of GDG, returns the GDG generation
	 * required.
	 * 
	 * @param realName full dataset name
	 */
	public void setRealName(String realName) {
		this.realName = realName;
	}

	/**
	 * Sets the offset value.
	 * 
	 * @param version version integer
	 */
	public void setOffset(int version) {
		this.offset = version;
	}

	/**
	 * Returns the offset value. If dataset is a GDG this is the relative
	 * position to generation 0, otherwise returns Integer.MIN_VALUE
	 * 
	 * @return If dataset is a GDG this is the relative position to generation
	 *         0, otherwise returns Integer.MIN_VALUE
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Returns the generation in key format, used by root object of GDG.
	 * 
	 * @return key of generation
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the generation in key format, used by root object of GDG.
	 * 
	 * @param key key of generation
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Returns the file, which represents this dataset
	 * 
	 * @return file of this dataset
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Sets the file, which represents this dataset
	 * 
	 * @param file file of this dataset
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Returns <code>true</code> if real file is not null.
	 * 
	 * @return real file
	 */
	public boolean hasRealFile() {
		return this.realFile != null;
	}

	/**
	 * Returns the real file which represents this dataset
	 * 
	 * @return real file
	 */
	public File getRealFile() {
		return (realFile == null) ? file : realFile;
	}

	/**
	 * Sets the real file which represents this dataset
	 * 
	 * @param realFile real file instance
	 */
	public void setRealFile(File realFile) {
		this.realFile = realFile;
	}

	/**
	 * Sets the data source to access to IO stream
	 * @return the resourceId
	 */
	public String getDataSource() {
		return dataSource;
	}

	/**
	 * Returns the data source to access to IO stream
	 * @param dataSource the data source to set
	 */
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Returns the string representation of dataset.
	 * 
	 * @return text dataset name
	 */
	@Override
	public String toString() {
		if (type == DataSetType.GDG){
			return "dsn=" + file.getAbsolutePath() + File.separator + realName;
		} else if (file != null) { 
			return "dsn=" + ((name == null) ? "n/a" : name) + " ["+ file.getAbsolutePath() +"]";
		} else if (dataSource != null) {
			return "dsn=" + name + " ["+ dataSource +"]";
		} else {
			return "dsn=" + name;
		}
	}
}