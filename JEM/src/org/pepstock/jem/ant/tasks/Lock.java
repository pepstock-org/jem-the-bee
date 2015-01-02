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
package org.pepstock.jem.ant.tasks;

import org.apache.tools.ant.BuildException;
import org.pepstock.jem.ant.AntMessage;

/**
 * Represents a lock definition. A lock is a string (a tag) used inside of Hazelcast 
 * by LOCK structure of it.<br>
 * This lock is always in exclusive mode.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class Lock {
	
	private String name = null;

	/**
	 * Empty constructor
	 */
	public Lock() {
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		// checks if the name is empty
		if (name.trim().length() == 0){
			throw new BuildException(AntMessage.JEMA023E.toMessage().getFormattedMessage());
		}
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Lock [name=" + name + "]";
	}
	
}