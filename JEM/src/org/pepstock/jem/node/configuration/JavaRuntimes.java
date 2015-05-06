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
package org.pepstock.jem.node.configuration;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Is the XML element of JEM node configuration which contains all the JVM installed on the machine 
 * to use when a job needs a specific JVM.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
@XStreamAlias(ConfigKeys.JAVA_RUNTIMES_ALIAS)
public class JavaRuntimes {

	@XStreamImplicit
	private List<Java> javas = new ArrayList<Java>();
	
	/**
	 * @return the javas
	 */
	public List<Java> getJavas() {
		return javas;
	}

	/**
	 * @param javas the javas to set
	 */
	public void setJavas(List<Java> javas) {
		this.javas = javas;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JavaRuntimes [javas=" + javas + "]";
	}

}
