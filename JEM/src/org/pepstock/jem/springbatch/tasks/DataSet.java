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
package org.pepstock.jem.springbatch.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.text.ParseException;

import org.pepstock.catalog.DataSetImpl;
import org.pepstock.catalog.gdg.GDGUtil;
import org.pepstock.jem.springbatch.SpringBatchMessage;
import org.pepstock.jem.springbatch.SpringBatchRuntimeException;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;

/**
 * Is a special Resources of Spring framework. It could be used in the tasklet
 * or with ItemReader and ItemWriter.<br>
 * Represents a file to use for reading or writing.
 * 
 * @see org.springframework.core.io.AbstractResource
 * @author Andrea "Stock" Stocchero
 * 
 */
public class DataSet extends AbstractResource implements Serializable{

	private static final long serialVersionUID = 1L;

	private DataSetImpl dataSetImpl = new DataSetImpl();

	private String name = null;

	private int offset = Integer.MIN_VALUE;

	private String textBuffer = "";
	
	private String datasource = null;
	
	private boolean replaceProperties = false;

	/**
	 * Reads from environment the variable with name assigned to constant
	 * <code>Keys.JEM_DATA_PATH_NAME</code>.<br>
	 * if variable is not set, a NullPointerException will throw.
	 */
	public DataSet() {
	}

	/**
	 * Returns the dataset implementation.
	 * 
	 * @see org.pepstock.catalog.DataSetImpl
	 * @return dataset implementation
	 */
	public DataSetImpl getDataSetImpl() {
		return dataSetImpl;
	}

	/**
	 * Get the name of dataset, used to create the real file name
	 * 
	 * @return name of dataset
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets teh name of dataset. Checks if is GDG computing the realtive offset
	 * 
	 * @param name dataset name
	 */
	public void setName(String name) {
		// checks if the name is empty
		if (name.trim().length() == 0){
			throw new SpringBatchRuntimeException(SpringBatchMessage.JEMS029E);
		}
		try {
			// parse the name
			Object[] objects = GDGUtil.isGDG(name);
			this.name = objects[0].toString();
			offset = Integer.parseInt(objects[1].toString());
		} catch (ParseException e) {
			// if we have a exception, is not a GDG
			this.name = name;
			offset = Integer.MIN_VALUE;
		}
		// set name to dataset impl
		dataSetImpl.setName(name);
	}

	/**
	 * Returns the offset if the dataset is a GDG. If no, return minimum value
	 * of a integer
	 * 
	 * @return relative offset of GDG or Integer.MIN_VALUE
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Returns <code>true</code> if the name starts with @@.<br>
	 * If <code>true</code>, a temporary file will be created.
	 * 
	 * @return <code>true</code> if the name starts with @@ and then is
	 *         temporary file
	 */
	public boolean isTemporary() {
		if (name != null){
			return name.startsWith("@@") && !isInline();
		} else {
			return false;
		}
	}

	/**
	 * Sets the content of dataset, directly from JCL.
	 * 
	 * @param text content of dataset
	 */
	public void setText(String text) {
		textBuffer = text;
	}

	/**
	 * Returns the content of dataset
	 * 
	 * @return content of dataset or <code>null</code>
	 */
	public String getText() {
		return textBuffer;
	}

	/**
	 * Returns <code>true</code> if the data set implementation is already
	 * loaded with the file
	 * 
	 * @return <code>true</code> if the data set implementation is already
	 *         loaded with the file
	 */
	public boolean isImplemented() {
		return dataSetImpl.getFile() != null;
	}

	/**
	 * Returns <code>true</code> if the data set content is defined by JCL
	 * 
	 * @see DataSet#setText(String)
	 * @return <code>true</code> if the data set content is defined by JCL
	 */
	public boolean isInline() {
		return textBuffer.length() > 0;
	}

	/**
	 * Returns <code>true</code> if the data set is a GDG, so with format
	 * <code>filename(offset)</code>.
	 * 
	 * @return <code>true</code> if the data set is a GDG, so with format
	 *         <code>filename(offset)</code>
	 */
	public boolean isGdg() {
		return offset != Integer.MIN_VALUE  && !isDefinedDatasource();
	}

	/**
	 * Returns <code>true</code> if data set is defined to be linked to a common resource.
	 * 
	 * @return <code>true</code> if data set is defined to be linked to a common resource
	 */
	public boolean isDefinedDatasource() {
		return datasource != null;
	}

	/**
	 * Returns the common resources to use to have a IO stream 
	 * 
	 * @return the resource logical name
	 */
	public String getDatasource() {
		return datasource;
	}

	/**
	 * Sets the common resources to use to have a IO stream
	 * 
	 * @param datasource the resource logical name to set
	 */
	public void setDatasource(String datasource) {
		// checks if the resource is empty
		if (datasource.trim().length() == 0){
			throw new SpringBatchRuntimeException(SpringBatchMessage.JEMS012E);
		}
		this.datasource = datasource;
	}
	
	/**
	 * @return the replaceProperties
	 */
	public boolean isReplaceProperties() {
		return replaceProperties;
	}

	/**
	 * @param replaceProperties the replaceProperties to set
	 */
	public void setReplaceProperties(boolean replaceProperties) {
		this.replaceProperties = replaceProperties;
	}

	/**
	 * This implementation returns whether the underlying file exists.
	 * 
	 * @see java.io.File#exists()
	 */
	public boolean exists() {
		return dataSetImpl.getRealFile().exists();
	}

	/**
	 * This implementation opens a FileInputStream for the real file of dataset.
	 * 
	 * @see java.io.FileInputStream
	 * @return input stream to read
	 * @throws IOException if IO errors occurs
	 */
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(dataSetImpl.getRealFile());
	}

	/**
	 * This implementation returns a URL for the real file of dataset.
	 * 
	 * @see java.io.File#getAbsolutePath()
	 * @return URL representation of real file of dataset
	 */
	public URL getURL() throws IOException {
		return dataSetImpl.getRealFile().toURI().toURL();
	}

	/**
	 * This implementation returns the real file reference of dataset.
	 * 
	 * @return real file
	 */
	public File getFile() {
		return dataSetImpl.getRealFile();
	}

	/**
	 * Do nothing. Not implemented
	 * 
	 * @param relativePath path
	 * @return always null
	 */
	public Resource createRelative(String relativePath) {
		return null;
	}

	/**
	 * This implementation returns the name of the real file.
	 * 
	 * @see java.io.File#getAbsolutePath()
	 * @return file name
	 */
	public String getFilename() {
		return dataSetImpl.getRealFile().getAbsolutePath();
	}

	/**
	 * This implementation returns a description that includes the absolute path
	 * of the file.
	 * 
	 * @see java.io.File#getAbsolutePath()
	 * @return description string
	 */
	public String getDescription() {
		return "file [" + dataSetImpl.getRealFile().getAbsolutePath() + "]";
	}

	/**
	 * This implementation compares the real file names.
	 * 
	 * @param obj a dataset instance
	 * @return <code>true</code> if the real names are equals
	 */
	public boolean equals(Object obj) {
		return obj == this || (obj instanceof DataSet && this.dataSetImpl.getRealName().equals(((DataSet) obj).getDataSetImpl().getRealName()));
	}

	/**
	 * This implementation returns the hash code of the real file reference.
	 * 
	 * @return hash code
	 */
	public int hashCode() {
		return dataSetImpl.hashCode();
	}

	/**
	 * Returns the string representation of data set
	 * 
	 * @return string representation
	 */
	@Override
	public String toString() {
		if (isInline()) {
			return "content=" + textBuffer;
		} else {
			return "dsn=" + name;
		}
	}

}