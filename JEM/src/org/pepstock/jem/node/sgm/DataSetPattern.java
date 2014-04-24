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
package org.pepstock.jem.node.sgm;

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 */
@XStreamAlias("rule")
public class DataSetPattern {

	@XStreamAsAttribute
	private String pathName = null;

	@XStreamAsAttribute
	private String oldPathName = null;

	
	@XStreamImplicit(itemFieldName="dataSetPattern")
	private List<String> patterns = new LinkedList<String>();
	
	/**
	 * 
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
