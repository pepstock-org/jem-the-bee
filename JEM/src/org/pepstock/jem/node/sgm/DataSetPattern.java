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
package org.pepstock.jem.node.sgm;

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Bean used to configure the rule how to assign to a file the right mount point specified on jem-node xml configuration.
 * <br>
 * This bean is used by XStream to serialize and deserialize the rules.
 * The xml root element is called <code>rule</code>.
 * 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 */
@XStreamAlias("rule")
public class DataSetPattern {

	// specified the current path name to use for this rule
	@XStreamAsAttribute
	private String pathName = null;

	// specified the old path name to use for this rule,
	// when the file is in READ
	@XStreamAsAttribute
	private String oldPathName = null;
	
	// implicit collection, named by element as described below.
	@XStreamImplicit(itemFieldName="dataSetPattern")
	private List<String> patterns = new LinkedList<String>();
	
	/**
	 * Empty constructor
	 */
	public DataSetPattern() {
	}

	/**
	 * @return the pathName
	 */
	public String getPathName() {
		return pathName;
	}

	/**
	 * @param pathName the pathName to set
	 */
	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	/**
	 * @return the oldPathNames
	 */
	public String getOldPathName() {
		return oldPathName;
	}

	/**
	 * @param oldPathName the oldPathNames to set
	 */
	public void setOldPathName(String oldPathName) {
		this.oldPathName = oldPathName;
	}

	/**
	 * @return the patterns
	 */
	public List<String> getPatterns() {
		return patterns;
	}

	/**
	 * @param patterns the patterns to set
	 */
	public void setPatterns(List<String> patterns) {
		this.patterns = patterns;
	}
}