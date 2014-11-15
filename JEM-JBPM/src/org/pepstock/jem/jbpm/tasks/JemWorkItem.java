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
package org.pepstock.jem.jbpm.tasks;

import java.util.Map;

/**
 * Is a standard interface that you should extend to use the JEM work item.<br>
 * The instance of this interface will be created by workitem and the <code>execute</code> mthod will be performed.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public interface JemWorkItem {
	
	/**
	 * This method is called by workitem, passing the parameters defined in BPMN file
	 * @param parameters parameters defined for this task in BPMN file
	 * @return return code
	 * @throws Exception if any error occurs
	 */
	int execute(Map<String, Object> parameters) throws Exception; 

}
