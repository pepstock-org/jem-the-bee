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
package org.pepstock.jem.node.affinity;

import java.io.File;
import java.io.IOException;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public interface ScriptAffinityLoader extends AffinityLoader {
	
	/**
	 * Called to have the list of affinities and memory, using the system information if needed.
	 * 
	 * @param script file of script 
	 * @param info system information
	 * @return result object with all affinities and memory
	 * @throws IOException if an error occurs
	 */
	Result runScript(File script, SystemInfo info) throws IOException;
	
	/**
	 * Returns the script file defined by property.
	 * @return script file
	 */
	File getScriptFile();
	
	/**
	 * Returns the script type. I.e. groovy or javascript
	 * @return script file
	 */
	String getScriptType();
	
	/**
	 * Called to have the list of affinities and memory, using the system information if needed, for test.
	 * 
	 * @param script content of script 
	 * @param info system information
	 * @return result object with all affinities and memory
	 * @throws IOException if an error occurs
	 */
	Result testScript(String script, SystemInfo info) throws IOException;
	
	
}
