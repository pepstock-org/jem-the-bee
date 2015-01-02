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
package org.pepstock.jem.log;

import java.util.LinkedList;
import java.util.List;

/**
 * Singleton which contains last amount of log records produced by LOG4J.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class LogBuffer {
	
	/**
	 * Default maximum number of log records maintained in the list
	 */
	public static final int DEFAULT_MAXIMUM_ROWS = 100;
	
	private static final LogBuffer BUFFER = new LogBuffer();
    
	private final List<String> logRows = new LinkedList<String>();
	
    private long maxRows = DEFAULT_MAXIMUM_ROWS; 
	
    /**
     * Empty constructor
     */
	private LogBuffer(){
	}

	/**
	 * 
	 * @return log buffer, the static reference
	 */
	public static LogBuffer getInstance(){
		return BUFFER;
	}
	
	/**
	 * Add log record by a string buffer,
	 * 
	 * @param buffer log record
	 */
	public void addLog(StringBuffer buffer){
		// adds record to end
        logRows.add(buffer.toString());
        // if maximum is reachied, removes the first 
        if (logRows.size() > maxRows){
        	logRows.remove(0);
        }
	}
	
	/**
	 * @return the logRows
	 */
	public List<String> getLogRows() {
		return logRows;
	}

	/**
	 * @return the maxRows
	 */
	public long getMaxRows() {
		return maxRows;
	}

	/**
	 * @param maxRows the maxRows to set
	 */
	public void setMaxRows(long maxRows) {
		this.maxRows = maxRows;
	}
	
}
