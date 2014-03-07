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
package org.pepstock.jem.node.executors.nodes;

import java.util.List;

import org.pepstock.jem.log.LogBuffer;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;


/**
 * Returns last N log records, using Log4J appender specific to store last records in memory.
 * 
 * @see LogBuffer
 * @author Andrea "Stock" Stocchero
 * @version 1.3	
 *
 */
public class GetLog extends DefaultExecutor<String> {

	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.executors.DefaultExecutor#execute()
	 */
	@Override
	public String execute() throws ExecutorException {
		StringBuilder sb = new StringBuilder();
		// gets log records from log buffer
		List<String> log = LogBuffer.getInstance().getLogRows();
		for (String record : log){
			sb.append(record);
		}
		return sb.toString();
	}
}