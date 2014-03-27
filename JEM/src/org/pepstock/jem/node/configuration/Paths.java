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
package org.pepstock.jem.node.configuration;

import org.pepstock.jem.node.sgm.DataPaths;

/**
 * Container with all "paths" definition for many purposes.<br>
 * Contains only strings used during the startup phase to start JEM node.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class Paths {

	private DataPaths data = null;

	private String output = null;

	private String source = null;

	private String binary = null;

	private String classpath = null;

	private String library = null;

	private String persistence = null;

	/**
	 * Empty constructor
	 */
	public Paths() {
	}

	/**
	 * @return the persistence path where the database files for the persistence
	 *         of the jem clustered maps should be stored.
	 */
	public String getPersistence() {
		return persistence;
	}

	/**
	 * Set the persistence path where the database files for the persistence of
	 * the jem clustered maps should be stored.
	 * 
	 * @param persistence the persistence to set
	 */
	public void setPersistence(String persistence) {
		this.persistence = persistence;
	}

	/**
	 * @return the library path where all the native system libraries (like
	 *         .dll, .so), that are needed by the executable files present in
	 *         the binary path, should be stored
	 */
	public String getLibrary() {
		return library;
	}

	/**
	 * Set he library path where all the native system libraries (like .dll,
	 * .so), that are needed by the executable files present in the binary path,
	 * should be stored
	 * 
	 * @param library the library to set
	 */
	public void setLibrary(String library) {
		this.library = library;
	}

	/**
	 * @return the classpath path where all the library (jars, zip, etc...)
	 *         relative to an execution of a jcl should be stored
	 */
	public String getClasspath() {
		return classpath;
	}

	/**
	 * Set the classpath path where all the library (jars, zip, etc...) relative
	 * to an execution of a jcl should be stored
	 * 
	 * @param classpath the classpath to set
	 */
	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}

	/**
	 * @return the binary path where all the executable files, like .exe files,
	 *         that are called at runtime should be stored.
	 * 
	 */
	public String getBinary() {
		return binary;
	}

	/**
	 * Set the binary path where all the executable files, like .exe files, that
	 * are called at runtime should be stored.
	 * 
	 * @param binary the binary to set
	 */
	public void setBinary(String binary) {
		this.binary = binary;
	}

	/**
	 * Returns a container of paths where the datasets and files should be stored.
	 * 
	 * @return container of paths where the datasets and files should be stored
	 */
	public DataPaths getData() {
		return data;
	}

	/**
	 * Sets container of paths where the datasets and files should be stored.
	 * 
	 * @param data container of paths where the datasets and files should be stored
	 */
	public void setData(DataPaths data) {
		this.data = data;
	}

	/**
	 * Returns the path where the stored all output files of job execution.
	 * 
	 * @return the path where the stored all output files of job execution
	 */
	public String getOutput() {
		return output;
	}

	/**
	 * Sets the path where the stored all output files of job execution.
	 * 
	 * @param output the path where the stored all output files of job execution
	 */
	public void setOutput(String output) {
		this.output = output;
	}

	/**
	 * @return the source the path where should be stores all the files that can
	 *         be considered sources and that can be called at runtime.
	 *         <p>
	 *         e.g.
	 *         <p>
	 *         Peaces of jcl to be included at runtime in an "ant" jcl
	 *         <p>
	 *         Peaces of jcl to be included at runtime in a "spring" jcl
	 *         <p>
	 *         etc.
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Set the path where should be stores all the files that can be considered
	 * sources and that can be called at runtime.
	 * <p>
	 * e.g.
	 * <p>
	 * Peaces of jcl to be included at runtime in an "ant" jcl
	 * <p>
	 * Peaces of jcl to be included at runtime in a "spring" jcl
	 * <p>
	 * etc.
	 * 
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * Returns the string representation of paths
	 * 
	 * @return string representation of paths
	 */
	@Override
	public String toString() {
		return "[paths: output=" + ((output != null) ? output : "N/A") + ", data=" + ((data != null) ? data : "N/A") + "]";
	}

}