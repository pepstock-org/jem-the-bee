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
package org.pepstock.jem.jppf;

/**
 * Container of quick JPPF configuration.
 * <br>
 * Used in SpringBatch to configure JPPF.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public final class JPPFBean {

	private String runnable = null;

	private String address = null;

	private int parallelTaskNumber = Keys.DEFAULT_PARALLEL_TASK_NUMBER;
	
	private String datasource = null;
	
	private String delimiter = null;
	
	private String delimiterString = null;
	
	private String chunkableDataDescription = null;
	
	private String mergedDataDescription = null;

	/**
	 * @return the runnable
	 */
	public String getRunnable() {
		return runnable;
	}

	/**
	 * @param runnable the runnable to set
	 */
	public void setRunnable(String runnable) {
		this.runnable = runnable;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the parallelTaskNumber
	 */
	public int getParallelTaskNumber() {
		return parallelTaskNumber;
	}

	/**
	 * @param parallelTaskNumber the parallelTaskNumber to set
	 */
	public void setParallelTaskNumber(int parallelTaskNumber) {
		this.parallelTaskNumber = parallelTaskNumber;
	}

	/**
	 * @return the datasource
	 */
	public String getDatasource() {
		return datasource;
	}

	/**
	 * @param datasource the datasource to set
	 */
	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	/**
	 * @return the delimiter
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * @param delimiter the delimiter to set
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * @return the delimiterString
	 */
	public String getDelimiterString() {
		return delimiterString;
	}

	/**
	 * @param delimiterString the delimiterString to set
	 */
	public void setDelimiterString(String delimiterString) {
		this.delimiterString = delimiterString;
	}

	/**
	 * @return the chunkableDataDescription
	 */
	public String getChunkableDataDescription() {
		return chunkableDataDescription;
	}

	/**
	 * @param chunkableDataDescription the chunkableDataDescription to set
	 */
	public void setChunkableDataDescription(String chunkableDataDescription) {
		this.chunkableDataDescription = chunkableDataDescription;
	}

	/**
	 * @return the mergedDataDescription
	 */
	public String getMergedDataDescription() {
		return mergedDataDescription;
	}

	/**
	 * @param mergedDataDescription the mergedDataDescription to set
	 */
	public void setMergedDataDescription(String mergedDataDescription) {
		this.mergedDataDescription = mergedDataDescription;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JPPFBean [runnable=" + runnable + ", address=" + address + ", parallelTaskNumber=" + parallelTaskNumber + ", datasource=" + datasource + ", delimiter=" + delimiter + ", delimiterString=" + delimiterString + ", chunkableDataDescription="
				+ chunkableDataDescription + ", mergedDataDescription=" + mergedDataDescription + "]";
	}	
}